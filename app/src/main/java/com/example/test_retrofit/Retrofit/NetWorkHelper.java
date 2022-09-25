package com.example.test_retrofit.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetWorkHelper {

    private Retrofit retrofit;
    private ApiService apiService;
    public static NetWorkHelper netWorkHelper = new NetWorkHelper();

    private static final String BASE_URL ="http://3.39.153.170/";

    private NetWorkHelper(){

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                // client 를 등록해준다.
                .client(clientBuilder.build())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static NetWorkHelper getInstance(){
        return netWorkHelper;
    }

    public ApiService getApiService(){
        return apiService;
    }
}
