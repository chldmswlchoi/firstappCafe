package com.example.test_retrofit.group.retrofit_Interface;

import com.example.test_retrofit.group.DTO.DTOMessage;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Interface_postGroup {
    @Headers("Accept: application/json")
    //클라이언트가 헤더 부분의 어떤 형태로 값을 받고 싶은시 명시함
    @FormUrlEncoded
    // 인자를 키-값 과 같은 형태로 전달 할 때 사용 레트로 핏에서 @Field를 쓰기 전에 작성해줘야 한다.
    @POST("test/group/create_group.php")
        //post 방식으로 파일명.php에 접근한다.
    Call <DTOMessage> post_MakeGroup(
            // Call 이라는 인터페이스(규칙 있는 클래스?) 의 객체는 getUserRegist 로 설정해줌
            //웹 서버에 요청을 보내고 응답을 반환하는 Retrofit 메서드
            //요청을 보내고 응답을 받는다
            //결과 타입이 MessageDTO.
            @Field("id") String host,
            @Field("cafe") String cafe,
            @Field("title") String title,
            @Field("gdate") String gdate,
            @Field("gtime") String gtime,
            @Field("people") String people,
            @Field("road_address") String road_address,
            @Field("mapx") Integer mapx,
            @Field("mapy") Integer mapy

            //@어노테이션("key값")  type 형식 value값 으로 작성
            //@Field는 POST 로 서버에 값을 보낼 때 붙어여 하는 어노테이션
            // @Field() 안에 PHP 파일에서 $_POST['username']; 의 [''] 안에 있는 것과 똑같은 이름을 넣어야 한다.
    );
}
