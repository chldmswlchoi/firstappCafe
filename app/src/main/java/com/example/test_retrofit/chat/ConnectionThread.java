package com.example.test_retrofit.chat;

import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    ConnectionThread(String nickname,String id, Handler chatHandler, int id_meeting, String isMemgber){
        this.nickname = nickname;
        this.id = id;
        this.chatHandler= chatHandler;
        this.id_meeting = id_meeting; //모임 고유 아이디 값 -> 채팅방 방나눌 때 기준이 되는 숫자임
        this.isMember = isMemgber;

    }

    public void run(){

            try {
                Log.e(TAG,"try");

                //로컬 호스트의 주소를 가져온다.
                socket = new Socket("3.39.153.170",9000);
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
                JSONObject sending = new JSONObject();
                sending.put("nickname",nickname);
                sending.put("date",getTime);
                sending.put("detail_date",getDetailTime);
                sending.put("id_meeting",id_meeting);
                sending.put("isMember",isMember);
                sending.put("sender",id);
                writer.println(sending);
                writer.flush();
                //메시지를 받는 스레드 생성 및 시작
                receiverThread = new ThreadReceiver(socket,chatHandler);
                receiverThread.start();

                //join은 메인 스레드에서 thread1 & thread2가 종료(완료) 될 때 까지
                // 기다려야 할 때 사용한다.
//                socket.close();
            } catch (Exception e) {
                Log.e(TAG,"에러");
                Log.e(TAG,e.getMessage());
            }

    }
}
