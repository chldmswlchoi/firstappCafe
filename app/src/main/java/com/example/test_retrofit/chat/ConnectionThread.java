package com.example.test_retrofit.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.test_retrofit.Retrofit.NetWorkHelper;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnectionThread extends Thread{

    private final String TAG = this.getClass().getSimpleName();
    private ThreadReceiver receiverThread;
    private Socket socket;
    private String nickname,id;
    private Handler chatHandler;
    private int id_meeting;

    private long now;
    private Date date;
    private SimpleDateFormat Dsdf, sdf;
    private String getTime,getDetailTime;

    //이미 모임에 참여한 멤버인지 여부를 알 수 있게 하는 변수
    private String isMember = "no";
    private List<DTOChatList.ChatData> chatDataList;
    private Context context;

    ConnectionThread(String nickname,String id, Handler chatHandler, String isMember, Context context){
        this.nickname = nickname;
        this.id = id;
        this.chatHandler= chatHandler;
//        this.id_meeting = id_meeting; //모임 고유 아이디 값 -> 채팅방 방나눌 때 기준이 되는 숫자임
        this.isMember = isMember;
        this.context = context;


    }

    public void run(){
        chatDataList = new ArrayList<>();
        getChatList();
        try {
            sleep(1000);
            Log.e(TAG,"try");
            //로컬 호스트의 주소를 가져온다.
            socket = new Socket("43.200.106.233",9000);
            Log.e(TAG,"socket = new Socket");
            ActivityChatRoom.mySocket = socket;
            // 클라이언트의 소켓 객체 생성
            Log.e(TAG,"서버 접속 성공");
            Log.e(TAG,"sendThread에 닉네임 넘겨주기");
            now = System.currentTimeMillis();
            date = new Date(now);
            Dsdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss", Locale.KOREA);
            sdf = new SimpleDateFormat("a hh:mm", Locale.KOREA);
            getDetailTime = Dsdf.format(date);
            getTime= sdf.format(date);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder roomNumbers = new StringBuilder();
            for(int i =0; i<chatDataList.size();i++) { //내가 참여한 채팅방 아이디들을 roomNumber에 저장해줌
                Log.e(TAG,"chatDataList.size() : " + chatDataList.size());
                Log.e(TAG,"chatDataList.get(i).getId_meeting() : " + chatDataList.get(i).getId_meeting());
                String id_meeting = String.valueOf(chatDataList.get(i).getId_meeting());
                if(i==(chatDataList.size()-1)){
                    Log.e(TAG,"id_meeting : " + id_meeting);
                    roomNumbers.append(id_meeting);
                    Log.e(TAG,"roomNumbers : " + roomNumbers);
                }
                else {

                    Log.e(TAG,"id_meeting : " + id_meeting);
                    roomNumbers.append(id_meeting).append(",");
                    Log.e(TAG,"roomNumbers : " + roomNumbers);
                }

            }
            Log.e(TAG, String.valueOf(roomNumbers));
            JSONObject sending = new JSONObject();
            sending.put("nickname", nickname);
            sending.put("date", getTime);
            sending.put("detail_date", getDetailTime);
            sending.put("message", String.valueOf(roomNumbers));
            sending.put("isMember", isMember);
            sending.put("sender", id);
            writer.println(sending);
            writer.flush();
            //메시지를 받는 스레드 생성 및 시작
            receiverThread = new ThreadReceiver(socket,chatHandler,context);
            receiverThread.start();

            //join은 메인 스레드에서 thread1 & thread2가 종료(완료) 될 때 까지
            // 기다려야 할 때 사용한다.
//                socket.close();
        } catch (Exception e) {
            Log.e(TAG,"에러");
            Log.e(TAG,e.toString());
        }

    }



    void getChatList() {

        Call<DTOChatList> call = NetWorkHelper.getInstance().getApiService().getChatList(id);
        call.enqueue(new Callback<DTOChatList>() {
            @Override
            public void onResponse(Call<DTOChatList> call, Response<DTOChatList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "서버에서 받아온 값 형태 response + r.body : \n" + response + "/"+ response.body());
                    DTOChatList dtoChat = response.body();
                    Log.e(TAG, "응답 성공 후 DTOChat 파싱해준 값1" +dtoChat);

                    if (dtoChat.getStatus().equals("true")) {
                        chatDataList.addAll(dtoChat.getData()) ;
                        Log.e(TAG, "응답 성공 후 ChatData 파싱해준 값2" + chatDataList);

                    } else {
                        Log.e(TAG,"서버에서 오류");
                    }
                } else {
                    Log.e(TAG, "실패" + response.body()+ response);
                }
            }
            @Override
            public void onFailure(Call<DTOChatList> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });
    }


}
