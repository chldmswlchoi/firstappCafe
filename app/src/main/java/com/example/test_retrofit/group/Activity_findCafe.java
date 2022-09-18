package com.example.test_retrofit.group;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.test_retrofit.R;
import com.example.test_retrofit.group.DTO.NSDTO;
import com.example.test_retrofit.group.retrofit_Interface.NS_ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_findCafe extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private String client_id ="jCfR6qxEXe70t890JIHN";
    private String client_pw ="ymsCOKa82I";

    private Button Find;
    private EditText Cafe;
    private String cafe;

    private NSAdpater nsAdpater;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private  List<NSDTO.NSItems> NSList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_cafe);

        Cafe = findViewById(R.id.cafe);
        Find = findViewById(R.id.find_cafe);

        NSList = new ArrayList<>();
        Log.e(TAG, "1hi"+ NSList);

        recyclerView = findViewById(R.id.cafe_list);
        Log.e(TAG, "1"+ "뷰 객체에 대하 참조");
        linearLayoutManager = new LinearLayoutManager(Activity_findCafe.this);
        Log.e(TAG, "4"+ "리사이클러뷰에 LinearLayoutManager 지정");
        recyclerView.setLayoutManager(linearLayoutManager);
        Log.e(TAG, "4"+ "리사이클러뷰에 LinearLayoutManager 지정");
        nsAdpater = new NSAdpater(NSList);
        Log.e(TAG, "2"+ "어댑터 객체 생성");

        recyclerView.setAdapter(nsAdpater);
        Log.e(TAG, "5"+ "리사이클러뷰에 어댑터 객체 지정");




        //카페명 입력하고 검색버튼 클릭했을 때
        Find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cafe = Cafe.getText().toString();
                getResultSearch();
                //레트로핏 이용해서 통신 하는 함수

            }
        });

        //아이템 클릭했을 때
        nsAdpater.setOnItemClickListener(new NSAdpater.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String cafe_result = NSList.get(position).getTitle();
//                list.get(index) get()는 리스트의 지정된 위치에 있는 요소를 반환한다
//                인자값 -> 반환할 요소의 인덱스 리턴값-> 지정된 위치에 있는 요소
                String road_address = NSList.get(position).getRoadAddress();
                Integer mapx = NSList.get(position).getMapx();
                Log.e(TAG, String.valueOf(NSList.get(position)));
                Log.e(TAG, NSList.get(position).getRoadAddress());
                Integer mapy = NSList.get(position).getMapy();

                Intent intent = new Intent();
                intent.putExtra("road_address",road_address);
                intent.putExtra("cafe",cafe_result);
                intent.putExtra("mapx",mapx);
                intent.putExtra("mapy",mapy);
                Log.e(TAG,"intent"+road_address+cafe_result+mapx+mapy);
                setResult(RESULT_OK,intent);
                // 새로 띄운 액티비티에서 이전 액티비티로 인텐트를 전달하고 싶을 때 사용하는 메서드
                finish();
            }
        });

    }

    //레트로핏 이용해서 통신하는 함수 네이버 api 지역 검색 결과 받아옴
    void getResultSearch(){
        NS_ApiInterface apiInterface = NSApiClient.getInstance().create(NS_ApiInterface.class);
        Call<NSDTO> call = apiInterface.getSearchResult(client_id,client_pw,"local.json",cafe,5,"random");
        // 인자값 클라이언트 id, 클라이언트 secret, 세부 요청 URL,검색명, 결과값, 정렬 옵션 : random(유사도순), comment(카페/블로그 리뷰 개수 순)
        call.enqueue(new Callback<NSDTO>() {
            @Override
            public void onResponse(Call<NSDTO> call, Response<NSDTO> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    NSDTO result = response.body();
                    Log.e(TAG,"성공"+result);
                    List<NSDTO.NSItems> items = result.getItems();
                    Log.e(TAG,"성공"+items);
                    generateDataList(items);
                    // 받아온 결과값을 인자로 넘겨주고 이 메서드는 어댑터에게 리사이클러뷰 데이터가 업데이트 되었다고 알려줌
                }
                else {
                    Log.e(TAG, "실패"+response.body());
                }
            }

            @Override
            public void onFailure(Call<NSDTO> call, Throwable t) {
                Log.e(TAG, "에러"+ t.getMessage());

            }
        });
    }

    private void generateDataList(List<NSDTO.NSItems>items)
    {
        Log.e(TAG, "items"+ items);
        NSList.clear();
//        어댑터랑 바인드된 리스트 값 모두 삭제해주기
        for (int i = 0; i < items.size(); i++) {

            NSList.add(items.get(i));
        }
        //어댑터에 바인드된 리스트에 아이템 추가해줌

        Log.e(TAG, "NSList"+ NSList);
        nsAdpater.notifyDataSetChanged();
//        데이터가 업데이트 되었지만 위치는 변하지 않을 때 사용


//        위와 같이 for 문을 통해 NsList add 해주면 어댑터에 바인드 해줬던 NSList 값이 변경된다. 하지만
//        NSList = items;
//        위의 방식은 로그를 찍어보면 NList가 새로 생성되는 것을 볼 수 있다. 짱신기 몰랐음
//        그러므로 원래 어댑터에 바인드 되어잇는 NSlist 의  값이 변경되는 것이 아니어서
//        notifyDataSetChanged 에서 값이 변경되지 않는다.




    }



}