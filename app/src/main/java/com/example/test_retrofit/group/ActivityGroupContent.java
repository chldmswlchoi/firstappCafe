package com.example.test_retrofit.group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test_retrofit.R;
import com.example.test_retrofit.chat.ActivityChatRoom;
import com.example.test_retrofit.group.DTO.DTOGroupResponse;
import com.example.test_retrofit.group.retrofit_Interface.Interface_cancelJoinGroup;
import com.example.test_retrofit.group.retrofit_Interface.Interface_getGroupContent;
import com.example.test_retrofit.group.retrofit_Interface.Interface_joinGroup;
import com.example.test_retrofit.user.ApiClient;
import com.example.test_retrofit.user.PreferenceHelper;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.MarkerIcons;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityGroupContent extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = this.getClass().getSimpleName();
    private TextView cafe, location, date, time, people, title,total_member;
    private Integer id_meeting, mapx, mapy , get_people, get_total_member;
    private String road_address;
    private PreferenceHelper preferenceHelper;
    private String id;// 모임 생성자
    private Button button;
    private String select = "";
    private String login_id;

    private String USER_SELECT = "a";

    private FragmentManager fm;
    private MapFragment mapFragment;
    private NaverMap naverMap;

    private Tm128 tm128;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_content);

        title = findViewById(R.id.title);
        cafe = findViewById(R.id.cafe);
        location = findViewById(R.id.location);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        people = findViewById(R.id.people);
        button = findViewById(R.id.modify);
        total_member = findViewById(R.id.total_member);

        // 프레그먼트 위에 프레그먼트를 세팅하기 위해 필요한 함수
        FragmentManager fm = getSupportFragmentManager();
        mapFragment = (MapFragment)fm.findFragmentById(R.id.map);

//        인텐트에서 보낸 값 받는 과정
        id_meeting = getIntent().getIntExtra("id_meeting", -1);
        road_address = getIntent().getStringExtra("road_address");
        select = getIntent().getStringExtra("my_group");

        Log.e(TAG, "인텐트에서 받는 값/" + id_meeting + road_address + select);

        preferenceHelper = new PreferenceHelper(this);
        login_id = preferenceHelper.getID();
//        로그인한 유저 아이디 받아오기

        getGroupData();
