package com.example.test_retrofit.chat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.test_retrofit.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import java.util.List;


public class ThreadReceiver extends Thread {
    private final String TAG = this.getClass().getSimpleName();
    Socket socket;
    Handler chatHandler;
    Context context;
    // Channel에 대한 id 생성
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // Channel을 생성 및 전달해 줄 수 있는 Manager 생성
    private NotificationManager mNotificationManager;

    // Notification에 대한 ID 생성
    private static final int NOTIFICATION_ID = 0;

    Gson gson = new Gson();

    final int HANDLER_WHAT = 1111;

    ThreadReceiver(Socket socket, Handler chatHandler, Context context) {
        this.socket = socket;
        this.chatHandler = chatHandler;
        this.context = context;

    }

    public void run() {
        try {
            Log.e(TAG, "ThreadReceiver");
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                Log.e(TAG, "서버에서 메시지 받음");
                String str = reader.readLine(); //개행문자열이 포함된 메시지가 와야만 다음 문장이 실행된다.
                if (str == null) {
                    break;
                }
                Log.e(TAG, "현재 액티비티가 뭔지 관찰");
                ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> info = manager.getRunningTasks(1);
                ComponentName componentName = info.get(0).topActivity;
                String ActivityName = componentName.getShortClassName().substring(1);
                Log.e(TAG, "관찰 결과 : " + ActivityName);
                DTOChat chatData = gson.fromJson(str, DTOChat.class);
                if (ActivityName.equals("chat.ActivityChatRoom") && chatData.getId_meeting() == ActivityChatRoom.id_meeting) {

                    Log.e(TAG, "액티비티가 채팅방일 떄");
                    //핸들러에게 전달할 메시지 객체
                    Message message = ActivityChatRoom.chatHandler.obtainMessage();
                    //핸들러에게 전달할 메시지의 식별자
                    message.what = HANDLER_WHAT;
                    //메시지의 본문
                    message.obj = str;
                    //핸들러에게 메시지 전달
                    ActivityChatRoom.chatHandler.sendMessage(message);
                } else {
                    createNotificationChannel();
                    sendNotification(chatData.getNickname(), chatData.getMessage(), chatData.getId_meeting(),chatData.getView_type());

                }
            }
        } catch (IOException e) {
            Log.e(TAG, "에러");
            Log.e(TAG, e.getMessage());
        }
    }

    //채널을 만드는 메소드
    public void createNotificationChannel() {
        Log.e(TAG, "createNotificationChannel()");
        //notification manager 생성
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
        if (android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O) {
            //Channel 정의 생성자( construct 이용 )
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID
                    , "Test Notification", mNotificationManager.IMPORTANCE_HIGH);
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

    }

    // Notification Builder를 만드는 메소드
    private NotificationCompat.Builder getNotificationBuilder(String nickname, String text, Integer id_meeting, Integer viewType) {
        Intent intent = new Intent(context, ActivityChatRoom.class);
        intent.putExtra("id_meeting", id_meeting);
        if(viewType==4 || viewType ==5){
            text ="사진입니다";
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT );
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setContentTitle(nickname)
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.cafe)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return notifyBuilder;
    }

    // Notification을 보내는 메소드
    public void sendNotification(String nickname, String text, Integer id_meeting, Integer viewType) {
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(nickname, text, id_meeting,viewType);
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

}

