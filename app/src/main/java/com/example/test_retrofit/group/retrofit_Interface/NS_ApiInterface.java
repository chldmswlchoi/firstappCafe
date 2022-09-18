package com.example.test_retrofit.group.retrofit_Interface;

import com.example.test_retrofit.group.DTO.NSDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NS_ApiInterface {


    @GET("search/{type}")
//    () 안에 디테일한 url 주소 설정
    Call<NSDTO> getSearchResult(
            @Header("X-Naver-Client-Id") String id,
//            client id
            @Header("X-Naver-Client-Secret") String pw,
//            client secret
            @Path("type") String type,
//            STring type 값을 Get의 {type}에 넘겨줌
            @Query("query") String query,
            @Query("display") Integer display,
            @Query("sort")  String sort
//            get 방식에서만 사용 가능 url 뒤에 붙는다.
//            query?= query 이러한 형식으로

    );

    //함수 인자 값인데 여기서 String sort; 선언해주고 그냥 sort를 넣어주면 하면 당연히 오류나지
}
