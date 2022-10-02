package com.example.test_retrofit.Retrofit;

import com.example.test_retrofit.cafe.DTOCafe;
import com.example.test_retrofit.cafe.DTOReview;
import com.example.test_retrofit.chat.DTOChatList;
import com.example.test_retrofit.chat.DTOChat;
import com.example.test_retrofit.group.DTO.DTOMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface ApiService {


    @Headers("Accept: application/json")
    @GET("test/chat/read_user_list.php")
//    () 안에 디테일한 url 주소 설정
    Call<List<DTOChat>> getUserList(
            @Query("id_meeting") int id_meeting
    );

    @Headers("Accept: application/json")
    @GET("test/chat/read_chat_message.php")
//    () 안에 디테일한 url 주소 설정
    Call<List<DTOChat>> getChatMessages(
            //페이징 하기 위해서 필요한 page,limit
//            @Query("page") Integer page,
//            @Query("limit") Integer limit,
            @Query("id") String id, // 로그인 한 유저 -> 유저가 참여한 채팅방 불러오기 위해서
            @Query("id_meeting") int id_meeting
    );



    @Headers("Accept: application/json")
    @GET("test/chat/read_chatList.php")
//    () 안에 디테일한 url 주소 설정
    Call<DTOChatList> getChatList(
            //페이징 하기 위해서 필요한 page,limit
//            @Query("page") Integer page,
//            @Query("limit") Integer limit,
            @Query("id") String id // 로그인 한 유저 -> 유저가 참여한 채팅방 불러오기 위해서
    );

    @Headers("Accept: application/json")
    @Multipart
//    서버에 이미지를 전송하기 위해 사용되는 어노테이션
//    파일을 서버로 전송하기 위해 사용되는 content-type 이다.
//    content-type 은 body에 들어가는 데이터 타입을 명시해 준다.
//    multipart 이기 때문에 @Field 사용할수 없다 -> Map 을 사용해서 mapping 해서 넣어줘야함
//    앞쪽에 @Part를 지정해하고 데이터 타입은 MultipartBody.Part로 지정하여야 한다.
    @POST("test/cafe/create_cafe.php")
    Call<String> postCafe(@Part ArrayList<MultipartBody.Part> images,
                          @PartMap HashMap<String, RequestBody> data);


    @Headers("Accept: application/json")
    @Multipart
    @POST("test/cafe/update_cafe_content.php")
    Call<String> modifyCafe(@Part ArrayList<MultipartBody.Part> images,
                            @PartMap HashMap<String, RequestBody> data);

    @Headers("Accept: application/json")
    @Multipart
    @POST("test/cafe/create_review.php")
//    () 안에 디테일한 url 주소 설정
    Call<DTOReview> sendReview(
            @Part ArrayList<MultipartBody.Part> images,
            @PartMap HashMap<String, RequestBody> data
    );

    @Headers("Accept: application/json")
    @GET("test/cafe/read_review.php")
//    () 안에 디테일한 url 주소 설정
    Call<DTOReview> setReview(
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("id_cafe") Integer id_cafe
    );

    @Headers("Accept: application/json")
    @GET("test/cafe/delete_review.php")
//    () 안에 디테일한 url 주소 설정
    Call<DTOReview> deleteReview(
            @Query("r_id") Integer review_id
    );

    @Headers("Accept: application/json")
    @GET("test/cafe/update_cafe_review.php")
//    () 안에 디테일한 url 주소 설정
    Call<DTOReview> modifyReview(
            @Query("r_id") Integer review_id,
            @Query("review") String review,
            @Query("star") Float star
    );

    @Headers("Accept: application/json")
    @GET("test/cafe/read_dessertList.php")
//    () 안에 디테일한 url 주소 설정
    Call<DTOCafe> getDessertList(
            @Query("f_id") Integer f_id,
            @Query("id") String login_id
    );


    @Headers("Accept: application/json")
    @GET("test/cafe/read_dessertList_filter.php")
//    () 안에 디테일한 url 주소 설정
    Call<DTOCafe> getDessertList_filter(
            @Query("d_id") String d_id,
            @Query("t_id") String t_id,
            @Query("f_id") Integer f_id,
            @Query("id") String login_id
    );


    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("test/cafe/change_cafeLove.php")
//    () 안에 디테일한 url 주소 설정
    Call<DTOCafe> postLoveCafe(
            @Field("id") String login_id,
            @Field("id_cafe") Integer id_cafe,
            @Field("is_loved") Integer is_loved

    );


    @Headers("Accept: application/json")
    @GET("test/cafe/read_cafeLikeList.php")
//    () 안에 디테일한 url 주소 설정
    Call<DTOCafe> getMyCafeLoveList(
            @Query("id") String login_id
    );


    @Headers("Accept: application/json")
    @GET("test/cafe/read_cafe_content.php")
        //    () 안에 디테일한 url 주소 설정
    Call<DTOCafe> getCafeContent(
            @Query("id_cafe") Integer cafe_id,
            @Query("id") String login_idg
    );


    @Headers("Accept: application/json")
    @GET("test/cafe/read_cafe_content.php")
        //    () 안에 디테일한 url 주소 설정
    Call<DTOCafe> getCafeFragmentContent(
            @Query("id_cafe") String cafe_id,
            @Query("id") String login_idg
    );

    @Headers("Accept: application/json")
    @GET("test/cafe/delete_cafe.php")
        //    () 안에 디테일한 url 주소 설정
    Call<DTOMessage> deleteCafe(
            @Query("id_cafe") Integer cafe_id
    );
}
