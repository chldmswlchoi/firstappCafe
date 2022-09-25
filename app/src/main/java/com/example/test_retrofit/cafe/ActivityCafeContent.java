package com.example.test_retrofit.cafe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.test_retrofit.R;
import com.example.test_retrofit.Retrofit.NetWorkHelper;
import com.example.test_retrofit.group.DTO.DTOMessage;
import com.example.test_retrofit.user.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCafeContent extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;

    private PreferenceHelper preferenceHelper;
    public static String login_id;
    public static String login_nickname;
    public static String profile;
    public Integer cafe_id, f_id;
    private ArrayList<String> images;

    private TextView cafe_name, tag, info, review;

    FragmentCafeDetailData fragmentCafeDetailData;

    //    수정,삭제 관련 변수 선언
    private LinearLayout ButtonGroup;
    private Button update, delete;


    //    카페 글 수정하기 버튼 눌렀을 때 인텐트로 전달할 값
    private String i_title, i_review, i_cafe, i_address, i_number, i_fnumber,
            i_last_order, i_dessert, i_tema, i_url;

    // 리뷰 프래그먼트에 전달할 작성자의 닉네임, 프로필
    private String wnickname,wprofile;

    private Integer i_mapx, i_mapy, i_area;

    public static ActivityCafeContent activityCafeContent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.test_retrofit.R.layout.activity_cafe_content);

        activityCafeContent = this;
        sliderViewPager = findViewById(R.id.sliderViewPager);
        layoutIndicator = findViewById(R.id.layoutIndicators);
        cafe_name = findViewById(R.id.cafe_name);
        tag = findViewById(R.id.tag);
        info = findViewById(R.id.info);
        review = findViewById(R.id.review);
        ButtonGroup = findViewById(R.id.button_group);

        update = findViewById(R.id.cafe_update);
        delete = findViewById(R.id.cafe_delete);


        preferenceHelper = new PreferenceHelper(this);
        images = new ArrayList<String>();
        fragmentCafeDetailData = new FragmentCafeDetailData();


        login_id = preferenceHelper.getID();
        login_nickname = preferenceHelper.getNickname();
        profile = preferenceHelper.getProfile();

//        카페 리스트에서 인텐트로 넘겨준 값 받아오기
        cafe_id = getIntent().getIntExtra("cafe_id", -1);
        f_id = getIntent().getIntExtra("f_id", -1);
//        writer = getIntent().getStringExtra("writer");
        Log.e(TAG, cafe_id.toString() + "/" + f_id.toString());
        preferenceHelper.putCafeId(cafe_id);
//        카페 리스트에서 인텐트로 넘겨준 값 받아오기


        getDessertList();
//      --------------------프래그먼트 초기 세팅 화면 세팅해주기-----------------------------
        review.setEnabled(true);
        review.setTextColor(Color.parseColor("#FF000000"));
        info.setTextColor(Color.parseColor("#F6875109"));
        info.setEnabled(false);


//        처음에 어떤 프레그먼트를 보여줄지 세팅하는 과정
        Fragment fragment_info = new FragmentCafeDetailData();
        Fragment fragment_review = new FragmentCafeReview();
        getSupportFragmentManager().beginTransaction().replace(R.id.cafe_inf, fragment_info).commitAllowingStateLoss();
//        처음에 어떤 프레그먼트를 보여줄지 세팅하는 과정

        //      --------------------토글 버튼 -------------------------------

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                review.setEnabled(true);
                review.setTextColor(Color.parseColor("#FF000000"));
                info.setTextColor(Color.parseColor("#F6875109"));
                info.setEnabled(false);
                Log.e(TAG, "tbCafe 클릭");
                getSupportFragmentManager().beginTransaction().replace(R.id.cafe_inf, fragment_info).commitAllowingStateLoss();

            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setEnabled(true);
                info.setTextColor(Color.parseColor("#FF000000"));
                review.setTextColor(Color.parseColor("#F6875109"));
                review.setEnabled(false);
                Log.e(TAG, "리뷰 프래그먼트로 화면 전환 및 값 전달");

                //프래그먼트에 값을 전달하는 과정
                Bundle bundle = new Bundle(3);
                //전달하려는 값의 개수
                bundle.putString("review", i_review);
                bundle.putString("nickname", wnickname);
                bundle.putString("profile", wprofile);
                fragment_review.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.cafe_inf, fragment_review).commitAllowingStateLoss();

            }
        });


        sliderViewPager.setOffscreenPageLimit(1);
