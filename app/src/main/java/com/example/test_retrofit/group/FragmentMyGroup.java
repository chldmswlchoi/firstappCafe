package com.example.test_retrofit.group;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.test_retrofit.R;
import com.example.test_retrofit.group.DTO.DTOGroupResponse;
import com.example.test_retrofit.group.DTO.DTOMessage;
import com.example.test_retrofit.group.retrofit_Interface.Interface_cancel_finishGroup;
import com.example.test_retrofit.group.retrofit_Interface.Interface_deleteGroup;
import com.example.test_retrofit.group.retrofit_Interface.Interface_finishMeeting;
import com.example.test_retrofit.group.retrofit_Interface.Interface_getGroupList;
import com.example.test_retrofit.user.ApiClient;
import com.example.test_retrofit.user.PreferenceHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentMyGroup extends Fragment {

    private View view;
    private final String TAG = this.getClass().getSimpleName();
    private FloatingActionButton made_group;


    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdapterMyGroup adapterMyGroup;
    private List<DTOGroupResponse.GroupDTO> myGroupList;


    private PreferenceHelper preferenceHelper;
    private String id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_my_group, container, false);


//        findView 캐싱
        made_group = view.findViewById(R.id.register);
        recyclerView = view.findViewById(R.id.my_group_list);


//        객체 생성
        preferenceHelper = new PreferenceHelper(getContext());
        myGroupList = new ArrayList<>();


//        리사이클러뷰와 어댑터를 바인딩 해주는 작업
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterMyGroup = new AdapterMyGroup(myGroupList,getContext());
        recyclerView.setAdapter(adapterMyGroup);

        Log.e("fragment","my_groupFragment 들어옴 ");
//        현재 로그인한 유저 id 불러오기
        id = preferenceHelper.getID();

//       내가 생성한 모임 데이터 불러오기
        getMyGroupData();



//       플로팅 버튼 모임 만들기 버튼 클릭 시
        made_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Activity_madeGroup.class);
                startActivity(intent);

            }
        });


//        리스트에 있는 아이템 클릭시
        adapterMyGroup.setOnItemClickListener(new AdapterMyGroup.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Integer id_meeting = myGroupList.get(position).getId_meeting();
                String road_address =myGroupList.get(position).getRoad_address();

                Intent intent = new Intent(getContext(), ActivityGroupContent.class);
                intent.putExtra("id_meeting",id_meeting);
                intent.putExtra("road_address",road_address);
                intent.putExtra("my_group","my_group");
                startActivity(intent);

            }

