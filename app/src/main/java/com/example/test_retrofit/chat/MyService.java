package com.example.test_retrofit.chat;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.test_retrofit.user.PreferenceHelper;

import java.util.List;

public class MyService extends Service {
    private final String TAG = this.getClass().getSimpleName();
    PreferenceHelper preferenceHelper;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { // startService를 호출 할 때 마다 실행되는 함수이다.
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() { // 서비스가 처음 생성되었을 때 호출되어 일회성 설정 절차를 수행
        //서비스가 이미 실행중인 경우, 호출되지 않음
        Log.e(TAG, "onCreate");
        super.onCreate();
        preferenceHelper = new PreferenceHelper(this);
        ConnectionThread connectionThread = new ConnectionThread(preferenceHelper.getNickname(),preferenceHelper.getID(),ActivityChatRoom.chatHandler,"yes",this);
        connectionThread.start();

    }

    @Override
    public void onDestroy() { // 서비스를 소멸시킬 때 호출됨, 리소스 정리하기 위한 코드를 이곳에 구현, 즉 서비스가 수신하는 마지막 호출
        super.onDestroy();
    }
}