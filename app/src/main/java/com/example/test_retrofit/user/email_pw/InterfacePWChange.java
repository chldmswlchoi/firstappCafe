package com.example.test_retrofit.user.email_pw;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface InterfacePWChange {

    String LOGIN_URL ="http://3.37.18.125/";

    @FormUrlEncoded
    @POST("test/change.php")
    Call<String> getUserPW(
            @Field("id") String email,
            @Field("password") String password

    );
}