//        레트로핏 통신 서버에서 데이터 가져옴


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//               로그인한 아이디와 게시판 작성자의 아이디가 같을 경우는 -> 모임 생성자인 경우
                if (id.equals(login_id)) {
                    Intent intent = new Intent(ActivityGroupContent.this, Activity_updateGroup.class);
                    String title2 = (String) title.getText();
                    String cafe2 = (String) cafe.getText();
                    String date2 = (String) date.getText();
                    String time2 = (String) time.getText();
                    String people2 = (String) people.getText();

//                    인텐트로 값을 보내는 과정
                    intent.putExtra("title", title2);
                    intent.putExtra("cafe", cafe2);
                    intent.putExtra("date", date2);
                    intent.putExtra("time", time2);
                    intent.putExtra("people", people2);
                    intent.putExtra("id_meeting", id_meeting);
                    intent.putExtra("road_address", road_address);
                    intent.putExtra("mapx", mapx);
                    intent.putExtra("mapy", mapy);
                    intent.putExtra("my_group", select);

                    Log.e(TAG, "인텐트에서 보내주는 값/" + id_meeting + road_address + mapx + mapy);
                    startActivity(intent);
                }
                //                    모임 생성자가 아닌 경우
                else {
//                    모임 참여자가 아닌 경우
                    if (USER_SELECT.equals("join")) {
                            JoinGroupDialog();

//                    다이얼로그에서 참여를 선택하면 레트로핏 통신으로 모임참여 db에 저장해준다.
                    } else if (USER_SELECT.equals("already_join")) {
                        cancelJoinGroupDialog();
//                    다이얼로그에서 참여취소를 선택하면 레트로핏 통신으로 모임참여 db에 삭제해준다.
                    } else {
                        EmptyDialog("참여 오류 다시 시도하세요");
                    }

                }

            }
        });


    }


    // 그룹 상세보기 관련 데이터 불러오는 레트로핏 함수
    void getGroupData() {
        Interface_getGroupContent Interface_getGroupContent = ApiClient.getApiClient().create(Interface_getGroupContent.class);
        Call<DTOGroupResponse> call = Interface_getGroupContent.getGroupContentResult(id_meeting, login_id);
        call.enqueue(new Callback<DTOGroupResponse>() {
            @Override
            public void onResponse(Call<DTOGroupResponse> call, Response<DTOGroupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DTOGroupResponse DTOGroupResponse = response.body();
                    Log.e(TAG, "응답 성공 후 dto 파싱해준 값" + DTOGroupResponse);

//                    모임 생성자거나 아직 참여안한 유저일 경우
                    if (DTOGroupResponse.getStatus().equals("true")) {
                        List<DTOGroupResponse.GroupDTO> items = DTOGroupResponse.getData();
//                        getData 형태가 리스트 형태니까 List 로 받아야지
                        Log.e(TAG, "응답 성공 후 dto 파싱해준 값" + items);
                        setContent(items);
//                        결과값을 tv에 세팅해주는 함수

//                        참여 안한 유저일 경우
                        if (!id.equals(login_id)) {
                            USER_SELECT = "join";

                            if(get_total_member>=get_people || items.get(0).getExit_member() ==1)
                            { // 모임 참여 인원이 다 찼을 때, 퇴장 당한 멤버일 때 버튼 비활성화
                                button.setVisibility(View.GONE);
                            }
                            else {

                                button.setText("참여하기");
                                Log.e(TAG, "참여안한 유저" + USER_SELECT);
                                button.setVisibility(View.VISIBLE);
                            }


                        }

                    }
//                    이미 참여한 유저일 경우
                    else if (DTOGroupResponse.getStatus().equals("already_join"))
                    {
                        List<DTOGroupResponse.GroupDTO> items = DTOGroupResponse.getData();
//                        getData 형태가 리스트 형태니까 List 로 받아야지
                        Log.e(TAG, "응답 성공 후 dto 파싱해준 값" + items);
                        setContent(items);
                        USER_SELECT = "already_join";
                        button.setText("참여 취소하기");
                        Log.e(TAG, "이미 참여한 유저" + USER_SELECT);
                    }

                    else {
                        EmptyDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }

                }
            }

            @Override
            public void onFailure(Call<DTOGroupResponse> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });

    }


    //        결과값을 tv에 세팅해주는 함수
    public void setContent(List<DTOGroupResponse.GroupDTO> content) {
//        그러면 리스트 요소들은 object 안에 key:value 형태로 값들이 많은데 하나하나를 꺼내려면..?
//        다음과 같이 하면된다.!
        DTOGroupResponse.GroupDTO result = content.get(0);
        title.setText(result.getTitle());
        cafe.setText(result.getCafe());
        location.setText(result.getRoad_address());
        date.setText(result.getGdate());
        time.setText(result.getGtime());
        people.setText(String.valueOf(result.getPeople()));
        id = result.getId(); // 모임 생성자
        mapx = result.getMapx();
        mapy = result.getMapy();
        total_member.setText(String.valueOf(result.getTotal_member()));

        get_total_member = result.getTotal_member();
        get_people=result.getPeople();
        Log.e(TAG,"참여인원 & 제한 인원"+get_total_member+get_people);

        Log.e(TAG, "setContent" + "레트로핏 통신 후 결과값을 세팅해줌//" + USER_SELECT);

        //        네이버 지역 검색 api로 받아온 mapx,mapy를 위도 경도로 변환하는 과정
        tm128 = new Tm128(mapx,mapy);
        latLng = tm128.toLatLng();

        //        지도를 화면에 나타내기
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        //        네이버 api 호출하는 과정
        mapFragment.getMapAsync(this);

    }


    //    모임에 참여여부에 네 클릭시 db에 저장하는 과정
    public void joinGroup() {
        Interface_joinGroup Interface_joinGroup = ApiClient.getApiClient().create(Interface_joinGroup.class);
        Call<String> call = Interface_joinGroup.joinGroup(login_id, id_meeting);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "응답1 =" + response.body());
                Log.e(TAG, "응답2 =" + response);

                if (response.isSuccessful() && response.body() != null) {
                    Log.e("onSuccess", response.body());
                    String jsonResponse = response.body();
                    saveDB_enterMeeting(jsonResponse);
//                    db에 모임 참여 정보가 저장이 되었는지 여부 따지는 함수

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });

    }

    //    db에 모임 참여 정보가 저장이 되었는지 여부 따지는 함수
    private void saveDB_enterMeeting(String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            String message = jsonObject.getString("message");
            if (jsonObject.getString("status").equals("true")) {

                Toast.makeText(ActivityGroupContent.this, message, Toast.LENGTH_LONG).show();
                //참가 완료되었다는 문구 띄워지면 -> db에 저장이 된것임
                //db에 참가 정보가 저장이 완료가 되면 채팅방 액티비티로 이동
                Intent intent = new Intent(this, ActivityChatRoom.class);
                intent.putExtra("id_meeting",id_meeting);
                intent.putExtra("host",id);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(ActivityGroupContent.this, message, Toast.LENGTH_LONG).show();
//                참가 실패 다시 시도하세요 문구는 -> db에 저장이 안된것임
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void EmptyDialog(String a) {//아이디 비번 입력 알람창
        Log.e("다이얼로그", "1");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(a);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }

    public void JoinGroupDialog() {//아이디 비번 입력 알람창
        Log.e("다이얼로그", "1");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("참여하시겠습니까?").setPositiveButton("네",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {

                        joinGroup();
//                        사용자가 참석한다고 선택시 db에 저장하는 레트로핏 통신
                    }
                })
                .setNeutralButton("아니요", null).show();
    }


    public void cancelJoinGroupDialog() {//아이디 비번 입력 알람창
        Log.e("다이얼로그", "1");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("참여 취소하시겠습니까?").setPositiveButton("네",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {

                        cancelGroupJoin();
//                        사용자가 참석한다고 선택시 db에 저장하는 레트로핏 통신
                    }
                })
                .setNeutralButton("아니요", null).show();
    }


    public void cancelGroupJoin(){

        Interface_cancelJoinGroup interface_cancelJoinGroup = ApiClient.getApiClient().create(Interface_cancelJoinGroup.class);
        Call <String> call = interface_cancelJoinGroup.cancelJoinGroup(id_meeting,login_id,0);

        Log.e(TAG, "응답2 =" + id_meeting+login_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "응답1 =" + response.body());
                Log.e(TAG, "응답2 =" + response);


                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e ("onSuccess", String.valueOf(response.body()));
                    String jsonResponse = response.body();
                    ISDeleteDB_enterMeeting(jsonResponse);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }



    //    db에 모임 참여 정보가 저장이 되었는지 여부 따지는 함수
    private void ISDeleteDB_enterMeeting(String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            String message = jsonObject.getString("message");
            if (jsonObject.getString("status").equals("true")) {

                Toast.makeText(ActivityGroupContent.this, message, Toast.LENGTH_LONG).show();
//                참가 취소 완료되었다는 문구 띄워지면 -> db에 삭제가 된 것임
                finish();

            } else {
                Toast.makeText(ActivityGroupContent.this, message, Toast.LENGTH_LONG).show();
//                참가 실패 다시 시도하세요 문구는 -> db에 삭제가 안된것임
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING,true);
//       지도옵션 선택
        CameraPosition cameraPosition = new CameraPosition(
                latLng,17
        );
//       지도에 보여줄 위도경도와, 줌 확대 정도
        naverMap.setCameraPosition(cameraPosition);
//        세팅해줌


        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(latLng)
                .animate(CameraAnimation.Easing);
//        지도를 움직이기 위한 애니메이션 선택 후 등록
        naverMap.moveCamera(cameraUpdate);
//       세팅해줌

        Marker marker = new Marker();
        marker.setPosition(latLng);
        marker.setIcon(MarkerIcons.BLACK);
        marker.setIconTintColor(Color.parseColor("#FF9800"));
//        마커 위치와 색깔 지정해줌
        marker.setMap(naverMap);
//       마커 세팅
    }
}