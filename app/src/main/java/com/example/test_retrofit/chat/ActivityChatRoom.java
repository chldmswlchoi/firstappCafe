package com.example.test_retrofit.chat;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test_retrofit.R;
import com.example.test_retrofit.Retrofit.NetWorkHelper;
import com.example.test_retrofit.group.retrofit_Interface.Interface_cancelJoinGroup;
import com.example.test_retrofit.user.ApiClient;
import com.example.test_retrofit.user.PreferenceHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    static Socket mySocket = null;

    //리사이클러뷰 어댑터 관련 채팅
    private ArrayList<DTOChat> chatList;
    private LinearLayoutManager linearLayoutManager;
    private AdapterChat adapterChat;

    //리사이클러뷰 어댑터 관련 유저 리스트
    private ArrayList<DTOChat> userList;
    private LinearLayoutManager UserlinearLayoutManager;
    private AdapterUserList adapterUserList;

    static Handler chatHandler;
    private Gson gson;

    private final int HANDLER_WHAT = 1111;

    //이미 모임에 참여한 멤버인 경우 -> 채팅방 리스트 클릭을 통해 채팅방에 입장한 경우
    private String isMember, ChatTitle;
    static String host;

    //방나누기 할 때 필요한 모임 식별값
    static Integer id_meeting;
    private String login_id;

    //이미지 선택 관련
    private Integer IMAGERESULT = 4165;
    //이미지 선택 후 안드로이드 갤러리 상대경로 uri 담는 리스트
    ArrayList<Uri> uriList = new ArrayList<>();
    //이미지 상대경로를 절대 경로(sd 카드의 경로)로 변경 후 담는 리스트
    private ArrayList<String> imagePath = new ArrayList<>();
    private HashMap<String, RequestBody> map;
    private ArrayList<String> imageNameArray = new ArrayList<>();
    private long now;
    private Date date;
    private SimpleDateFormat Dsdf, sdf;
    private String getTime, getDetailTime;


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


        //소켓으로 받은 채팅 메시지를 리사이클러뷰에 아이템 추가해서 ui 변경해주기
        chatHandler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == HANDLER_WHAT) {
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
                        if (-2 == getChat.getRemoveUserList()) {
                            if (login_id.equals(getChat.getExit_id())) {
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
                    } else if (getChat.getView_type() == ViewType.CENTER_TIME) {
                        Log.e("handleMessage ", "CenterTime");
                        chatList.add(new DTOChat(getChat.getMessage(), getChat.getNickname(), getChat.getDate()
                                , getChat.getView_type(), getChat.getProfile_name()));
                        adapterChat.notifyItemInserted(chatList.size());
                        recyclerView.scrollToPosition(chatList.size() - 1);
                    } else if (getChat.getView_type() == ViewType.LEFT_IMAGE) {
                        Log.e("handleMessage ", "LEFT_IMAGE");
                        chatList.add(new DTOChat(getChat.getMessage(), getChat.getNickname(), getChat.getDate()
                                , getChat.getView_type(), getChat.getProfile_name()));
                        adapterChat.notifyItemInserted(chatList.size());
                        recyclerView.scrollToPosition(chatList.size() - 1);
                    } else {
                        Log.e("handleMessage ", "RIGHT_IMAGE");
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
//        connectSocket();


        //갤러리 들어가는 버튼
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                //setType은 데이터가 아닌 특정한 유형인 인텐트를 생성하기 위해 사용된다.
                //예를 들어 반환할 데이터 유형을 표시하기 위해,
                //또한 이 메서드는 이전에 설정된 모든 데이터를 자동으로 지운다.
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                //EXTRA_ALLOW_MULTIPLE 은 사용자가 여러 항목을 선택하고 반환하도록 허용한다.
                //getClipData() 부분으로 반환된다.
                intent.setAction(Intent.ACTION_PICK);

                startActivityForResult(intent, IMAGERESULT);
            }
        });


        register.setOnClickListener(new View.OnClickListener() { // 채팅 보내기 버튼 클릭 시
            @Override
            public void onClick(View v) {
                if ("bye".equals(chat.getText().toString())) {

                    Log.e(TAG, "나가기 실행");
                    ThreadSender threadSender = new ThreadSender(mySocket,
                            chat.getText().toString(), preferenceHelper.getProfile(),id_meeting);
                    threadSender.start();
                } else {
                    if (!chat.getText().toString().isEmpty()) {
                        ThreadSender threadSender = new ThreadSender(mySocket,
                                chat.getText().toString(), preferenceHelper.getProfile(),id_meeting);
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
                                            cancelGroupJoin(1, userList.get(position).getId(), userList.get(position).getNickname());
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

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "chat");
                recyclerView.smoothScrollToPosition(chatList.size() - 1);
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
                                        cancelGroupJoin(0, login_id, null);
                                    }
                                })
                        .setNeutralButton("아니요", null).show();
            }
        });
    }

    //----------------------- 이미지 선택 후 결과 값 받는 부분 onActivityResult ----------------------------

    //결과값 받는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 인자 설명 1: 액티비티 식별값, 2:setResult()에서 보낸 값,
        // 3: putExtra() 를 통해 인텐트에서 보내준 값

        Log.e(TAG, "onActivityResult 시작");
        if (requestCode == IMAGERESULT) {
            if (resultCode == RESULT_OK) {
                uriList.clear();
                imagePath.clear();
                //이미지를 하나라도 선택한 경우
                if (data.getClipData() == null) {
                    Log.e("single choice: ", String.valueOf(data.getData()));
                    Uri imageUri = data.getData();
                    uriList.add(imageUri);
                    postImage();
                }
                // 이미지를 여러장 선택한 경우
                else {
                    ClipData clipData = data.getClipData();
                    Log.e("clipData: 사진이 몇개 반환되었는지", String.valueOf(clipData.getItemCount()));

                    if (clipData.getItemCount() > 6) {  //선택한 이미지가 4장 이상인 경우
                        Toast.makeText(getApplicationContext(), "사진은 6장까지만 선택 가능합니다.", Toast.LENGTH_LONG).show();
                    } else {  //선택한 이미지가 1장 이상 6장 이하인 경우
                        Log.e(TAG, "multiple choice");

                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri imageUri = clipData.getItemAt(i).getUri();
                            //선택한 이미지의 uri 를 가져온다.
                            try {
                                uriList.add(imageUri); // uri를 list에 담는다
                            } catch (Exception e) {
                                Log.e(TAG, "File select error", e);
                            }
                        }
                    }

                    postImage();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                //이미지 선택 취소시 호출할 행동 쓰기
                Toast.makeText(this, TAG + "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }

    }

    //image 의 절대경로를 가져오는 메소드 실제 sd카드의 경로를 리턴해주는 함수이다.
    private ArrayList<String> getRealPathFromURL(ArrayList<Uri> uri) {
        Log.e(TAG, "getRealPathFromURL : " + uri.size());
        Cursor cursor = null;
        //쿼리한 데이터를 순회회하는 역할을 cursor가 한다.
        //cursor에는 쿼리할때 projection으로 요청한 컬럼들이 포함되어 있다. 즉 db의 칼럼들이 저장되어 있다.
        try {
            for (Integer i = 0; i < uri.size(); i++) {
                String[] proj = {MediaStore.Images.Media.DATA};
                //MediaStore.Images는 타입이 image인 미디어의 모음집이다.
                //MediaStore.Images.Media.DATA 는 캐시된 열의 인덱스인듯
                //쿼리에서 받는 인자 값 형태가 string 배열 형태이기 떄문에

                Log.e(TAG, "타입이 image인 미디어의 모음집" + proj);
                cursor = getContentResolver().query(uri.get(i), proj, null, null, null);
                //쿼리의 인자값 1. uri 찾고자 하는 데이터의 uri /2. projection db의 칼럼과 같다. 결과로 받고 싶은 데이터의 종류를 알려준다.
                // 3.Selection : DB 의 where 키워드와 같다. 어떤 조건으로 필터링된 결과를 받을 때 사용 /4.selection과 함께 사용 /5. 쿼리 결과 데이터를 분류 할 때 사용
                Log.e(TAG, "절대 경로3/" + cursor);

                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //컬럼의 인덱스를 얻는 코드이다 왜 컬럼의 인덱스를 얻지?
                //커서로 컬럼을 읽을 때는 getLOng 또는 getString 함수에 인자값으로 컬럼의 index를 넣어야 하기 때문
                Log.e(TAG, "절대 경로4/" + column_index);
                cursor.moveToFirst();
                Log.e(TAG, "절대 경로5/" + cursor.getString(column_index));
                imagePath.add(cursor.getString(column_index));
                Log.e(TAG, i + "번째 imagePath 값 : " + imagePath.get(i));
                //getString 은 욫청된 열의 값을 문자열로 반환
            }
            return imagePath;
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
    }

    //---------------------------레트로핏 통신 & 리사이클러뷰에 아이템 추가 하는 함수 -------------------------------

    private void postImage() {
        Log.e(TAG, "postImage()");
        //filepath는 String 변수로 갤러리에서 이미지를 가져올 때 photoUri.getPath()를 통해 받아온다
        getRealPathFromURL(uriList);

        ArrayList<MultipartBody.Part> images = new ArrayList<>();
        for (Integer i = 0; i < imagePath.size(); i++) {
            File file = new File(imagePath.get(i));

            // -------------> 이미지 압축 과정 시작
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uriList.get(i));
                //contentResolver 주소를 통해 데이터를 접근해서 결과를 가져온다.
                //openInputStream은 uri와 연결된 콘텐츠에 대한 스트림(데이터가 전송되는 통로)을 연다.
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            //입력스트림을 비트맵으로 디코딩한다.
            //매개변수는 비트맵으로 디코딩할 수 원시데이터를 가지고 있는 입력 스트림
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //이 클래스는 출력 스트림이며(데이터가 나가는 통로의 역할에 대해 규정),
            //데이터가 바이트 배열에 기록된다.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            //지정된 출력 스트림에대한 비트맵 버전을 압축한다. 즉 사진 용량 줄이는 과정
            //매개변수 1 : 압축된 이미지의 형식, 품질 (0-100),
            //압축된 데이터를 쓰기 위한(데이터를 내보낸다는 뜻) 출력 스트림
            // -------------> 이미지 압축 과정 끝
            //사진 requestbody -> multipart
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), byteArrayOutputStream.toByteArray());

            //http 요청 또는 응답 본문의 콘텐츠 유형을 설명한다.
            images.add(MultipartBody.Part.createFormData("uploaded_file[]", file.getName(), requestFile));
            Log.e(TAG, "uploaded_file" + images.get(i));
            //인터페이스에서 파일을 보내기 위해 컨탠트 파입을 multipart 로 명시해주었기 때문에 requestbody를  multipart로 변경한다.
            //그리고 images라는 배열에 넣어준다.
        }

        now = System.currentTimeMillis();
        date = new Date(now);
        Dsdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss", Locale.KOREA);
        sdf = new SimpleDateFormat("a hh:mm", Locale.KOREA);
        getDetailTime = Dsdf.format(date);
        getTime = sdf.format(date);

        map = new HashMap<>();
        RequestBody sender = RequestBody.create(MediaType.parse("text/plain"), login_id);
        RequestBody roomNumber = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id_meeting));
        RequestBody image_count = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(images.size()));
        RequestBody detail_date = RequestBody.create(MediaType.parse("text/plain"), getDetailTime);
        RequestBody date = RequestBody.create(MediaType.parse("text/plain"), getTime);

        map.put("image_count", image_count);
        map.put("sender", sender);
        map.put("roomNumber", roomNumber);
        map.put("detail_date", detail_date);
        map.put("date", date);

        Call<String> call = NetWorkHelper.getInstance().getApiService().postChatImage(images, map);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "응답1 =" + response.body());
                Log.e(TAG, "응답2 =" + response);
                String jsonResponse = response.body();

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if ((jsonObject.optString("result").equals("success"))) {

                    Log.e(TAG, "이미지이름 배열" + jsonObject.optString("image_array"));
                    String image_names = jsonObject.optString("image_array");

                    ThreadSendImage threadSendImage = new ThreadSendImage(mySocket, image_names, preferenceHelper.getProfile(),id_meeting);
                    threadSendImage.start();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });
    }

    public void cancelGroupJoin(int exit, String exit_id, String exitNickname) { // exit는 서버에서 퇴장인지 아닌지 여부를 결정하기 위해 쓰인다.
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
                    ISDeleteDB_enterMeeting(jsonResponse, exitNickname, exit_id);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
            }
        });

    }


    //    db에 모임 참여 취소 저장이 되었는지 여부 따지는 함수
    private void ISDeleteDB_enterMeeting(String response, String nickname, String exit_id) {
        try {
            Log.e(TAG, "ISDeleteDB_enterMeeting");
            JSONObject jsonObject = new JSONObject(response);
            String message = jsonObject.getString("message");
            if (0 == jsonObject.getInt("exit")) { // 유저가 스스로 나가기 해서 모임을 나간 경우
                if (jsonObject.getString("status").equals("true")) {
                    ThreadSender sendOut = new ThreadSender(mySocket, "bye", preferenceHelper.getProfile(),id_meeting);
                    sendOut.start(); //ThreadSener 은 bye를 입력 받으면 나간다는 메시지를 서버에게 보내고 소켓을 종료한다.
                    Toast.makeText(ActivityChatRoom.this, message, Toast.LENGTH_LONG).show();
                    super.onBackPressed();

                } else {
                    Toast.makeText(ActivityChatRoom.this, message, Toast.LENGTH_LONG).show();
                }
                //참여 취소 실패 다시 시도하세요 문구는 -> db에 삭제가 안된것임
            } else {
                Log.e(TAG, "ISDeleteDB_enterMeeting // 퇴장");
                ThreadSenderExit sendExit = new ThreadSenderExit(mySocket, nickname + "님이 퇴장되었습니다", exit_id);
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
                    generateUserList(data);

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
        Log.e(TAG, "getMessages" + "과거 채팅 목록 받아오는 함수");
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
        chatList.clear();
        chatList.addAll(items);
        adapterChat.notifyDataSetChanged();
        recyclerView.scrollToPosition(chatList.size() - 1);

    }

    void connectSocket() {
        Log.e(TAG, "connectSocket");
            Log.e(TAG, "소켓이 연결되지 않을 때만 connectionThread 통해 연결");
//            thread = new ConnectionThread(preferenceHelper.getNickname(), preferenceHelper.getID(), chatHandler, isMember);
//            thread.start();
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

    }

    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
//        try {// 뒤로가기 했을 때 소켓 닫아준다.
//            if (mySocket != null) {
//                mySocket.close();
//            }
//            //소켓과 관련된 outstream, inputstrem이 닫아지고 -> 스트림이 종료되면서 관련 스레드도 종료됨
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
//        getMessages();


    }

}