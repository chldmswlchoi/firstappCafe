package com.example.test_retrofit.like;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.test_retrofit.R;
import com.example.test_retrofit.cafe.ActivityCafeContent;
import com.example.test_retrofit.cafe.Activity_dessertList;
import com.example.test_retrofit.cafe.AdapterDessertList;
import com.example.test_retrofit.cafe.DTOCafe;
import com.example.test_retrofit.cafe.NetWorkHelper;
import com.example.test_retrofit.group.AdapterGroup;
import com.example.test_retrofit.user.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentLike extends Fragment {

    private View view;
    private final String TAG = this.getClass().getSimpleName();


    private AdapterDessertList adapterDessertList;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<DTOCafe.CafeData> cafeLikeList;


    private PreferenceHelper preferenceHelper;
    private String login_id;
    private Integer is_loved;

    private TextView tbCafe, tbGroup;
    private Integer cafe_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_like, container, false);
        preferenceHelper = new PreferenceHelper(getContext());
        login_id= preferenceHelper.getID();
        cafeLikeList = new ArrayList<>();


        recyclerView = view.findViewById(R.id.cafeLikeList);
        tbCafe = view.findViewById(R.id.cafe);
        tbGroup = view.findViewById(R.id.group);


        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterDessertList = new AdapterDessertList(cafeLikeList);
        recyclerView.setAdapter(adapterDessertList);


//      --------------------좋아요 초기 세팅 화면 세팅해주기-----------------------------
        tbGroup.setEnabled(true);
        tbGroup.setTextColor(Color.parseColor("#FF000000"));
        tbCafe.setTextColor(Color.parseColor("#F6875109"));
        tbCafe.setEnabled(false);
        getMyCafeLikeList();

//      --------------------토글 버튼 -------------------------------

        tbCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tbGroup.setEnabled(true);
                tbGroup.setTextColor(Color.parseColor("#FF000000"));
                tbCafe.setTextColor(Color.parseColor("#F6875109"));
                tbCafe.setEnabled(false);
                Log.e(TAG,"tbCafe 클릭");
                getMyCafeLikeList();

            }
        });

        tbGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tbCafe.setEnabled(true);
                tbCafe.setTextColor(Color.parseColor("#FF000000"));
                tbGroup.setTextColor(Color.parseColor("#F6875109"));
                tbGroup.setEnabled(false);
                Log.e(TAG,"tbGRoup 클릭");

            }
        });


        adapterDessertList.setOnItemClickListener(new AdapterDessertList.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                cafe_id= cafeLikeList.get(position).getId_cafe();
                Intent intent = new Intent(getActivity(), ActivityCafeContent.class);
                intent.putExtra("cafe_id",cafe_id);
                startActivity(intent);

            }

            @Override
            public void toggleLoveButton(View v, int position) {
                String p = String.valueOf(position);
                Toast.makeText(getContext(), p, Toast.LENGTH_LONG).show();
//               서버에 보낼 게시글의 고유 아이디, 좋아요 여부 상태 리스트로부터 값 받아 초기화 하기
                Integer cafe_id = cafeLikeList.get(position).getId_cafe();
                is_loved = cafeLikeList.get(position).getIs_loved();
                Log.e(TAG, is_loved.toString());
                changeLoveCafe(login_id, cafe_id, is_loved, position);

            }
        });
        return view;
    }


    public void getMyCafeLikeList(){
        Log.e(TAG, "getMyCafeLikeList"+"유저가 좋아요한 카페 리스트 가져오기");
        Call<DTOCafe> call = NetWorkHelper.getInstance().getApiService().getMyCafeLoveList(login_id);
        call.enqueue(new Callback<DTOCafe>() {
            @Override
            public void onResponse(Call<DTOCafe> call, Response<DTOCafe> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "서버에서 받아온 값 " + response + response.body());
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
                        Log.e(TAG, cafeLikeList.get(position).getIs_loved().toString());
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
            cafeLikeList.get(position).setIs_loved(1);
            Log.e(TAG + "cangeIs_loved 0 ->1 :", cafeLikeList.get(position).getIs_loved().toString());
        } else {
            cafeLikeList.get(position).setIs_loved(0);
            Log.e(TAG + "cangeIs_loved 1 ->0 :", cafeLikeList.get(position).getIs_loved().toString());
        }
    }


    //    리스테에서 값이 변경되었을 때, 액티비티 리스트 값 변경 후 어댑터에게 알리는 역할.
    private void generateDataList(List<DTOCafe.CafeData> items) {
        Log.e(TAG, "items" + items);
        cafeLikeList.clear();
        for (int i = 0; i < items.size(); i++) {
            cafeLikeList.add(items.get(i));
        }
        adapterDessertList.notifyDataSetChanged();
    }

    public void EmptyDialog(String a) {//아이디 비번 입력 알람창
        Log.e("다이얼로그", "1");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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