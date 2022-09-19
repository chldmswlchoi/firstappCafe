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

                    chatList.add(new ItemChat(type[2],type[1],
                            "오후 2:00",Integer.parseInt(type[0])));
                    adapterChat.notifyItemInserted(chatList.size());
                    recyclerView.scrollToPosition(chatList.size()-1);
                }

            }
        };

        if(true) {
            thread = new ConnectionThread(preferenceHelper.getNickname(),chatHandler,id_meeting);
            thread.start();
        } else {Log.e(TAG,"isConnect == True");}

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
                                chat.getText().toString() + "#");
                        threadSender.start();
                        chat.setText("");
                    }
                }


            }
        });
    }

}