package com.example.test_retrofit.group.retrofit_Interface;

import com.example.test_retrofit.group.DTO.DTOGroupResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface Interface_getGroupList {
    @Headers("Accept: application/json")
    @GET("test/group/read_grouplist.php")

//    () 안에 디테일한 url 주소 설정
    Call<DTOGroupResponse> getGroupListResult(


    );


    interface get_MyGroupList_Interface
    {
        @Headers("Accept: application/json")
        @GET("test/group/read_Mygroup_list.php")

        Call<DTOGroupResponse> getMyGroupListResult(
                @Query("id") String id
        );


    }
}
