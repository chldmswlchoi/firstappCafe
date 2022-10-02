package com.example.test_retrofit.chat;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.customview.widget.Openable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test_retrofit.R;
import com.example.test_retrofit.Retrofit.NetWorkHelper;
import com.example.test_retrofit.group.ActivityGroupContent;
import com.example.test_retrofit.group.retrofit_Interface.Interface_cancelJoinGroup;
import com.example.test_retrofit.user.ApiClient;
import com.example.test_retrofit.user.PreferenceHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityChatRoom extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Button register;
    private EditText chat;
    private ImageView image, menu, back, close, out;
    private RecyclerView recyclerView, userListRecyclerView;
    private TextView title;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    //소켓 생성하고 서버와 연결하고, 송수신 스레드 생성및 호출
    private ConnectionThread thread;
    private PreferenceHelper preferenceHelper;
    static Socket mySocket;

    //리사이클러뷰 어댑터 관련 채팅
    private ArrayList<DTOChat> chatList;
    private LinearLayoutManager linearLayoutManager;
    private AdapterChat adapterChat;

    //리사이클러뷰 어댑터 관련 유저 리스트
    private ArrayList<DTOChat> userList;
    private LinearLayoutManager UserlinearLayoutManager;
    private AdapterUserList adapterUserList;

    private Handler chatHandler;
    private Gson gson;

    private final int HANDLERWHAT = 1111;

    //이미 모임에 참여한 멤버인 경우 -> 채팅방 리스트 클릭을 통해 채팅방에 입장한 경우
    private String isMember, ChatTitle, host;

    //방나누기 할 때 필요한 모임 식별값
    private int id_meeting;
    private String login_id;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        register = findViewById(R.id.register);
        chat = findViewById(R.id.chat);
        image = findViewById(R.id.image);
        back = findViewById(R.id.back);
        menu = findViewById(R.id.menu);
        recyclerView = findViewById(R.id.chat_list);
        userListRecyclerView = findViewById(R.id.userList);
        title = findViewById(R.id.title);
        close = findViewById(R.id.close);
        out = findViewById(R.id.out);
        preferenceHelper = new PreferenceHelper(this);
        id_meeting = getIntent().getIntExtra("id_meeting", -1);
        isMember = getIntent().getStringExtra("isMember");
        ChatTitle = getIntent().getStringExtra("title");
        host = getIntent().getStringExtra("host");
        login_id = preferenceHelper.getID();
        gson = new Gson();
        chatList = new ArrayList<>();

        //어댑터와 리사이클러뷰 바인드 해주는 과정
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterChat = new AdapterChat(chatList);
        recyclerView.setAdapter(adapterChat);

        userList = new ArrayList<>();
        UserlinearLayoutManager = new LinearLayoutManager(this);
        userListRecyclerView.setLayoutManager(UserlinearLayoutManager);
        adapterUserList = new AdapterUserList(userList);
        userListRecyclerView.setAdapter(adapterUserList);

        Log.e(TAG, "onCreate");


        //소켓으로 받은 채팅 메시지를 리사이클려뷰에 아이템 추가해서 ui 변경해주기
        chatHandler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == HANDLERWHAT) {
                    //메시지가 왔다면
                    Log.e("handleMessage ", msg.obj.toString());
                    String handleMessage = msg.obj.toString();
                    DTOChat getChat = gson.fromJson(handleMessage, DTOChat.class);
                    Log.e(TAG, handleMessage);

                    if (getChat.getView_type() == ViewType.CENTER_JOIN) { // 입장,나감 또는 퇴장일 경우 viewType =0
                        chatList.add(new DTOChat(getChat.getMessage(), getChat.getNickname(), getChat.getDate()
                                , getChat.getView_type(), getChat.getProfile_name()));
                        adapterChat.notifyItemInserted(chatList.size());
                        recyclerView.scrollToPosition(chatList.size() - 1);
                        if(-2==getChat.getRemoveUserList())
                        {
                            if(login_id.equals(getChat.getExit_id()))
                            {
                                ActivityChatRoom.super.onBackPressed();
                            }
                        }
                        getUserList();


                    } else if (getChat.getView_type() == ViewType.RIGHT_CHAT) { // 내가 보낸 메시지인 경우 viewtype = 1
                        Log.e("handleMessage ", "RightChat");
                        chatList.add(new DTOChat(getChat.getMessage(), getChat.getNickname(), getChat.getDate()
                                , getChat.getView_type(), getChat.getProfile_name()));
                        adapterChat.notifyItemInserted(chatList.size());
                        recyclerView.scrollToPosition(chatList.size() - 1);

                    } else if (getChat.getView_type() == ViewType.LEFT_CHAT) { // 다른 사람이 보낸 메시지인 경우 viewtype =2
                        Log.e("handleMessage ", "LeftChat");
                        chatList.add(new DTOChat(getChat.getMessage(), getChat.getNickname(), getChat.getDate()
                                , getChat.getView_type(), getChat.getProfile_name()));
                        adapterChat.notifyItemInserted(chatList.size());
                        recyclerView.scrollToPosition(chatList.size() - 1);
                    } else {
                        Log.e("handleMessage ", "CenterTime");
                        chatList.add(new DTOChat(getChat.getMessage(), getChat.getNickname(), getChat.getDate()
                                , getChat.getView_type(), getChat.getProfile_name()));
                        adapterChat.notifyItemInserted(chatList.size());
                        recyclerView.scrollToPosition(chatList.size() - 1);
                    }
                }
            }
        };

        if (!"yes".equals(isMember)) {
            isMember = "no";
        }
        getMessages(); // db에 저장된 채팅 메시지 불러옴


        register.setOnClickListener(new View.OnClickListener() { // 채팅 보내기 버튼 클릭 시
            @Override
            public void onClick(View v) {
                if ("bye".equals(chat.getText().toString())) {

                    Log.e(TAG, "나가기 실행");
                    ThreadSender threadSender = new ThreadSender(mySocket,
                            chat.getText().toString(), preferenceHelper.getProfile());
                    threadSender.start();
                } else {
                    if (!chat.getText().toString().isEmpty()) {
                        ThreadSender threadSender = new ThreadSender(mySocket,
                                chat.getText().toString(), preferenceHelper.getProfile());
                        threadSender.start();
                        chat.setText("");
                    }
                }
            }
        });

        adapterUserList.setOnItemClickListener(new AdapterUserList.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }

            @Override
            public void onExitClick(View v, int position) { //롱 클릭 시 모임 생성자만 유저 퇴장 가능
                if (host.equals(login_id) && !host.equals(userList.get(position).getId())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle(userList.get(position).getNickname() + "님을 퇴장 시키겠습니까?")
                            .setMessage("퇴장된 유저는 다시 모임 참여가 불가능합니다.")
                            .setPositiveButton("네",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            cancelGroupJoin(1, userList.get(position).getId(),userList.get(position).getNickname());
                                            userList.remove(position);
                                            Log.e(TAG, "userList.remove");
                                            adapterUserList.notifyItemRemoved(position);
                                            Log.e(TAG, "adapterUserList.notifyItemRemoved");
                                        }
                                    })
                            .setNeutralButton("아니요", null).show();
                }
            }
        });

        //----------------------------  클릭 이벤트 관련 코드 ------------------------------------

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END); // 메뉴 버튼 클릭시 drawr 오픈
                getUserList(); // 모임에 참여한 유저 리스트 불러옴
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.END);// x 버튼 클릭시 drawr 닫음
            }
        });

        back.setOnClickListener(new View.OnClickListener() { // 뒤로가기 이미지 클릭시 뒤로감
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        out.setOnClickListener(new View.OnClickListener() { //나가기 클릭시 채팅방을 나가게 됨
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("나가기")
                        .setMessage("채팅방을 나가면 모임 참여 취소가 됩니다. 채팅방을 나가시겠습니까?")
                        .setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cancelGroupJoin(0, login_id,null);
                            }
                        })
                        .setNeutralButton("아니요", null).show();
            }
        });
    }


    //---------------------------레트로핏 통신 & 리사이클러뷰에 아이템 추가 하는 함수 -------------------------------



    public void cancelGroupJoin(int exit, String exit_id,String exitNickname) { // exit는 서버에서 퇴장인지 아닌지 여부를 결정하기 위해 쓰인다.
        Log.e(TAG, "cancelGroupJoin // " + exit);
        Interface_cancelJoinGroup interface_cancelJoinGroup = ApiClient.getApiClient().create(Interface_cancelJoinGroup.class);
        Call<String> call = interface_cancelJoinGroup.cancelJoinGroup(id_meeting, exit_id, exit);

        Log.e(TAG, "응답2 =" + id_meeting + exit_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "응답1 =" + response.body());
                Log.e(TAG, "응답2 =" + response);
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("onSuccess", String.valueOf(response.body()));
                    String jsonResponse = response.body();
                    ISDeleteDB_enterMeeting(jsonResponse,exitNickname,exit_id);
                }

            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }


    //    db에 모임 참여 취소 저장이 되었는지 여부 따지는 함수
    private void ISDeleteDB_enterMeeting(String response,String nickname,String exit_id) {
        try {
            Log.e(TAG, "ISDeleteDB_enterMeeting");
            JSONObject jsonObject = new JSONObject(response);
            String message = jsonObject.getString("message");
            if (0 == jsonObject.getInt("exit")) { // 유저가 스스로 나가기 해서 모임을 나간 경우
                if (jsonObject.getString("status").equals("true")) {
                    ThreadSender sendOut = new ThreadSender(mySocket, "bye", preferenceHelper.getProfile());
                    sendOut.start(); //ThreadSener 은 bye를 입력 받으면 나간다는 메시지를 서버에게 보내고 소켓을 종료한다.
                    Toast.makeText(ActivityChatRoom.this, message, Toast.LENGTH_LONG).show();
                    super.onBackPressed();

                } else {
                    Toast.makeText(ActivityChatRoom.this, message, Toast.LENGTH_LONG).show();
                }
//                참여 취소 실패 다시 시도하세요 문구는 -> db에 삭제가 안된것임
            }

            else {
                Log.e(TAG, "ISDeleteDB_enterMeeting // 퇴장");
                ThreadSenderExit sendExit = new ThreadSenderExit(mySocket,nickname+"님이 퇴장되었습니다",exit_id);
                sendExit.start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getUserList() { // drawer Layout 에 채팅방 모임 참여자 리스트를 서버에 요청하고 받는다.
        Call<List<DTOChat>> call = NetWorkHelper.getInstance().getApiService().getUserList(id_meeting);
        call.enqueue(new Callback<List<DTOChat>>() {
            @Override
            public void onResponse(Call<List<DTOChat>> call, Response<List<DTOChat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "getUserList response + r.body : \n" + response + "/" + response.body());
                    List<DTOChat> data = response.body();
                    generateUserList((List<DTOChat>) data);

                } else {
                    Log.e(TAG, "getUserList 실패" + response.body() + response);
                }
            }

            @Override
            public void onFailure(Call<List<DTOChat>> call, Throwable t) {
                Log.e(TAG, "getUserList 응답에러 = " + t.getMessage());
            }
        });
    }


    private void generateUserList(List<DTOChat> items) { // 받아온 채팅 리스트를 리사이클러뷰에 추가해준 뒤 서버와 소켓통신함
        Log.e(TAG, "generateUserList");
        userList.clear();
        userList.addAll(items);
        adapterUserList.notifyDataSetChanged();
        title.setText(ChatTitle);
    }


    void getMessages() { // 과거 채팅 목록 서버에 요청해서 받아오는 함수
        Call<List<DTOChat>> call = NetWorkHelper.getInstance().getApiService().getChatMessages(login_id, id_meeting);
        call.enqueue(new Callback<List<DTOChat>>() {
            @Override
            public void onResponse(Call<List<DTOChat>> call, Response<List<DTOChat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "서버에서 받아온 값 형태 response + r.body : \n" + response + "/" + response.body());
                    List<DTOChat> data = response.body();
                    generateDataList((List<DTOChat>) data);

                } else {
                    Log.e(TAG, "실패" + response.body() + response);
                }
            }

            @Override
            public void onFailure(Call<List<DTOChat>> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void generateDataList(List<DTOChat> items) { // 받아온 채팅 리스트를 리사이클러뷰에 추가해준 뒤 서버와 소켓통신함
        Log.e(TAG, "generateDataList");
        chatList.addAll(items);
        adapterChat.notifyDataSetChanged();
        recyclerView.scrollToPosition(chatList.size()-1);
        thread = new ConnectionThread(preferenceHelper.getNickname(), preferenceHelper.getID(), chatHandler, id_meeting, isMember);
        thread.start();
    }


    // --------------------------------- 네비게이션 바 관련 코드 -------------------------------------------------


    @Override
    public void onBackPressed() { //뒤로가기 했을 때
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    //---------------------------------------- 생명주기 관련 코드 -------------------------------------------------
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
        try {// 뒤로가기 했을 때 소켓 닫아준다.
            if (mySocket != null) {
                mySocket.close();
            }
            ;
            //소켓과 관련된 outstream, inputstrem이 닫아지고 -> 스트림이 종료되면서 관련 스레드도 종료됨
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }


    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");

    }

    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart");
        isMember = "yes";
        getMessages();


    }

}