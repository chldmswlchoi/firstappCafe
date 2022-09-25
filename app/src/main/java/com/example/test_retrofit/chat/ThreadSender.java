package com.example.test_retrofit.chat;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.UiThread;

import com.example.test_retrofit.user.PreferenceHelper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ThreadSender extends Thread {
    private final String TAG = this.getClass().getSimpleName();
    private Socket socket;
    private String message;
    private String profile_name,getTime,getDetailTime;
    private PrintWriter writer;
    private long now;
    private Date date;
    private SimpleDateFormat Dsdf, sdf;


    ThreadSender(Socket socket, String message, String profile_name) {
        this.socket = socket;
        this.message = message;
        this.profile_name = profile_name;
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // printWriter 에 OutputStream 을 래핑하여 다음과 같이 데이터를 텍스트 형식으로 보낼 수 있다.
        //다양한 출력형식을 제공하는 메소드를 제공한다.,
        // 인수 1: outputStrem 객체, 2: autoFlush 를 t로 하면 버퍼가 차거나 필요한 경우 비운다.

    }

    @SuppressLint("SimpleDateFormat")
    public void run() {
        try {
            Log.e(TAG, "ThreadSender");
            if (writer != null) {
                if (message != null) {
                    now = System.currentTimeMillis();
                    date = new Date(now);
                    Dsdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
                    sdf = new SimpleDateFormat("a H:mm", Locale.KOREA);
                    getDetailTime = Dsdf.format(date);
                    getTime= sdf.format(date);
                    writer.println(message+"#"+profile_name+"#"+getTime+"#"+getDetailTime);
                    writer.flush();}
            }

        } catch (Exception e) {
            Log.e(TAG, "에러");
            Log.e(TAG, e.getMessage());
        }
    }
}

