package com.example.test_retrofit.user;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test_retrofit.ActivityHome;
import com.example.test_retrofit.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_signup extends AppCompatActivity {

    public final String TAG = "MAINACTIVITY2";

    private EditText etnickname, etid, etpassword, etpassword2;
    TextView email_check,nickname_check,password_check,password2_check;
    private Button btnregister;
    private PreferenceHelper preferenceHelper;
    private String passwordValidation ="^.*(?=^.{8,}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$";
    boolean pw1=false;
    boolean pw2 =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        preferenceHelper = new PreferenceHelper(this);

        // 액티비티 요소들과 변수 연결?
        etnickname = (EditText) findViewById(R.id.etnickname);
        etid = (EditText) findViewById(R.id.etemail);
        etpassword = (EditText) findViewById(R.id.etpassword);
        etpassword2= (EditText) findViewById(R.id.etpassword2);

        email_check= (TextView) findViewById(R.id.email_check);
        nickname_check= (TextView) findViewById(R.id.nickname_check);
        password_check= (TextView) findViewById(R.id.password_check);
        password2_check= (TextView) findViewById(R.id.password2_check);
        btnregister = (Button) findViewById(R.id.btn);


        etpassword.addTextChangedListener(new TextWatcher() {
            //비밀번호 확인 edittext 에서 텍스트가 변경될 때
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // edit text 에서 값 가져오는 과정 꼭 해주기 !!
                String password = etpassword.getText().toString();

                if(!password.matches(passwordValidation)){
                    password_check.setText("문자, 숫자, 기호를 조합하여 8자 이상을 사용하세요");
                    pw1=false;
                }

                else {
                    password_check.setText(" ");
                    pw1=true;
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

                if(!password2.equals(password)){
                    password2_check.setText("일치하지 않은 비밀번호 입니다.");
//                    Log.e("1", password2+password);
                    pw2=false;
                }

                else{
                    password2_check.setText("");
//                    Log.e("2", password2+password);
                    pw2=true;

                }

            }
        });



        btnregister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = etid.getText().toString();
                String password = etpassword.getText().toString();
                String nickname = etnickname.getText().toString();
                String password2 = etpassword2.getText().toString();
                if(email.isEmpty() || password.isEmpty()||nickname.isEmpty()||password2.isEmpty()) {// 아이디 또는 비번을 입력하지 않았을 때
                    Log.e("빈 상태", "빈 상태");
                    EmptyDialog("필수 인자 값을 모두 입력해주세요");
                    Log.e("빈 상태", "빈 상태");
                }

                if(!pw1&&pw2)
                {

                    EmptyDialog("잘못된 비밀번호 형식입니다");
                }

                else {
                    registerMe();
                }
            }
        });

    }



    private void registerMe()
    {

        email_check.setText("");
        nickname_check.setText("");

        Log.e(TAG, "에러 =" + "버튼 클릭");
        final String nickname = etnickname.getText().toString();
        //etnickname 에 있는 값 가져와서 string으로 변환
        final String id = etid.getText().toString();
        final String password = etpassword.getText().toString();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                //객체 생성해 초기 설정하는 과정
                .baseUrl(RegisterInterface.REGIST_URL)
                // 서버 연동을 위한 baseurl  설정, 호출하는 주소를 고정 값으로 세팅하면 url 호출 시 반복 줄여줌

                .addConverterFactory(ScalarsConverterFactory.create())
                //json 은 자바에서 바로 사용할 수 있는 데이터 형식이 아니기 때문에 이를 변환해주기 위해
                // 데이터를 변환해줄 컨버터를 사용해야 한다.
                //.addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        Log.e(TAG, "에러 =" + "버튼 클릭2");

        //등록 인터페이스

        // 이 부분 이해 안가네 왜 객체 생성 했는데 new 안해주지? .class  왜 붙인거야??
        RegisterInterface api = retrofit.create(RegisterInterface.class);
        Log.e(TAG, "에러 =" + "버튼 클릭2");
        //왜 또 인터 페이스 Call 사용한거지?
        Call<String> call = api.getUserRegist(nickname, id, password);
        Log.e(TAG, "에러 =" + "버튼 클릭2");
        //입력 해준 회원 정보를 넘겨준다.

        call.enqueue(new Callback<String>() {
            //2가지  enqueue 비동기 방식, execute 동기 방식
            //enequeue 해주면 네트워크 통신이 된다.
            //이 함수는 1.요청을 비동기적으로 보내고 응답을 콜백에게 알린다.
            //2.만약 서버 통신, 요청 생성, 응답을 처리하는 중에 오류가 발생하면 알려준다
            //Callback : 서버 또는 오프라인 요청이 오면 응답을 해준다.
            @Override
            public void onResponse(@NonNull  Call<String> call, @NonNull Response<String> response) {
                //onResponse 요청에 대한 응답이 오면
                Log.e(TAG, "응답 =" + response.body());
                Log.e(TAG, "응답 =" + response);
                if(response.isSuccessful() && response.body() != null)
                    //onRsponse 가 실행되었다고 해서 무조건 성공한건 아니다 실패코드에도 호출이 되므로
                    // isSuccessful() 확인이 필요하다
                //응답이 성공이고 역직렬화(j->object) 된 응답 본문이 null 이 아니면
                {
                    Log.e("onSuccess", response.body());
                    String jsonResponse = response.body();
                    //응답 결과 인듯

                    try
                    {
                        parseRegData(jsonResponse);


                        //응답 받은 데이터를 원하는 데이터 형식으로 변환해주는 함수
                    }
                    catch (JSONException e)
                    {
                        // 이건 또 뭐람
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "에러 =" + t.getMessage());
                //에러 메시지를 볼 수 있는 듯
            }
        });

    }

    private void parseRegData (String response) throws JSONException
    {//데이터 변환 해주는 과정
        Log.e(TAG, "데이터 변환 =" + "함수 시작");
        JSONObject jsonObject = new JSONObject(response);
        Log.e(TAG, "데이터 변환 =" + "json객체 생성");
        //json 객체를 생성하고 응답 밧은 결과 response 를 생성한 객체에 넣어준다

        if (jsonObject.optString("status").equals("true"))
        {//추측 -> 키 값이 status 이고 value 가 true 와 같다면함
            saveInfo(response);
            //sp에 회원가입완료한 회원 정보 저장해주는 함수
            Intent intent= new Intent(Activity_signup.this, ActivityHome.class);
            startActivity(intent);
            finish();
            //회원가입완료 홈화면이동
            Toast.makeText(Activity_signup.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
        }
        else
        {

            if( !jsonObject.isNull("message_email") )
            {//받아온 json 객체의 값이 null 이 아니면
                email_check.setText(jsonObject.getString("message_email"));
            }

            if(!jsonObject.isNull("message_nick")) {
                nickname_check.setText(jsonObject.getString("message_nick"));
            }

//            Log.e(TAG, "message =" + jsonObject.getString("message"));
//            Log.e(TAG, "message_email =" + jsonObject.getString("message_email"));
//            Log.e(TAG, "message_nick" + jsonObject.getString("message_nick"));
        }

    }

    private void saveInfo (String response)
    {Log.e(TAG, "sp 저장 =" + "함수 시작");
        preferenceHelper.putIsLogin(true);

        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.optString("status").equals("true"))
            //추측 -> 키 값이 status 이고 value 가 true 와 같다면
            {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                //json 에서 키에 대한 값이 array인 경우 JSonArray 사용해서 구별
                //jsonObject 에서 키 값이 data인 value(array 형태임) 값을 가져온다.

                for (int i = 0; i < dataArray.length(); i++) {
                    //dataArray 길이 만큼

                    JSONObject dataobj = dataArray.getJSONObject(i);
                    preferenceHelper.putNickname(dataobj.getString("nickname"));
                    preferenceHelper.putID(dataobj.getString("id"));
                    //회원가입 완료한 사람의 닉네임과 아이디를 sd 에 저장

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void EmptyDialog(String a)
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
