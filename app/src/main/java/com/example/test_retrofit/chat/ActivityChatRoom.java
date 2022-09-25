package com.example.test_retrofit.chat;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.test_retrofit.R;
import com.example.test_retrofit.user.PreferenceHelper;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ActivityChatRoom extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Button register;
    private EditText chat;
    private ImageView image;
    private RecyclerView recyclerView;


    //소켓 생성하고 서버와 연결하고, 송수신 스레드 생성및 호출
    private ConnectionThread thread;
    private PreferenceHelper preferenceHelper;
    static Socket mySocket;

    //리사이클러뷰 어댑터 관련
    private ArrayList<ItemChat> chatList;
    private LinearLayoutManager linearLayoutManager;
    private AdapterChat adapterChat;

    private Handler chatHandler;

    private final int HANDLERWHAT = 1111;

    //이미 모임에 참여한 멤버인 경우 -> 채팅방 리스트 클릭을 통해 채팅방에 입장한 경우
    private String isMember;

    //방나누기 할 때 필요한 모임 식별값
    private int id_meeting;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        register = findViewById(R.id.register);
        chat = findViewById(R.id.chat);
        image = findViewById(R.id.image);
        recyclerView = findViewById(R.id.chat_list);
        preferenceHelper = new PreferenceHelper(this);
        chatList = new ArrayList<>();
        id_meeting = getIntent().getIntExtra("id_meeting",-1);

        //어댑터와 리사이클러뷰 바인드 해주는 과정
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterChat = new AdapterChat(chatList);
        recyclerView.setAdapter(adapterChat);

        isMember = getIntent().getStringExtra("isMember");
        Log.e(TAG,"onCreate");


        //소켓으로 받은 채팅 메시지를 리사이클려뷰에 아이템 추가해서 ui 변경해주기
        chatHandler = new Handler(Looper.getMainLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == HANDLERWHAT){
                    //메시지가 왔다면
                    Log.e("handleMessage ",msg.obj.toString());
                    String [] type = msg.obj.toString().split("#");
                    Log.e(TAG, Arrays.toString(type));

                    if(Integer.parseInt(type[0]) == ViewType.CENTER_JOIN){ // 입장 또는 퇴장일 경우 viewType =0
                        chatList.add(new ItemChat(type[1],null,
                                null,Integer.parseInt(type[0]),null));
                        adapterChat.notifyItemInserted(chatList.size());
                        recyclerView.scrollToPosition(chatList.size()-1);
                    }

                    else if (Integer.parseInt(type[0]) == ViewType.RIGHT_CHAT)
                    { // 내가 보낸 메시지인 경우 viewtype = 1
                        Log.e("handleMessage ","RightChat");
                        chatList.add(new ItemChat(type[2],null,
                                type[4],Integer.parseInt(type[0]),null));
                        adapterChat.notifyItemInserted(chatList.size());
                        recyclerView.scrollToPosition(chatList.size()-1);
                    }

                    else { // 다른 사람이 보낸 메시지인 경우 viewtype =2
                        Log.e("handleMessage ","LeftChat");
                        chatList.add(new ItemChat(type[2], type[1],
                                type[4], Integer.parseInt(type[0]), type[3]));
                        adapterChat.notifyItemInserted(chatList.size());
                        recyclerView.scrollToPosition(chatList.size() - 1);
                    }
                }
            }
        };

        if(!"yes".equals(isMember)) {
            isMember = "no";
        }
            thread = new ConnectionThread(preferenceHelper.getNickname(),preferenceHelper.getID(),chatHandler,id_meeting,isMember);
            thread.start();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("bye".equals(chat.getText().toString())){
                    try {
                        mySocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    if(!chat.getText().toString().isEmpty()) {
                        ThreadSender threadSender = new ThreadSender(mySocket,
                                chat.getText().toString() ,preferenceHelper.getProfile());
                        threadSender.start();
                        chat.setText("");
                    }
                }


            }
        });
    }

}