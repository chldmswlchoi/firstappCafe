package com.example.test_retrofit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.test_retrofit.user.ActivityLogin;
import com.example.test_retrofit.user.PreferenceHelper;

public class ActivityFirstLoading extends AppCompatActivity {

    PreferenceHelper preferenceHelper;
    String id;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        preferenceHelper = new PreferenceHelper(ActivityFirstLoading.this);
//        sp를 편집 또는 저장된 값을 가져오기 위해서는 초기화를 해줘야 한다.
        id = preferenceHelper.getID();

        startLoading();

    }

    private void startLoading(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
//           첫번쨰 인자인 스레드가 대기열에 추가되고 두번째 인자에서 설정해준 시간만큼 지난뒤 스레드가 실행된다.
            @Override
            public void run() {

                if(id.length()==0){
                    intent = new Intent(ActivityFirstLoading.this, ActivityLogin.class);
                    startActivity(intent);
                }

                else {
                    intent = new Intent(ActivityFirstLoading.this, ActivityHome.class);
                    startActivity(intent);

                }
                finish();
            }
        },2000);

    }
}