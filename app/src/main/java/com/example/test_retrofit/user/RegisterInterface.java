package com.example.test_retrofit.user;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

//회원가입 인터페이스 (가이드라인, 규격, 약속)
public interface RegisterInterface {


    String REGIST_URL = "http://43.200.106.233/";

    //요청할 서버의 URL
    //변수 혹은 상수로 할당할 때 http://가 들어가야 하며, 주소 끝에 /를 붙여줘야 한다.
    //공식문서를 읽어보면 baseURL()에 설정된 서버 URL에 /가 없다.


    @FormUrlEncoded
    // 인자를 키-값 과 같은 형태로 전달 할 때 사용 레트로 핏에서 @Field를 쓰기 전에 작성해줘야 한다.
    @POST("test/retrofit_test copy.php")
    //post 방식으로 파일명.php에 접근한다.
    Call<String> getUserRegist(
            // Call 이라는 인터페이스(규칙 있는 클래스?) 의 객체는 getUserRegist 로 설정해줌
            //웹 서버에 요청을 보내고 응답을 반환하는 Retrofit 메서드
    //요청을 보내고 응답을 받는다
    //결과 타입이 String .
            @Field("nickname") String nickname,
            @Field("id") String id,
            @Field("password") String password

            //@어노테이션("key값")  type 형식 value값 으로 작성

            //@Field는 POST 로 서버에 값을 보낼 때 붙어여 하는 어노테이션
            // @Field() 안에 PHP 파일에서 $_POST['username']; 의 [''] 안에 있는 것과 똑같은 이름을 넣어야 한다.
    );


}

