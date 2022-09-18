package com.example.test_retrofit.user.email_pw;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test_retrofit.R;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.test_retrofit.user.ApiClient;
import com.example.test_retrofit.user.ActivityLogin;

public class ChangepwActivity extends AppCompatActivity {

    private EditText etpassword, etpassword2;
    private TextView password_check, password2_check;
    private Button register;
    private boolean pw, pw2 = false;
    private String passwordValidation = "^.*(?=^.{8,}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$";
    private String email;
    private String TAG ="ChangepwActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepw);


        etpassword = (EditText) findViewById(R.id.etpassword);
        etpassword2 = (EditText) findViewById(R.id.etpassword2);
        password_check = (TextView) findViewById(R.id.password_check);
        password2_check = (TextView) findViewById(R.id.password2_check);
        register = findViewById(R.id.register);


        Intent intent = getIntent(); /*데이터 수신*/
        email = intent.getExtras().getString("email"); /*String형*/
        Log.e("인텐트 값 확인",email);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pw && pw2) {
                    String password= etpassword.getText().toString();
                    Changepw();


//                    Toast.makeText(getApplicationContext(), "비밀번호 변경 완료", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(ChangepwActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    finish();


                } else {
                    CheckDialog();
                }

            }
        });


        etpassword.addTextChangedListener(new TextWatcher() {
            //비밀번호 확인 edittext 에서 텍스트가 변경될 때
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // edit text 에서 값 가져오는 과정 꼭 해주기 !!
                String password = etpassword.getText().toString();

                if (!password.matches(passwordValidation)) {
                    password_check.setText("문자, 숫자, 기호를 조합하여 8자 이상을 사용하세요");
                    pw = false;


                } else {
                    password_check.setText("");
                    pw = true;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        etpassword2.addTextChangedListener(new TextWatcher() {
            //비밀번호 확인 edittext 에서 텍스트가 변경될 때
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

                String password2 = etpassword2.getText().toString();
                // edit text 에서 값 가져오는 과정 꼭 해주기 !!
                String password = etpassword.getText().toString();

                if (!password2.equals(password)) {
                    password2_check.setText("일치하지 않은 비밀번호 입니다.");
//                    Log.e("1", password2+password);
                    pw2 = false;
                } else {
                    password2_check.setText("");
//                    Log.e("2", password2+password);
                    pw2 = true;

                }

            }
        });


    }

    public void Changepw(){
        final String password = etpassword.getText().toString().trim();

        InterfacePWChange api = ApiClient.getApiClient().create(InterfacePWChange.class);
        Call<String> call = api.getUserPW(email,password);
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
                    Toast.makeText(getApplicationContext(), "비밀번호 변경 완료", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangepwActivity.this, ActivityLogin.class);
                    startActivity(intent);
                    finish();
                Log.e(TAG, "비밀번호 변경 성공");

            }

            else
            {
                String message=jsonObject.getString("message");
                Toast.makeText(getApplicationContext(),message , Toast.LENGTH_SHORT).show();
            }
        }


        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void CheckDialog() {//아이디 비번 입력 알람창
        Log.e("다이얼로그", "1");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("비밀번호를 다시 확인해주세요");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

}