package com.example.test_retrofit.chat;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ThreadSenderExit extends Thread {
    private final String TAG = this.getClass().getSimpleName();
    private Socket socket;
    private String message,exit_id;
    private PrintWriter writer;
    private long now;
    private Date date;
    private SimpleDateFormat Dsdf, sdf;
    private String getTime,getDetailTime;

    ThreadSenderExit(Socket socket, String message,String exit_id){
        this.socket = socket;
        this.message = message;
        this.exit_id =exit_id;

        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        Log.e(TAG, "ThreadSenderExit");

        try {
            Log.e(TAG, "ThreadSender");
            if (writer != null) {
                if (message != null) {
                    now = System.currentTimeMillis();
                    date = new Date(now);
                    Dsdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss", Locale.KOREA);
                    sdf = new SimpleDateFormat("a hh:mm", Locale.KOREA);
                    getDetailTime = Dsdf.format(date);
                    getTime= sdf.format(date);
                    JSONObject sending = new JSONObject();
                    sending.put("message",message);
                    sending.put("date",getTime);
                    sending.put("detail_date",getDetailTime);
                    sending.put("view_type",300);
                    sending.put("exit_id",exit_id);
                    writer.println(sending);
                    Log.e(TAG, "서버에게 메시지 보냄");
                    writer.flush();}

            }

        } catch (Exception e) {
            Log.e(TAG, "에러");
            Log.e(TAG, e.getMessage());
        }
    }
}
