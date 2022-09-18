package com.example.test_retrofit.group.retrofit_Interface;

import com.example.test_retrofit.group.DTO.DTOMessage;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface Interface_getProfile {
//    public 이 없으면 같은 패키지 안에서는 자유롭게 접근 가능

    @Headers("Accept: application/json")
    //클라이언트가 헤더 부분의 어떤 형태로 값을 받고 싶은시 명시함
    @GET("test/read_profile.php")

//    () 안에 디테일한 url 주소 설정
    Call<DTOMessage> getProfile(
            @Query("id") String id
    );
}
