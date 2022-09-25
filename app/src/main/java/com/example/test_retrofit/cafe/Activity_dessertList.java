package com.example.test_retrofit.cafe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.test_retrofit.R;
import com.example.test_retrofit.Retrofit.NetWorkHelper;
import com.example.test_retrofit.user.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_dessertList extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private AdapterDessertList adapterDessertList;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<DTOCafe.CafeData> dessertList;

    private PreferenceHelper preferenceHelper;
    private String login_id;
    //    카페 activity 에서 보내준 값

    private Integer f_id;
    private Integer is_loved;
    //    스피너
    private Spinner select_dessert, select_tema;
    private ArrayAdapter dadapter;
    //    스피너에서 가져온 값
    private String gdessert, gtema;

    //    상단바 글씨
    TextView topText;
    private Integer spinner_selectTema_num = 0;
    private Integer spinner_selectDessert_num = 0;

    public static Activity_dessertList activity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.test_retrofit.R.layout.activity_dessert_list);

        activity = this;

//       초기 세팅 -------- 객체생성, 바인딩 해주는 작업 & sp 에서 유저 id 가져오는 작업-------------

        recyclerView = findViewById(R.id.dessert_list);

        preferenceHelper = new PreferenceHelper(this);
        dessertList = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterDessertList = new AdapterDessertList(dessertList);
        recyclerView.setAdapter(adapterDessertList);

        select_dessert = findViewById(R.id.select_dessert);
        select_tema = findViewById(R.id.select_tema);

        topText = findViewById(R.id.f_id);
        f_id = getIntent().getIntExtra("f_id", -1);

        login_id = preferenceHelper.getID();

        //       -------- 스피너 관련 -------------

        String[] tema = {"테마 선택 없음", "데이트하기 좋은", "조용한", "공원카페", "모임하기 좋은", "특별한 날",
                "야외카페", "감성카페", "인스타카페", "대형카페", "주택개조", "로스팅 직접 하는"};

        String[] dessert = {"디저트 선택 없음", "일반", "베이커리", "전통 디저트", "도넛", "와플", "마카롱", "케이크"
                , "타르트", "초콜릿", "쿠키", "마들렌", "카눌레", "스콘", "머랭쿠키", "푸딩", "아이스크림", "빙수", "젤라또"};

        String[] brunch = {"디저트 선택 없음", "일반", "샌드위치", "샐러드", "토스트", "요거트", "팬케이크", "베이글"};

        String[] fruit = {"디저트 선택 없음", "과일라떼", "과일스무디", "과일에이드", "과일주스", "생과일", "야채주스"};

        String[] vegan = {"디저트 선택 없음", "비건", "키토"};

        ArrayAdapter<String> tadapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, tema);

        select_tema.setAdapter(tadapter);


        //       -------- 스피너 인텐트로 받은 값에 따라 디저트 스피너 세팅 해주는 과정-------------
        switch (f_id) {
            case 1:
                setDessertSpinner(dessert);

                break;
            case 2:
                topText.setText("브런치");
                setDessertSpinner(brunch);
                break;
            case 3:
                topText.setText("과일/야채");
                setDessertSpinner(fruit);
                break;
            case 4:
                topText.setText("비건");
                setDessertSpinner(vegan);
                break;

            default:
                break;
        }


//       초기 세팅 -------- 객체생성, 바인딩 해주는 작업 -------------

        getDessertList();
