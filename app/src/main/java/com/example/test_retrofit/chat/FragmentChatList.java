package com.example.test_retrofit.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.test_retrofit.MyDialog;
import com.example.test_retrofit.R;
import com.example.test_retrofit.cafe.DTOCafe;
import com.example.test_retrofit.Retrofit.NetWorkHelper;
import com.example.test_retrofit.group.DTO.DTOGroupResponse;
import com.example.test_retrofit.user.PreferenceHelper;
import com.naver.maps.geometry.Tm128;
import com.naver.maps.map.MapFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentChatList extends Fragment {
    private View view;
    private final String TAG = this.getClass().getSimpleName();

    private PreferenceHelper preferenceHelper;
    private String login_id;
    private MyDialog myDialog;

    //리사이클러뷰 관련
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdapterChatList adapterChatList;
    private List<DTOChat.ChatData> chatDataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        preferenceHelper = new PreferenceHelper(getContext());
        login_id = preferenceHelper.getID();
        chatDataList = new ArrayList<>();
        myDialog = new MyDialog(getContext());

        recyclerView = view.findViewById(R.id.chat_list);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterChatList = new AdapterChatList(chatDataList);
        recyclerView.setAdapter(adapterChatList);

        getChatList();

        adapterChatList.setOnItemClickListener(new AdapterChatList.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(),ActivityChatRoom.class);
                intent.putExtra("isMember","yes");
                intent.putExtra("id_meeting",chatDataList.get(position).getId_meeting());
                startActivity(intent);
            }
        });

        return view;
    }

    void getChatList() {
        Call<DTOChat> call = NetWorkHelper.getInstance().getApiService().getChatList(login_id);
        call.enqueue(new Callback<DTOChat>() {
            @Override
            public void onResponse(Call<DTOChat> call, Response<DTOChat> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "서버에서 받아온 값 형태 response + r.body : \n" + response + "/"+ response.body());
                    DTOChat dtoChat = response.body();
                    Log.e(TAG, "응답 성공 후 DTOChat 파싱해준 값1" +dtoChat);

                    if (dtoChat.getStatus().equals("true")) {
                        List<DTOChat.ChatData> items = dtoChat.getData();
                        Log.e(TAG, "응답 성공 후 ChatData 파싱해준 값2" + items);
                        generateDataList(items);
                    } else {
                        Log.e(TAG,"서버에서 오류");
                    }
                } else {
                    Log.e(TAG, "실패" + response.body()+ response);
                }
            }
            @Override
            public void onFailure(Call<DTOChat> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void generateDataList(List<DTOChat.ChatData> items) {
        Log.e(TAG, "generateDataList" );
        chatDataList.addAll(items);
        adapterChatList.notifyDataSetChanged();
    }


}