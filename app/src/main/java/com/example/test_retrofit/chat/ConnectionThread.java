package com.example.test_retrofit.chat;

import android.os.Handler;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionThread extends Thread{

    private final String TAG = this.getClass().getSimpleName();
    private ThreadReceiver receiverThread;
    private Socket socket;
    private String nickname;

    private Handler chatHandler;

    ConnectionThread(String nickname, Handler chatHandler){
        this.nickname = nickname;
        this.chatHandler= chatHandler;
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
                PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
                writer.println(nickname);
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
