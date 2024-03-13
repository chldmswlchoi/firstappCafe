package com.example.test_retrofit.user.email_pw;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface InterfaceEmail {
    String LOGIN_URL ="http://43.200.106.233/";

    @FormUrlEncoded
    @POST("test/email_check.php")
    Call<String> getUserEmail(
            @Field("id") String email

    );
}
