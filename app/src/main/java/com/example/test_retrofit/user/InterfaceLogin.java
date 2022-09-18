package com.example.test_retrofit.user;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface InterfaceLogin {

//    String LOGIN_URL ="http://3.39.153.170/";

    @FormUrlEncoded
    @POST ("test/login.php")
    Call<String> getUserLogin(
            @Field("id") String email,
            @Field("password") String password
    );
}
