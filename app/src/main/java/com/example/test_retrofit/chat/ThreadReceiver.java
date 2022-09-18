package com.example.test_retrofit.chat;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;


public class ThreadReceiver extends Thread {
    private final String TAG = this.getClass().getSimpleName();
    Socket socket;
    Handler chatHandler;

    final int HANDLERWHAT = 1111;
    ThreadReceiver(Socket socket, Handler chatHandler) {
        this.socket = socket;
        this.chatHandler = chatHandler;

    }

    public void run() {


        try {
            Log.e(TAG, "ThreadReceiver");
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
//                Log.e(TAG, "while");
                String str = reader.readLine();
                if (str == null) {
                    break;
                }
                //핸들러에게 전달할 메시지 객체
                Message message = chatHandler.obtainMessage();
                //핸들러에게 전달할 메시지의 식별자
                message.what = HANDLERWHAT;
                //메시지의 본문
                message.obj = str;
                //핸들레에게 메시지 전달
                chatHandler.sendMessage(message);
            }
        } catch (IOException e) {
            Log.e(TAG, "에러");
            Log.e(TAG,e.getMessage());
        }
    }


}