//            마감하기 버튼 클릭 시
            @Override
            public void onButtonClick(View v, int position) {
                Integer id_meeting = myGroupList.get(position).getId_meeting();

                if(myGroupList.get(position).getFinish()==0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("마감하기").setPositiveButton("마감하기",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    finishMeeting(id_meeting);
//                              레트로핏 통해 db 값 변경

                                }
                            })
                            .setNeutralButton("취소", null).show();

                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("마감취소하기").setPositiveButton("마감취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    cancelFinishMeeting(id_meeting);
//                              레트로핏 통해 db 값 삭제

                                }
                            })
                            .setNeutralButton("취소", null).show();

                }
            }

            @Override
            public void onDeleteClick(View v, int position) {


                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("삭제하기").setPositiveButton("삭제하기",
                        new DialogInterface.OnClickListener(){
                            public  void onClick(DialogInterface dialogInterface,int i){

                                deleteMyGroupData(position);
//                              레트로핏 통해 db 값 삭제

                            }
                        })
                        .setNeutralButton("취소",null).show();

            }
        });


        return  view;
    }


    //    레트로핏 통해 모임 게시글 삭제 하는 함수
    void deleteMyGroupData(int position){
//        인자 값으로 롱클릭한 아이템의 리스트 위치 받음

        Integer id_meeting = myGroupList.get(position).getId_meeting();
//        아이템 리스트 위치에 해당하는 id_meeting을 받아옴

        Interface_deleteGroup Interface_deleteGroup = ApiClient.getApiClient().create(Interface_deleteGroup.class);
        Call<DTOMessage> call = Interface_deleteGroup.deleteGroupListResult(id_meeting);
        call.enqueue(new Callback<DTOMessage>() {
            @Override
            public void onResponse(Call<DTOMessage> call, Response<DTOMessage> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    Boolean status = response.body().getStatus();
                    String message = response.body().getMessage();
                    Log.e ("onSuccess", String.valueOf(response.body()));

                    if (status)
                    {
                        deleteDataList(position,message);
                        //    어댑터에 세팅해준 리스트에 값 삭제하는 함수
                    }

                    else
                    {
                        EmptyDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }

                }

                else {
                    Log.e(TAG, "실패"+response.body());
                }
            }

            @Override
            public void onFailure(Call<DTOMessage> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
                EmptyDialog("실패 다시 시도해주세요");
            }

        });
    }


    //    레트로핏 통해 모임 마감하는 함수
    void finishMeeting(int id_meeting){

        Interface_finishMeeting interface_finishMeeting = ApiClient.getApiClient().create(Interface_finishMeeting.class);
        Call<DTOMessage> call = interface_finishMeeting.finishMeeting(id_meeting);
        call.enqueue(new Callback<DTOMessage>() {
            @Override
            public void onResponse(Call<DTOMessage> call, Response<DTOMessage> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    Boolean status = response.body().getStatus();
                    String message = response.body().getMessage();
                    Log.e ("onSuccess", String.valueOf(response.body()));

                    if (status)
                    {
                        Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        EmptyDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }

                }

                else {
                    Log.e(TAG, "실패"+response.body());
                }
            }

            @Override
            public void onFailure(Call<DTOMessage> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
                EmptyDialog("실패 다시 시도해주세요");
            }

        });
    }


    //    레트로핏 통해 모임 마감취소하는 함수
    void cancelFinishMeeting(int id_meeting){

        Interface_cancel_finishGroup interface_cancel_finishGroup = ApiClient.getApiClient().create(Interface_cancel_finishGroup.class);
        Call<DTOMessage> call = interface_cancel_finishGroup.cancelFinishGroup(id_meeting);
        call.enqueue(new Callback<DTOMessage>() {
            @Override
            public void onResponse(Call<DTOMessage> call, Response<DTOMessage> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    Boolean status = response.body().getStatus();
                    String message = response.body().getMessage();
                    Log.e ("onSuccess", String.valueOf(response.body()));

                    if (status)
                    {
                        Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        EmptyDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }

                }

                else {
                    Log.e(TAG, "실패"+response.body());
                }
            }

            @Override
            public void onFailure(Call<DTOMessage> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
                EmptyDialog("실패 다시 시도해주세요");
            }

        });
    }



    //    어댑터에 세팅해준 리스트에 값 삭제하는 과정
    private void deleteDataList( int position, String message)
    {
        Log.e(TAG, "리스트에 삭제 후 어댑터에게 알림"+ position);
        myGroupList.remove(position);
        adapterMyGroup.notifyItemRemoved(position);
        Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();

    }


    void getMyGroupData(){
        Log.e(TAG,"레트로핏 통신 함수 들어옴");

        Interface_getGroupList.get_MyGroupList_Interface getMyGroupListInterface
                = ApiClient.getApiClient().create(Interface_getGroupList.get_MyGroupList_Interface.class);

        Call<DTOGroupResponse> call = getMyGroupListInterface.getMyGroupListResult(id);

        call.enqueue(new Callback<DTOGroupResponse>() {
            @Override
            public void onResponse(Call<DTOGroupResponse> call, Response<DTOGroupResponse> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    Log.e(TAG,"서버에서 받아온 값 형태 "+response+response.body());
                    DTOGroupResponse DTOGroupResponse = response.body();
                    Log.e(TAG,"응답 성공 후 dto 파싱해준 값1"+ DTOGroupResponse);

                    if (DTOGroupResponse.getStatus().equals("true"))
                    {
                        List<DTOGroupResponse.GroupDTO> items = DTOGroupResponse.getData();
                        Log.e(TAG,"응답 성공 후 dto 파싱해준 값2"+items);
                        generateDataList(items);
                    }

                    else
                    {
                        EmptyDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }


                }
            }

            @Override
            public void onFailure(Call<DTOGroupResponse> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });


    }


//    @SuppressLint("NotifyDataSetChanged")
    private void generateDataList(List<DTOGroupResponse.GroupDTO> items)
    {

        Log.e(TAG, "items"+ items);
        myGroupList.clear();
        for (int i = 0; i < items.size(); i++) {
            Log.e(TAG, "리스트 값 추가 & 리스트 사이즈"+  items.size());

            myGroupList.add(items.get(i));
        }

        adapterMyGroup.notifyDataSetChanged();

    }


    public void EmptyDialog(String a)
    {
        Log.e("다이얼로그","1");
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