//       -------- 아이템 클릭 -------------

        adapterDessertList.setOnItemClickListener(new AdapterDessertList.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(Activity_dessertList.this,ActivityCafeContent.class);
                intent.putExtra("cafe_id",dessertList.get(position).getId_cafe());
                intent.putExtra("f_id",f_id);
                intent.putExtra("writer",dessertList.get(position).getId());
                startActivity(intent);

            }

            @Override
            public void toggleLoveButton(View v, int position) {
                String p = String.valueOf(position);
//                Toast.makeText(Activity_dessertList.this, p, Toast.LENGTH_LONG).show();
//               서버에 보낼 게시글의 고유 아이디, 좋아요 여부 상태 리스트로부터 값 받아 초기화 하기
                Integer cafe_id = dessertList.get(position).getId_cafe();
                is_loved = dessertList.get(position).getIs_loved();
                Log.e(TAG, is_loved.toString());
                changeLoveCafe(login_id, cafe_id, is_loved, position);

            }
        });


        //       -------- 스피너 클릭 -------------

        select_tema.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0 && spinner_selectTema_num > 0) {
                    gtema = select_tema.getSelectedItem().toString();
                    Log.e(TAG, "디저트 카테고리1 스피너 선택 결과" + gtema);
                    gtema = "";
                    Log.e(TAG, "디저트 카테고리1 스피너 선택 결과" + gtema);
                    getDessertListFilter();
                } else {
                    gtema = select_tema.getSelectedItem().toString();
                    Log.e(TAG, "디저트 카테고리1 스피너 선택 결과" + gtema);
                    getDessertListFilter();
                    spinner_selectTema_num++;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //       -------- 스피너 클릭 -------------
    }

    void getDessertList() {

        Call<DTOCafe> call = NetWorkHelper.getInstance().getApiService().getDessertList(f_id, login_id);
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


    //    디저트에서 필터 적용후 결과 가져오는 함수
    void getDessertListFilter() {

        Log.e(TAG, "getDessertListFilter()");

        if (spinner_selectDessert_num == 0 || spinner_selectTema_num == 0) {
            Log.e(TAG, "처음 리스트 데이터 값 가져올 때는 카테고리 선택 없음 통신 안하기 위해");
            return;
        }
        Log.e(TAG, "통신 시작 조건 확인 후 어떤 통신을 할 것인지 선택됨");

        if (gdessert.isEmpty() && gtema.isEmpty()) {
            Log.e(TAG, "후에 둘다 카테고리 선택 없음 선택 시");
            Log.e(TAG + "디저트 필터 레트로핏 함수 중 카테고리 둘다 없는 경우 : ", "모든 값을 가져오는 레트로핏 함수 실행");
            getDessertList();
        } else {
            Log.e(TAG + "카테고리가 둘 중 하나라도 있는 경우", "카테고리 레트로핏 통신");
            if ("디저트 선택 없음".equals(gdessert)) {
                gdessert = "";
                Log.e(TAG + "디저트 선택 없음 선택 시 빈 값으로 변경", gdessert);
            } else if ("테마 선택 없음".equals(gtema)) {
                gtema = "";
                Log.e(TAG + "테마 선택 없음 선택 시 빈 값으로 변경", gtema);
            }
            Call<DTOCafe> call = NetWorkHelper.getInstance().getApiService().getDessertList_filter(gdessert, gtema, f_id, login_id);
            call.enqueue(new Callback<DTOCafe>() {
                @Override
                public void onResponse(Call<DTOCafe> call, Response<DTOCafe> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        Log.e(TAG, "카테고리 서버에서 받아온 값 형태 " + response);
                        Log.e(TAG, "카테고리 서버에서 받아온 값 형태 2 " + response.body());
                        DTOCafe DTOGroupResponse = response.body();
                        Log.e(TAG, "카테고리 응답 성공 후 dto 파싱해준 값1" + DTOGroupResponse);

                        if (DTOGroupResponse.getStatus().equals("true")) {
                            List<DTOCafe.CafeData> items = DTOGroupResponse.getData();
                            Log.e(TAG, "카테고리 응답 성공 후 dto 파싱해준 값2" + items);
                            generateDataList(items);
                        } else {
                            EmptyDialog("카테고리에 해당하는 카페가 없습니다");
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

    }


    //    리스테에서 값이 변경되었을 때, 액티비티 리스트 값 변경 후 어댑터에게 알리는 역할.
    private void generateDataList(List<DTOCafe.CafeData> items) {
        Log.e(TAG, "items" + items);
        dessertList.clear();
        for (int i = 0; i < items.size(); i++) {
            dessertList.add(items.get(i));
        }
        adapterDessertList.notifyDataSetChanged();
    }


    //    디저트 스피너 세팅해주는 함수
    public void setDessertSpinner(String[] resource) {

        dadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, resource);
        select_dessert.setAdapter(dadapter);

        select_dessert.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0 && spinner_selectDessert_num > 0) {
                    gdessert = select_dessert.getSelectedItem().toString();
                    Log.e(TAG, "디저트 카테고리1 스피너 선택 결과" + gdessert);
                    gdessert = "";
                    Log.e(TAG, "디저트 카테고리1 스피너 선택 결과" + gdessert);
                    getDessertListFilter();

                } else {
                    gdessert = select_dessert.getSelectedItem().toString();
                    Log.e(TAG, "디저트 카테고리1 스피너 선택 결과" + gdessert);
                    getDessertListFilter();
                    spinner_selectDessert_num++;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void changeLoveCafe(String login_id, Integer cafe_id, Integer is_loved, Integer position) {
        Log.e(TAG, "changeLoveCafe");
        Call<DTOCafe> call = NetWorkHelper.getInstance().getApiService().postLoveCafe(login_id, cafe_id, is_loved);
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
                        Log.e(TAG, dessertList.get(position).getIs_loved().toString());
//                        db 값 변경 완료된 후 리스트의 값 변경해주는 함수
                        changeIs_loved(position, is_loved);

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

    public void changeIs_loved(Integer position, Integer is_loved) {
        if (is_loved == 0) {
//            0 -> 1(좋아요)가 되고 db 값이 변경된 후 이므로 리스트의 값 변경 해준다.
            dessertList.get(position).setIs_loved(1);
            Log.e(TAG + "cangeIs_loved 0 ->1 :", dessertList.get(position).getIs_loved().toString());
        } else {
            dessertList.get(position).setIs_loved(0);
            Log.e(TAG + "cangeIs_loved 1 ->0 :", dessertList.get(position).getIs_loved().toString());
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