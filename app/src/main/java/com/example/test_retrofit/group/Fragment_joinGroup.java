package com.example.test_retrofit.group;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.test_retrofit.R;
import com.example.test_retrofit.group.DTO.DTOGroupResponse;
import com.example.test_retrofit.group.retrofit_Interface.Interface_getJoinGroup;
import com.example.test_retrofit.user.ApiClient;
import com.example.test_retrofit.user.PreferenceHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_joinGroup extends Fragment {

    private View view;
    private final String TAG = this.getClass().getSimpleName();
    private FloatingActionButton made_group;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdapterjoinGroup adapterjoinGroup;
    private List<DTOGroupResponse.GroupDTO> joinGroupList;

    private PreferenceHelper preferenceHelper;
    private String host;

    private String login_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_join_group,container,false);

        preferenceHelper = new PreferenceHelper(getContext());
        joinGroupList = new ArrayList<>();

        made_group = view.findViewById(R.id.register);
        recyclerView = view.findViewById(R.id.join_group_list);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterjoinGroup = new AdapterjoinGroup(joinGroupList,getContext());
        recyclerView.setAdapter(adapterjoinGroup);

        login_id = preferenceHelper.getID();

        Log.e("TAG","fragment_join_group");


        getJoinGroupData();

        //플로팅 버튼 모임 만들기 버튼 클릭 시
        made_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_madeGroup.class);
//                프래그먼트에서는 액티비티를 호출하지 못하므로 부모 액티비티를 이용하여 호출
                startActivity(intent);
            }
        });

        adapterjoinGroup.setOnItemClickListener(new AdapterjoinGroup.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Integer id_meeting = joinGroupList.get(position).getId_meeting();
                String road_address =joinGroupList.get(position).getRoad_address();
//                host = joinGroupList.get(position).getId();

                Intent intent = new Intent(getContext(), ActivityGroupContent.class);
                intent.putExtra("id_meeting",id_meeting);
                intent.putExtra("road_address",road_address);

//                Log.e(TAG,"인텐트에서 보내주는 값/"+id_meeting+road_address);
                startActivity(intent);

            }
        });

        return view;


    }


    void getJoinGroupData()
    {
        Interface_getJoinGroup interface_getJoinGroup = ApiClient.getApiClient().create(Interface_getJoinGroup.class);
        Call<DTOGroupResponse> call = interface_getJoinGroup.getJoinGroupResult(login_id);
        call.enqueue(new Callback<DTOGroupResponse>() {
            @Override
            public void onResponse(Call<DTOGroupResponse> call, Response<DTOGroupResponse> response) {

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

            @Override
            public void onFailure(Call<DTOGroupResponse> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });
    }

    private void generateDataList(List<DTOGroupResponse.GroupDTO> items)
    {
        Log.e(TAG, "items"+ items);
        joinGroupList.clear();
        for (int i = 0; i < items.size(); i++) {

            joinGroupList.add(items.get(i));
        }

        adapterjoinGroup.notifyDataSetChanged();

    }

    public void EmptyDialog(String a)
    {//아이디 비번 입력 알람창
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