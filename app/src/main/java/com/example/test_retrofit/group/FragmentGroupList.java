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
import android.widget.Button;
import android.widget.Toast;

import com.example.test_retrofit.R;
import com.example.test_retrofit.chat.ActivityChatRoom;
import com.example.test_retrofit.group.DTO.DTOGroupResponse;
import com.example.test_retrofit.group.DTO.DTOMessage;
import com.example.test_retrofit.group.retrofit_Interface.Interface_deleteGroup;
import com.example.test_retrofit.group.retrofit_Interface.Interface_getGroupList;
import com.example.test_retrofit.user.ApiClient;
import com.example.test_retrofit.user.PreferenceHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentGroupList extends Fragment {

    private View view;
    private final String TAG = this.getClass().getSimpleName();

    private FloatingActionButton made_group;
    private AdapterGroup adapterGroup;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<DTOGroupResponse.GroupDTO> groupList;

    private PreferenceHelper preferenceHelper;
    private String id;

    private Button button;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group_list, container, false);
        // Inflate the layout for this fragment
        preferenceHelper = new PreferenceHelper(getContext());
        groupList = new ArrayList<>();
        // 꼭 new 해야지 생성된다고!!!!!
        made_group = view.findViewById(R.id.register);
        recyclerView = view.findViewById(R.id.group_list);
        button = view.findViewById(R.id.button);
        // 프래그먼트는 inflate 해준 view.findView 해야함
        linearLayoutManager = new LinearLayoutManager(getActivity());
        //프래그먼트는 getActivity() 로 해줘야함
        //프래그먼트에서는 액티비티를 호출하지 못하므로 부모 액티비티를 이용하여 호출
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterGroup = new AdapterGroup(groupList, getContext());
        recyclerView.setAdapter(adapterGroup);
        // 바인딩 해주는 작업

        Log.e(TAG, "그룹 리스트 프레그먼트 ONCREATEVIEW");

//        getGroupData();
        //레트로핏 통신 서버에서 데이터 가져옴

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityChatRoom.class);
                startActivity(intent);
            }
        });

        //플로팅 버튼 모임 만들기 버튼 클릭 시
        made_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Activity_madeGroup.class);
                //프래그먼트에서는 액티비티를 호출하지 못하므로 부모 액티비티를 이용하여 호출
                startActivity(intent);

            }
        });

        //리스트에 있는 아이템 클릭시
        adapterGroup.setOnItemClickListener(new AdapterGroup.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Integer id_meeting = groupList.get(position).getId_meeting();
                String road_address = groupList.get(position).getRoad_address();
                id = groupList.get(position).getId();

                Intent intent = new Intent(getContext(), ActivityGroupContent.class);
                intent.putExtra("id_meeting", id_meeting);
                intent.putExtra("road_address", road_address);

                // Log.e(TAG,"인텐트에서 보내주는 값/"+id_meeting+road_address);
                startActivity(intent);


            }

            @Override
            public void onDeleteClick(View v, int position) {
                Log.e(TAG, "삭제 클릭 1" + preferenceHelper.getID());

                id = groupList.get(position).getId();
                Log.e(TAG, "삭제 클릭 2" + id);
                if (id.equals(preferenceHelper.getID())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("삭제하기").setPositiveButton("삭제하기",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    deleteGroupData(position);

                                }
                            })
                            .setNeutralButton("취소", null).show();
                } else {
                    EmptyDialog("모임 생성자가 아닙니다 .");
                }


            }
        });
        return view;
    }

    public void onResume(){
        Log.e(TAG, "onResume");
        super.onResume();
        getGroupData();
    }
    //레트로핏 통해 모임 게시글 삭제 하는 함수

    void deleteGroupData(int position) {
        // 인자 값으로 롱클릭한 아이템의 리스트 위치 받음

        Integer id_meeting = groupList.get(position).getId_meeting();
            //아이템 리스트 위치에 해당하는 id_meeting을 받아옴

        Interface_deleteGroup Interface_deleteGroup = ApiClient.getApiClient().create(Interface_deleteGroup.class);
        Call<DTOMessage> call = Interface_deleteGroup.deleteGroupListResult(id_meeting);
        call.enqueue(new Callback<DTOMessage>() {
            @Override
            public void onResponse(Call<DTOMessage> call, Response<DTOMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean status = response.body().getStatus();
                    String message = response.body().getMessage();
                    Log.e("onSuccess", String.valueOf(response.body()));

                    if (status) {
                        deleteDataList(position, message);
                        //    어댑터에 세팅해준 리스트에 값 삭제하는 함수
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


    //    어댑터에 세팅해준 리스트에 값 삭제하는 과정
    private void deleteDataList(int position, String message) {
        Log.e(TAG, "deleteDataList" + position);
        groupList.remove(position);
        adapterGroup.notifyItemRemoved(position);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    void getGroupData() {
        Interface_getGroupList grouplist_interface = ApiClient.getApiClient().create(Interface_getGroupList.class);
        Call<DTOGroupResponse> call = grouplist_interface.getGroupListResult();
        call.enqueue(new Callback<DTOGroupResponse>() {
            @Override
            public void onResponse(Call<DTOGroupResponse> call, Response<DTOGroupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "서버에서 받아온 값 형태 " + response + response.body());
                    DTOGroupResponse DTOGroupResponse = response.body();
                    Log.e(TAG, "응답 성공 후 dto 파싱해준 값1" + DTOGroupResponse);

                    if (DTOGroupResponse.getStatus().equals("true")) {
                        List<DTOGroupResponse.GroupDTO> items = DTOGroupResponse.getData();
                        Log.e(TAG, "응답 성공 후 dto 파싱해준 값2" + items);
                        generateDataList(items);
                    } else {
//                        EmptyDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }
                } else {
                    Log.e(TAG, "실패" + response.body());
                }
            }

            @Override
            public void onFailure(Call<DTOGroupResponse> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });
    }


    private void generateDataList(List<DTOGroupResponse.GroupDTO> items) {
        Log.e(TAG, "items" + items);
        groupList.clear();
        for (int i = 0; i < items.size(); i++) {
            groupList.add(items.get(i));
        }
        adapterGroup.notifyDataSetChanged();
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

