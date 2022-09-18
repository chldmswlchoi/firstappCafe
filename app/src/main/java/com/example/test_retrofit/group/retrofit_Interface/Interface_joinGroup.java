package com.example.test_retrofit.group.retrofit_Interface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Interface_joinGroup {
    @Headers("Accept: application/json")
    //클라이언트가 헤더 부분의 어떤 형태로 값을 받고 싶은시 명시함
    @FormUrlEncoded
    // 인자를 키-값 과 같은 형태로 전달 할 때 사용 레트로 핏에서 @Field를 쓰기 전에 작성해줘야 한다.
    @POST("test/group/join_group.php")
        //post 방식으로 파일명.php에 접근한다.
    Call<String> joinGroup(

            @Field("login_id") String login_id,
            @Field("id_meeting") Integer id_meeting

            //@어노테이션("key값")  type 형식 value값 으로 작성
            //@Field는 POST 로 서버에 값을 보낼 때 붙어여 하는 어노테이션
            // @Field() 안에 PHP 파일에서 $_POST['username']; 의 [''] 안에 있는 것과 똑같은 이름을 넣어야 한다.
    );
}
