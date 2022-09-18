package com.example.test_retrofit.user;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.test_retrofit.user.email_pw.Activity_findPW;
import com.example.test_retrofit.ActivityHome;
import com.example.test_retrofit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLogin extends AppCompatActivity {

    private final String TAG ="LoginActivity";
    private EditText etemail, etpassword;
    private TextView password_check,find_pw,register;
    private Button login;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferenceHelper = new PreferenceHelper(this);

        //edittext 연결
        etemail =(EditText) findViewById(R.id.etemail);
        etpassword = findViewById(R.id.etpassword);

        //text 연결
        password_check = findViewById(R.id.password_check);
        find_pw = findViewById(R.id.find_pw);
        register = findViewById(R.id.register);

        //bt 연결
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etemail.getText().toString();
                String password = etpassword.getText().toString();
                if(email.isEmpty() || password.isEmpty())
                {// 아이디 또는 비번을 입력하지 않았을 때
                    Log.e("빈 상태","빈 상태");
                    EmptyDialog();
                    Log.e("빈 상태","빈 상태");
                }

                else{
                    loginUser(); // 서버 통신 함수
                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ActivityLogin.this, Activity_signup.class);
                startActivity(intent);
            }
        });

        find_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ActivityLogin.this, Activity_findPW.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser()
    {//서버 통신 함수
        final String email = etemail.getText().toString().trim();
        final String password = etpassword.getText().toString().trim();
        //trim 은 앞과 뒤 공백 제거 함수

        InterfaceLogin api = ApiClient.getApiClient().create(InterfaceLogin.class);
        Call<String> call = api.getUserLogin(email, password);
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
                password_check.setText("");
                saveinfo(response);

                Intent intent= new Intent(ActivityLogin.this, ActivityHome.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                //홈화면으로 이동
                Log.e(TAG, "로그인 성공");

            }

            else
            {
                password_check.setText(jsonObject.getString("message"));
            }
        }


        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveinfo(String response)
    {
        preferenceHelper.putIsLogin(true);
        try{

                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("status").equals("true")) {
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataobj = dataArray.getJSONObject(i);
                        preferenceHelper.putID(dataobj.getString("id"));
                        preferenceHelper.putNickname(dataobj.getString("nickname"));
                        preferenceHelper.putProfile(dataobj.getString("profile"));

                    }
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void EmptyDialog()
    {//아이디 비번 입력 알람창
        Log.e("다이얼로그","1");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이메일 또는 비밀번호를 입력해주세요");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }

}