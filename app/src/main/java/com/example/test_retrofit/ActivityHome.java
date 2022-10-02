package com.example.test_retrofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.test_retrofit.FragmentMypage;
import com.example.test_retrofit.R;

import com.example.test_retrofit.cafe.FragmentCafeHome;
import com.example.test_retrofit.chat.FragmentChatList;
import com.example.test_retrofit.group.FragmentGroupList;
import com.example.test_retrofit.group.FragmentMyGroup;
import com.example.test_retrofit.group.Fragment_joinGroup;
import com.example.test_retrofit.like.FragmentLike;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class ActivityHome extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    BottomNavigationView bottomNavigationView;
    private LinearLayout group_select;
    private TextView show_group, my_group, join_group;
    private String select ;
//    my_groupFragment -> GroupContentActivity 통해 intent로 받은 값
//    이 값에 따라 등록 후 어떤 화면으로 이동할지 결정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"홈 화면");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        group_select = findViewById(R.id.group_select);
        show_group = (TextView) findViewById(R.id.show_group);
        my_group = (TextView)findViewById(R.id.my_group);
        join_group = (TextView)findViewById(R.id.join_group);

        select = getIntent().getStringExtra("my_group");
        Log.e(TAG,"select 값 확인" + select);
//       프래그먼트 객체 선언
        Fragment fragment_home = new FragmentGroupList();
        Fragment fragment_mypage = new FragmentMypage();
        Fragment fragment_my_group = new FragmentMyGroup();
        Fragment fragment_join_group = new Fragment_joinGroup();
        Fragment fragment_cafe = new FragmentCafeHome();
        Fragment fragment_like = new FragmentLike();
        Fragment fragment_chat = new FragmentChatList();

//        제일 처음 띄워줄 뷰를 세팅해준다. .commit()까지 해줘야 한다.
        if(select != null)
        {

            if(select.equals("my_group")) {
                Log.e(TAG,"select 값 확인" + select);
//            내 그룹 보기에서 수정했을 때  처음 띄워질 뷰는 내 그룹 보기
                getSupportFragmentManager().beginTransaction().replace(R.id.group_list, fragment_my_group).commitAllowingStateLoss();
            }

        }

        else {
            Log.e(TAG,"select 값 확인2" + select);
            getSupportFragmentManager().beginTransaction().replace(R.id.group_list,fragment_home).commitAllowingStateLoss();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);



// 텍스트뷰 모임보기 클릭
        show_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.group_list,fragment_home).commitAllowingStateLoss();
                // replace(프레그먼트를 띄워줄 frameLayout, 교체할 fragment 객체)
                Log.e(TAG, "텍스트 뷰 모임보기 클릭");

            }
        });

        my_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.group_list,fragment_my_group).commitAllowingStateLoss();
                // replace(프레그먼트를 띄워줄 frameLayout, 교체할 fragment 객체)
                Log.e(TAG, "텍스트 뷰 내 모임 보기 클릭");

            }
        });

        join_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.group_list,fragment_join_group).commitAllowingStateLoss();
                // replace(프레그먼트를 띄워줄 frameLayout, 교체할 fragment 객체)
                Log.e(TAG, "텍스트 뷰 참여함 모임 보기 클릭");

            }
        });


//        네비게이션 바가 선택될 때 실행되는 코드
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            하단 항목이 선택될 때 알림을 받을 리스너를 설정한다. 선택된 리스너는 현재 선택된 항목이 다시 선택되면 다시 알림을 받는다.
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){

                    case R.id.homeItem:
                        Log.e(TAG, "nv바 홈 클릭");
                        // replace(프레그먼트를 띄워줄 frameLayout, 교체할 fragment 객체)
                        getSupportFragmentManager().beginTransaction().replace(R.id.group_list,fragment_home).commitAllowingStateLoss();
                        Log.e(TAG, "홈 클릭 후 프레그먼트 교체");
                        group_select.setVisibility(View.VISIBLE);
//                        한번 GONE 세팅하면 계속 유지되는 것 같음 그래서 보여줘야 하는 곳에서 다시 설정해줌
                        return true;

                    case R.id.mypageItem:
                        Log.e(TAG, "nv바 마이페이지 클릭");
                        getSupportFragmentManager().beginTransaction().replace(R.id.group_list,fragment_mypage).commitAllowingStateLoss();
                        Log.e(TAG, "마이페이지 클릭 후 프레그먼트 교체");
                        group_select.setVisibility(View.GONE);
//                        해당 뷰를 안 보여줌 (공간 마저 감춤)
                        return  true;

                    case R.id.cafeItem:
                        Log.e(TAG, "nv바 카페 클릭");
                        getSupportFragmentManager().beginTransaction().replace(R.id.group_list,fragment_cafe).commitAllowingStateLoss();
                        Log.e(TAG, "카페 클릭 후 프레그먼트 교체");
                        group_select.setVisibility(View.GONE);
                        return  true;


                    case R.id.likeItem:
                        Log.e(TAG, "nv바 좋아요 클릭");
                        getSupportFragmentManager().beginTransaction().replace(R.id.group_list,fragment_like).commitAllowingStateLoss();
                        Log.e(TAG, "좋아요 클릭 후 프레그먼트 교체");
                        group_select.setVisibility(View.GONE);
                        return  true;

                    case R.id.chatItem:
                        Log.e(TAG, "nv바 채팅 리스트 클릭");
                        getSupportFragmentManager().beginTransaction().replace(R.id.group_list,fragment_chat).commitAllowingStateLoss();
                        Log.e(TAG, "채팅 클릭 후 프레그먼트 교체");
                        group_select.setVisibility(View.GONE);
                        return  true;

                    default:
                        return false;
                }


            }

        });


    }

// 이러한 형식의 클릭은 oncreate 밖에서 해줘야 함


    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart");
    }


    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
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
}