package com.example.test_retrofit.group;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.test_retrofit.ActivityHome;
import com.example.test_retrofit.R;
import com.example.test_retrofit.chat.ActivityChatRoom;
import com.example.test_retrofit.group.DTO.DTOMessage;
import com.example.test_retrofit.group.retrofit_Interface.Interface_postGroup;
import com.example.test_retrofit.user.ApiClient;
import com.example.test_retrofit.user.PreferenceHelper;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_madeGroup extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private PreferenceHelper preferenceHelper;

    EditText title, cafe, dateText, time, people;
    View location, calendar, clock;
    Button register;
    private ImageView gallery, chat_image;

    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    //TimePicker을 사용하여 사용자에게 시간을 묻는 대화상자
    private String road_address;
    private String cafe_result;
    private Integer mapx;
    private Integer mapy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        preferenceHelper = new PreferenceHelper(this);
//        꼭 해줘야 한다.!


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_made_group);

        title = findViewById(R.id.title);
        cafe = findViewById(R.id.cafe);
        dateText = findViewById(R.id.date);
        time = findViewById(R.id.time);
        people = findViewById(R.id.people);
        location = findViewById(R.id.location);
        calendar = findViewById(R.id.calendar);
        clock = findViewById(R.id.clock);
        register = findViewById(R.id.register);
