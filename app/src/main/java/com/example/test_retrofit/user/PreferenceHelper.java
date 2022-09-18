package com.example.test_retrofit.user;

import android.content.Context;
import android.content.SharedPreferences;

//내가 저장한 sp에서 값 저장 또는 불러오는 함수
public class PreferenceHelper {

    //어디다 쓰이는거지? -> 키 값으로
    private final String INTRO = "intro";
    private final String NICKNAME = "nickname";
    private final String ID = "id";
    private final String CAFE_ID = "cafe_id";
    private final String PROFILE = "profile";
    private SharedPreferences app_prefs;
    private Context context;

    // 이 함수는 어떤 용도로 쓰이는 거지?
    // context는 현재 파일에 대한 정보? 같은 느낌
    public PreferenceHelper(Context context){
        //생성자임 sp 객체 생성할 때 마다 꼭 인자값 전달해줘야 함
        //context 안 써주면 오류 난다 왜 써줘야 하는거지?
//        이 클래스안에 있는 함수를 사용하기 위해서 액티비티
        app_prefs = context.getSharedPreferences("shared",0);
        // 파일이름 shared 모드 0-> 읽기쓰기 가능
        //MODE_PRIVSTE -> 이 앱에서만 사용가능
        this.context =context;

    }

    public void putIsLogin (boolean loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        // Editor을  app_prefs 에 쓰기 위해서 연결해준다.
        // shared 라는 파일엘 edit 해주겠다.
        edit.putBoolean(INTRO, loginOrOut);
        //키 값이 INTRO VALUE는 boolean 형태인 loginOrOut
        edit.apply();
    }

    //왜 변수를 loginorout 으로 해줬지 귀찮아서? 아니면 이유가 있나
    public void putNickname(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(NICKNAME, loginOrOut);
        edit.apply();
    }

    public String getNickname()
    {
        return app_prefs.getString(NICKNAME, "");
        //sp 에서 데이터를 가져올 때는 get자료형() 형태로 함수 호출
        //첫번째 인자는 가져올 데이터의 쌍의 key에 해당하며 두번쨰 인자는
        //key에 해당하는 데이터가 없을 때 가져올 데이터터
        }

    public void putID(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(ID, loginOrOut);
        edit.apply();
    }

    public String getID()
    {
        return app_prefs.getString(ID, "");
    }

    public void deleteSP(){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.clear();
        edit.apply();
    }

    public void putCafeId(Integer cafe_id){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(CAFE_ID, String.valueOf(cafe_id));
        edit.apply();
    }

    public String getCafeId ()
    {
        return  app_prefs.getString(CAFE_ID,"");
    }


    public void putProfile(String profile_name){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(PROFILE, String.valueOf(profile_name));
        edit.apply();
    }

    public String getProfile ()
    {
        return  app_prefs.getString(PROFILE,"");
    }

}
