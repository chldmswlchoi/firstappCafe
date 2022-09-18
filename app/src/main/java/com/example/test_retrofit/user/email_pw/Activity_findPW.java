package com.example.test_retrofit.user.email_pw;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test_retrofit.R;

import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.test_retrofit.user.ApiClient;

public class Activity_findPW extends AppCompatActivity {

    private TextView etemail = null;
    private TextView message = null;
    private Button button = null;
    private Button send;
    private String text_check="test";
    private TextView email_check;

    private final String TAG ="FindpwActivity";
    private String userEmail;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpw);


        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


        etemail = (TextView) findViewById(R.id.etemail); //받는 사람의 이메일
        message = (TextView) findViewById(R.id.etnumber); //본문 내용
        send =findViewById(R.id.send);
        button = (Button) findViewById(R.id.number_ok);
        email_check = findViewById(R.id.email_check);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = etemail.getText().toString();
                userEmail =email;

                if(email.isEmpty())
                {NumberDialog("이메일을 작성해주세요");}

                else {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();

                            GmailSender gMailSender = new GmailSender(etemail.getText().toString(), "ofiaahkylajvpisj");
                            //GMailSender.sendMail(제목, 본문내용, 받는사람);
                            try {
                                gMailSender.sendMail("인증번호", gMailSender.getEmailCode(), etemail.getText().toString());
                                text_check = gMailSender.getEmailCode();
                                Log.e("text_check",text_check);
                            }catch(SendFailedException e) {

                                //쓰레드에서는 Toast를 띄우지 못하여 runOnUiThread를 호출해야 한다.
                                Activity_findPW.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }catch(MessagingException e){
                                System.out.println("인터넷 문제"+e);

                                //쓰레드에서는 Toast를 띄우지 못하여 runOnUiThread를 호출해야 한다.
                                Activity_findPW.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"인터넷 연결을 확인 해 주십시오", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //쓰레드에서는 Toast를 띄우지 못하여 runOnUiThread를 호출해야 한다.
                            Activity_findPW.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "송신 완료", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }.start();

                    UserCheck();
                }





            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                message.getText().toString() .equals(text_check)
                if(message.getText().toString() .equals(text_check))
                {
                    Log.e("이메일 인증 성공",text_check);
                    Toast.makeText(getApplicationContext(), "이메일 인증 성공", Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(Activity_findPW.this,ChangepwActivity.class);
                    intent.putExtra("email",userEmail);
                    startActivity(intent);
                }

                else{
                    NumberDialog("잘못된 인증번호입니다.");
                    Log.e("이메일 인증 실패",text_check);

                }

            }
        });
    }

    private void UserCheck(){

        final String email = etemail.getText().toString().trim();

        InterfaceEmail api = ApiClient.getApiClient().create(InterfaceEmail.class);
        Call<String> call = api.getUserEmail(email);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "응답1 =" + response.body());
                Log.e(TAG, "응답2 =" + response);

                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e ("onSuccess",response.body());
                    String jsonResponse = response.body();
                    parseLoginData(jsonResponse);


                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });


    }

    private void parseLoginData(String response)
    {
        try{
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("true"))
            {
                email_check.setText("");




            }

            else
            {
                email_check.setText(jsonObject.getString("message"));
            }
        }


        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserEmail(){return userEmail;}

    public void NumberDialog(String a)
    {//아이디 비번 입력 알람창
        Log.e("다이얼로그","1");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(a);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }
}