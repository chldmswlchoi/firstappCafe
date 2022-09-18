package com.example.test_retrofit;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface InterfaceProfile {
    @Headers("Accept: application/json")
    @Multipart
//    서버에 이미지를 전송하기 위해 사용되는 어노테이션
//    파일을 서버로 전송하기 위해 사용되는 content-type 이다.
//    content-type 은 body에 들어가는 데이터 타입을 명시해 준다.
//    multipart 이기 때문에 @Field 사용할수 없다 -> Map 을 사용해서 mapping 해서 넣어줘야함
    @POST("test/profile_upload.php")
    Call<String> photoUpload(@Part MultipartBody.Part File,
                             @PartMap HashMap<String, RequestBody> data);
}
