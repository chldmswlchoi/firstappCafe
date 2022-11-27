package com.example.test_retrofit.chat;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ThreadSendImage extends Thread {

    private final String TAG = this.getClass().getSimpleName();
    private Socket socket;
    private String message;
    private String profile_name,getTime,getDetailTime;
    private PrintWriter writer;
    private long now;
    private Date date;
    private SimpleDateFormat Dsdf, sdf;
    private  Integer id_meeting;

    ThreadSendImage(Socket socket,String message,String profile_name,Integer id_meeting){
        this.socket = socket;
        this.message = message;
        this.profile_name = profile_name;
        this.id_meeting =id_meeting;
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void run(){
        try {
            Log.e(TAG, "ThreadSendImage");
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
                    sending.put("profile",profile_name);
                    sending.put("id_meeting",id_meeting);
                    sending.put("date",getTime);
                    sending.put("view_type",4);
                    sending.put("detail_date",getDetailTime);
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
