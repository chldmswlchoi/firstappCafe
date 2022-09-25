package com.example.test_retrofit.chat;

import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadSender2 extends Thread {
    private final String TAG = this.getClass().getSimpleName();
    private Socket socket;
    private String message;

    private PrintWriter writer;

    ThreadSender2(Socket socket, String message) {
        this.socket = socket;
        this.message = message;
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // printWriter 에 OutputStream 을 래핑하여 다음과 같이 데이터를 텍스트 형식으로 보낼 수 있다.
        //다양한 출력형식을 제공하는 메소드를 제공한다.,
        // 인수 1: outputStrem 객체, 2: autoFlush 를 t로 하면 버퍼가 차거나 필요한 경우 비운다.

    }

    public void run() {
        try {
            Log.e(TAG, "ThreadSender");
            if (writer != null) {
                if (message != null) {
                    writer.println(message);
                    writer.flush();}
            }

        } catch (Exception e) {
            Log.e(TAG, "에러");
            Log.e(TAG, e.getMessage());
        }
    }
}