//      viewpager를 사용할 때 이전 혹은 다음페이지를 몇개까지 미리 로딩할지 정하는 함수
//        sliderViewPager.setAdapter(new AdapterImageSlider(this, images));
//        adapter 연결
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
//            새 페이지가 선택될때 실행되는 코드
                super.onPageSelected(position);
                setCurrentIndicator(position);
//            선택된 페이지 동그라미 색깔 다르게 하기
            }
        });


//        버튼 클릭 관련 메서드
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ActivityCafeContent.this, ActivityModifyCafe.class);
                intent.putExtra("cafe_id", cafe_id);
                intent.putExtra("review", i_review);
                intent.putExtra("title", i_title);
                intent.putExtra("cafe", i_cafe);
                intent.putExtra("road_address", i_address);
                intent.putExtra("mapx", i_mapx);
                intent.putExtra("mapy", i_mapy);
                intent.putExtra("area", i_area);
                intent.putExtra("number", i_number);
                intent.putExtra("fnumber", i_fnumber);
                intent.putExtra("last_order", i_last_order);
                intent.putExtra("food", f_id);
                intent.putExtra("dessert", i_dessert);
                intent.putExtra("tema", i_tema);
                intent.putExtra("url", i_url);
                intent.putExtra("images", images);

                startActivity(intent);

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCafeContent.this);
                builder.setTitle("삭제하기").setPositiveButton("삭제하기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteGroupData();

                            }
                        })
                        .setNeutralButton("취소", null).show();

            }
        });

    }


    void getDessertList() {

        Call<DTOCafe> call = NetWorkHelper.getInstance().getApiService().getCafeContent(cafe_id, login_id);
        call.enqueue(new Callback<DTOCafe>() {
            @Override
            public void onResponse(Call<DTOCafe> call, Response<DTOCafe> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "서버에서 받아온 값 형태 " + response + response.body());
                    DTOCafe DTOGroupResponse = response.body();
                    Log.e(TAG, "응답 성공 후 dto 파싱해준 값1" + DTOGroupResponse);

                    if (DTOGroupResponse.getStatus().equals("true")) {
                        List<DTOCafe.CafeData> items = DTOGroupResponse.getData();
                        Log.e(TAG, "응답 성공 후 dto 파싱해준 값2" + items);

                        generateDataList(items);
                    } else {
                        EmptyDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }
                } else {
                    Log.e(TAG, "실패" + response.body());
                }

            }

            @Override
            public void onFailure(Call<DTOCafe> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });

    }

    //    리스트에서 값이 변경되었을 때, 액티비티 리스트 값 변경 후 어댑터에게 알리는 역할.
    @SuppressLint("SetTextI18n")
    private void generateDataList(List<DTOCafe.CafeData> items) {

        Log.e(TAG, "generateDataList = " + images.size());
        List<String> ImageList = new ArrayList<>();
        ImageList = items.get(0).getImage_array();


        for (int i = 0; i < ImageList.size(); i++) {
            Log.e(TAG, "for 문 1" + ImageList + items.get(0).getImage_array());
            images.add(ImageList.get(i));
            Log.e(TAG, "for 문 2" + ImageList + items.get(0).getImage_array().size());
        }
        if (ImageList.get(0).isEmpty()) {
            images.clear();
        } else {
            Log.e(TAG, " 비지 않았을 때 " + images.size());
            sliderViewPager.setAdapter(new AdapterImageSlider(this, images));
            setupIndicators(images.size());
        }
//         사진의 숫자만큼 동그란 점 만들기


//        텍스트 세팅 과정
        cafe_name.setText(items.get(0).getCafe());
        tag.setText("#" + items.get(0).getT_name() + "   " + "#" + items.get(0).getD_name());


        if (login_id.equals(items.get(0).getId())) // 글 작성자와 로그인한 사람의 아이디가 같으면
        {
            ButtonGroup.setVisibility(View.VISIBLE); // 수정,삭제 버튼을 보이게 한다.

        }

//        수정버튼 클릭시 인텐트로 넘겨줄 변수들 세팅 과정

        i_title = items.get(0).getTitle();
        i_review = items.get(0).getReview();
        i_cafe = items.get(0).getCafe();
        i_address = items.get(0).getRoad_address();
        i_number = items.get(0).getPhone_number();
        i_fnumber = i_number.substring(0, i_number.indexOf("-"));
        i_number = i_number.substring(i_number.indexOf("-") + 1);
//        앞 숫자 제외하고 전화번호 세팅
        i_last_order = items.get(0).getLast_order().toString();
        i_dessert = items.get(0).getD_name();
        i_tema = items.get(0).getT_name();
        i_url = items.get(0).getSns_url();

        i_mapx = items.get(0).getMapx();
        i_mapy = items.get(0).getMapy();
        i_area = items.get(0).getA_id();

        wnickname = items.get(0).getNickname();
        wprofile = items.get(0).getProfile();

        Log.e(TAG, "인텐트 변수 세팅 값들");
        Log.e(TAG, i_title + "/" + i_review + "/" + i_cafe + "/" + i_address + "/" + i_number + "/" + i_fnumber + "/" +
                i_last_order + "/" + i_dessert + "/" + i_tema + "/" + i_url
                + "/" + i_mapx.toString() + "/" + i_mapy.toString() + "/" + i_area.toString());
    }

    //    레트로핏 통해 모임 게시글 삭제 하는 함수

    void deleteGroupData() {
//        인자 값으로 롱클릭한 아이템의 리스트 위치 받음
        Call<DTOMessage> call = NetWorkHelper.getInstance().getApiService().deleteCafe(cafe_id);
        call.enqueue(new Callback<DTOMessage>() {
            @Override
            public void onResponse(Call<DTOMessage> call, Response<DTOMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean status = response.body().getStatus();
                    String message = response.body().getMessage();
                    Log.e("onSuccess", String.valueOf(response.body()));

                    if (status) {
                        if (Activity_dessertList.activity != null) // 액티비티가 살아있다면
                        {
                            Activity_dessertList activity = Activity_dessertList.activity;
                            activity.finish();
//                            액티비티 종료 해주기
                        }

                        Intent intent = new Intent(ActivityCafeContent.this, Activity_dessertList.class);
                        intent.putExtra("f_id", f_id);
                        startActivity(intent);
                        finish();
                    } else {
                        EmptyDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }

                } else {
                    Log.e(TAG, "실패" + response.body());
                }
            }

            @Override
            public void onFailure(Call<DTOMessage> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
                EmptyDialog("실패 다시 시도해주세요");
            }

        });
    }

    // 사진의 숫자만큼 동그란 점 만들기
    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.bg_indicator_inactive));
