package com.example.test_retrofit.group.retrofit_Interface;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface Interface_cancelJoinGroup {

    @Headers("Accept: application/json")
    @GET("test/group/join_cancel_group.php")

//    () 안에 디테일한 url 주소 설정
    Call<String> cancelJoinGroup(

            @Query("id_meeting") Integer id_meeting,
            @Query("exit_id") String exit_id,
            @Query("exit") int exit // 서버에서

//            몇번째 게시글을 삭제할건지 지정하기 위해서 쿼리를 통해 url에 포함해서 날려줌
//            get 방식에서만 사용 가능 url 뒤에 붙는다.
//            query?= query 이러한 형식으로
    );
}
