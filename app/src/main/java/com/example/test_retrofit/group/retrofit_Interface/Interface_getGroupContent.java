package com.example.test_retrofit.group.retrofit_Interface;

import com.example.test_retrofit.group.DTO.DTOGroupResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface Interface_getGroupContent {

    @Headers("Accept: application/json")
    //클라이언트가 헤더 부분의 어떤 형태로 값을 받고 싶은시 명시함
    @GET("test/group/read_group_content.php")

//    () 안에 디테일한 url 주소 설정
    Call<DTOGroupResponse> getGroupContentResult(
            @Query("id_meeting") Integer id_meeting,
            @Query("login_id") String login_id
    );

}