//            setImageDrawable : 이미지뷰의 내용을 drawable로 설정한다
//            매개 변수 : 설정할 drawable 또는 내용을 지우려면 null

//            drawable이란 ? 리소스를 그리는데 사용할 수 있는 개체

//            getDrawable : 특정 resource id 와 연결된 drawable 개체를 반환한다.
//            매개변수 : id(리소스 식별자)는 패키지, 유형 및 리소스 항복을 인코딩 한다.
            indicators[i].setLayoutParams(params);
//            뷰와 관련된 레이아웃 매개변수를 설정한다. 그리고
//            이 뷰들이 어떻게 정렬될지 지정하는 부모에게 매개변수를 제공한다.
            layoutIndicator.addView(indicators[i]);
//            자식 뷰를 추가한다. 만약 자식뷰에 레이아웃 매개변수가 이미 설정되어 있지 않으면
//            뷰그룹에 대한 기본 매개변수가 자식에 설정된다.
//            매개변수 : 추가할 자식 뷰
        }
        setCurrentIndicator(0);
    }

    //  선택된 페이지 색상 다르게 하기
    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
//        그룹의 자식 수를 양의 정수로 반환한다.
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
//            getChildAt() :그룹에 있는 지정된 위치에 이쓴 뷰를 반환한다.
//            매개변수 : index: 뷰를 가져올 위치
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }

    public void EmptyDialog(String a) {//아이디 비번 입력 알람창
        Log.e("다이얼로그", "1");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(a);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            //            확인 버튼 클릭시 실행하는 코드 onclick 안에 넣기
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }
}