//        gallery = findViewById(R.id.gallery);
//        chat_image = findViewById(R.id.chat_image);

        // 카페 검색해서 클릭한 결과물 받아와서 et 에 세팅 해주는 과정

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_madeGroup.this, Activity_findCafe.class);
                startActivityForResult(intent,0);
                // FindCafeActivity에서 값을 받아 온다.
                //두번째 인자 값은 여러 여러 액티비티들이 있을 경우 어떤 액티비티인지 식별하는 값이다.
            }
        });


        people.addTextChangedListener(new TextWatcher() {
            //모임 인원수 적는 et의 텍스트가 변경될 때
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

//            @SuppressLint("ResourceType")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //텍스트가 변경될 때마다 함수 호출

                if(people.getText().toString().length()>0) {
                    // 작성되어 있던 값을 지울 때 pareInt(" ") 에서 에러가 나기 때문에 위와 같은 과정 필요

                    if (Integer.parseInt(people.getText().toString()) > 20) {
                        // 20명 초과일 때
                        people.setText("20");
                        //et가 20으로 변경됨

                    }

                    if (Integer.parseInt(people.getText().toString()) < 2) {
                        // 2 미만일 때
                        people.setText("2");
                        // et가 2로 변경됨

                    }
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });


        calendar.setOnClickListener(new View.OnClickListener() {
            //캘린더 이미지가 클릭될 때 안드로이드 기본 달력이 뜬다.
            @Override
            public void onClick(View view) {
                // 클릭 시 달력 다이얼로그 생성

                Calendar calendar = Calendar.getInstance();
                //getInstance 는 현재 날짜를 선택값의 기본값으로 사용
                int pYear = calendar.get(Calendar.YEAR);
                int pMonth = calendar.get(Calendar.MONTH);
                int pDay = calendar.get(Calendar.DAY_OF_MONTH);
                //사용작 달력해서 선택한 날짜를 변수에 저장 해주는 듯

                datePicker = new DatePickerDialog(Activity_madeGroup.this,
                        new DatePickerDialog.OnDateSetListener() {
                    // 사용자가 날짜 선택을 끝냈음을 인식하는 리스너 함수
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                // 사용자가 날짜 선택을 끝내고 완료버튼을 눌렀을 때 실행되는 함수
                                // 사용자가 선택한 날짜 연, 달, 일이 이 함수의 인자로 넘어 간다.
                                month = month +1;
                                String date = year + "/" + month + "/" + day;
                                dateText.setText(date);
                                //선택한 날짜를 et에 세팅해주는 과정
                            }
                        }, pYear,pMonth,pDay);
                //다이얼로그의 인자 값으로 context, 날짜 선택을 끝냈음을 인식한 리스너 함수 구현체,
                //달력 다이얼로그를 띄울 때 표시될 날짜들을 인자로 넘겨준다.

                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000 );
                //지난날짜 비활성화
                datePicker.show();

            } // onClick
        });

        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                //getInstance 는 현재 날짜를 선택기의 기본값으로 사용
                int pHour = calendar.get(Calendar.HOUR);
                int pMinute = calendar.get(Calendar.MINUTE);

                timePicker = new TimePickerDialog(Activity_madeGroup.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        String pTime = hour+":"+min;
                        time.setText(pTime);
                        //선택한 시간을 et에 세팅해주는 과정
                    }
                },pHour, pMinute, true);
                //마지막 boolean 값은 시간을 24시간으로 보일지 아닐지

                timePicker.show();


            }
        });

        //글 등록하기 버튼을 누를 때
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etitle, ecafe, edateText, etime;
                String epeople;

                etitle = title.getText().toString();
                ecafe = cafe.getText().toString();
                edateText = dateText.getText().toString();
                etime = time.getText().toString();
                epeople = people.getText().toString();


                if(etitle.isEmpty() || ecafe.isEmpty()|| edateText.isEmpty()|| etime.isEmpty()|| epeople.isEmpty())
                {// 모임 작성 글 폼에서 sp에  빈 값이 있었을 때
                    Log.e("빈 상태","빈 상태");
                    CheckDialog("작성폼을 모두 입력해주세요");
                    Log.e("빈 상태","빈 상태");
                }

                else {
                    postGroup();
                    //서버 통신하는 함수
                }



            }
        });

    }


    private void postGroup()
    {
        final String etitle = title.getText().toString().trim();
        final String ecafe = cafe.getText().toString().trim();
        final String edateText = dateText.getText().toString().trim();
        final String etime = time.getText().toString().trim();
        final String epeople = people.getText().toString().trim();
        final String eaddress =road_address;
        final Integer emapx =mapx;
        final Integer emapy =mapy;
        final String host = preferenceHelper.getID();

        Log.e(TAG,etitle+"/"+ecafe+"/"+edateText+"/"+etime+"/"
                +epeople+"/"+eaddress+"/"+mapx+"/"+mapy +"/"+host);

        Interface_postGroup post = ApiClient.getApiClient().create(Interface_postGroup.class);
        Call<DTOMessage> call = post.post_MakeGroup(host,ecafe,etitle,edateText,
                etime,epeople,eaddress,mapx,mapy);



        call.enqueue(new Callback<DTOMessage>() {

            @Override
            public void onResponse(Call<DTOMessage> call, Response<DTOMessage> response) {

                Log.e(TAG, "응답1 =" + response.body());
                Log.e(TAG, "응답2 =" + response);

                if (response.isSuccessful() && response.body() != null)
                {

                    Boolean status = response.body().getStatus();
                    String message = response.body().getMessage();
                    Log.e ("onSuccess", String.valueOf(response.body()));

                    if(status)
                    {
                        Toast.makeText(Activity_madeGroup.this, message,Toast.LENGTH_LONG);
                        Intent intent= new Intent(Activity_madeGroup.this, ActivityChatRoom.class);
                        startActivity(intent);
                        finish();
                    }

                    else
                    {
                        CheckDialog(message);
                    }


                }
            }

            @Override
            public void onFailure(Call<DTOMessage> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());

                CheckDialog("실패");
            }
        });
    }

    //결과값 받는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 인자 설명 1: 액티비티 식별값, 2:setResult()에서 보낸 값, 3: putExtra() 를 통해 인텐트에서 보내준 값

//        Log.e(TAG, String.valueOf(resultCode)+String.valueOf(resultCode)+String.valueOf(data));
        Log.e(TAG,"onActivityResult 시작");
        if(requestCode == 0){
            Log.e(TAG,"requestCode 시작");
            if(resultCode ==RESULT_OK)
            {
                Log.e(TAG,"RESULT_OK 시작");
                road_address = data.getStringExtra("road_address");
                cafe_result = data.getStringExtra("cafe");
                mapx = data.getIntExtra("mapx",0);
                mapy = data.getIntExtra("mapy",0);
//                받는 값이 int면 만약 오는 값이 없을 때 저장되는 기본값을 설정 꼭 해줘야 한다.
                Log.e(TAG,road_address + cafe_result+ mapx+mapy);
                cafe.setText(Html.fromHtml(cafe_result));
            }
        }

    }

    public void CheckDialog(String a) {//아이디 비번 입력 알람창
        Log.e("다이얼로그", "1");
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