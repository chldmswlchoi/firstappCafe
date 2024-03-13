package com.example.test_retrofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.test_retrofit.group.DTO.DTOMessage;
import com.example.test_retrofit.group.retrofit_Interface.Interface_getProfile;
import com.example.test_retrofit.user.ApiClient;
import com.example.test_retrofit.user.ActivityLogin;
import com.example.test_retrofit.user.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentMypage extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    private Button log_out;
    private View view;
    private TextView nick;
    private PreferenceHelper preferenceHelper;
    private ImageView profile;
    File destFile;
    private Uri uri;
    private String s_id;
    private HashMap<String, RequestBody> map;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mypage, container, false);

        log_out = view.findViewById(R.id.bt_logout);
        nick = view.findViewById(R.id.nickname);
        profile = view.findViewById(R.id.profile);
        preferenceHelper = new PreferenceHelper(getContext());

        nick.setText(preferenceHelper.getNickname());
//       sp 에서 저장된 닉네임 가져오기'

        Log.e(TAG, "마이페이지 클릭 프레그먼트 ONCREATEVIEW");
        GetImage();


//        로그아웃
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceHelper.deleteSP();
//                sp에 저장된 회원 정보 삭제
                Intent intent = new Intent(getContext(), ActivityLogin.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
//                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                content://media/external/images/media 와 같은 uri 경로 아래서만 파일을 찾는다.
//                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*");
//                setType은 데이터가 아닌 특정한 유형인 인텐트를 생성하기 위해 사용된다.
//                예를 들어 반환할 데이터 유형을 표시하기 위해,
//                또한 이 메서드는 이전에 설정된 모든 데이터를 자동으로 지운다.
                intent.setAction(Intent.ACTION_PICK);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                setAction은 수행할 일반 작업을 설정한다 매개변수는 Action(수행할 작업이름)
//                여러 호출을 단일 상태로 연결하기 위해 intent 개체를 반환한다
//                ACTION_PICK는 데이터로부터 항목을 선택하고 선택한 항목을 반환한다.
                startActivityForResult(intent, 0);

            }
        });


        return view;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    uri = data.getData();
                    Log.e(TAG, String.valueOf(data.getData()));
//                        uri 객체를 통해 이미지 데이터를 전달받고
//                        이미지의 상대경로를 가져온다.
//                        uri 는 리소스를 구별할 수 있는 식별자 uri 통해 우리가 접근해야 하는
//                        리소스가 어디 있는지 알 수 있다. 인테넷만 uri 사용하는 것이 아니라 다양한 모바일 기기에서도 사용
//                        여기서 uri 역할은 이미제에 접근할 수 있는 유일한 식별자이다.

                    //갤러리앱에서 관리하는 DB정보가 있는데, 그것이 나온다 [실제 파일 경로가 아님!!]
                    //얻어온 Uri는 Gallery앱의 DB번호임. (content://-----/2854)
                    //업로드를 하려면 이미지의 절대경로(실제 경로: file:// -------/aaa.png 이런식)가 필요함
                    //Uri -->절대경로(String)로 변환


                    String imagePath = getRealPathFromURl(uri);
//                        Log.e(TAG, imagePath);
//                        image의 절대경로를 가져온다


//                    destFile = new File(imagePath);
//                        file 변수에 File 을 집어 넣는다.

                    SendImage(imagePath);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
//                    취소시 호출할 행동 쓰기
                Toast.makeText(getActivity(), TAG + "사진 선택 취소", Toast.LENGTH_LONG).show();

            }
        }
    }


    //        image 의 절대경로를 가져오는 메소드 실제 sd카드의 경로를 리턴해주는 함수이다.
    private String getRealPathFromURl(Uri contentUri) {
        Log.e(TAG, "절대 경로1/받은 상대경로 인자값 확인" + String.valueOf(contentUri));
        Cursor cursor = null;
//        쿼리한 데이터를 순화하는 역할을 cursor가 한다.
//        cursor에는 쿼리할때 projection으로 요청한 컬럼들이 포함되어 있다. 즉 db의 칼럼들이 저장되어 있다.
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
//            MediaStore.Images는 타입이 image인 미디어의 모음집이다.
//            MediaStore.Images.Media.DATA 는 캐시된 열의 인덱스인듯
//            쿼리에서 받는 인자 값 형태가 string 배열 형태이기 떄문에
            Log.e(TAG, "타입이 image인 미디어의 모음집" + proj);
            cursor = requireActivity().getContentResolver().query(contentUri, proj, null, null, null);
//            쿼리의 인자값 1. uri 찾고자 하는 데이터의 uri /2. projection db의 칼럼과 같다. 결과로 받고 시픈 데이터의 종류를 알려준다.
//            3.Selection : DB 의 where 키워드와 같다. 어떤 조건으로 필터링된 결과를 받을 때 사용 /4.selection과 함께 사용 /5. 쿼리 결과 데이터를 분류 할 때 사용
            Log.e(TAG, "절대 경로3/" + cursor);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            컬럼의 인덱스를 얻는 코드이다 왜 컬럼의 인덱스를 얻지?
//            커서로 컬럼을 읽을 때는 getLOng 또는 getString 함수에 인자값으로 컬럼의 index를 넣어야 하기 때문
            Log.e(TAG, "절대 경로4/" + column_index);
            cursor.moveToFirst();
            Log.e(TAG, "절대 경로5/" + cursor.getString(column_index));
            return cursor.getString(column_index);
//                getString 은 욫청된 열의 값을 문자열로 반환
        } finally {
            if (cursor != null) {
                cursor.close();
            }
//            }


        }
    }


    private void SendImage(String filepath) {


//        filepath는 String 변수로 갤러리에서 이미지를 가져올 때 photoUri.getPath()를 통해 받아온다
        File file = new File(filepath);
        InputStream inputStream = null;
        try {
            inputStream = getContext().getContentResolver().openInputStream(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray());
//        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("postImg", file.getName() ,requestBody);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), byteArrayOutputStream.toByteArray());
//        http 요청 또는 응답 본문의 콘텐츠 유형을 설명한다.
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);

        map = new HashMap<>();
        s_id = preferenceHelper.getID();
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), s_id);
        map.put("id", id);

        InterfaceProfile InterfaceProfile = ApiClient.getApiClient().create(InterfaceProfile.class);

        Call<String> call = InterfaceProfile.photoUpload(body, map);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Log.e(TAG, "응답1 =" + response.body());
                Log.e(TAG, "응답2 =" + response);

                String jsonResponse = response.body();

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if ((jsonObject.optString("result").equals("success"))) {

                    Glide.with(requireActivity()).
                            load(uri).
                            diskCacheStrategy(DiskCacheStrategy.NONE).
                            skipMemoryCache(true).
                            into(profile);
                    //                            디스크 캐시 저장 off
                    //                            메모리 캐시 저장 off
                    Toast.makeText(getActivity(), TAG + "프로필 등록 완료.", Toast.LENGTH_LONG).show();
                    preferenceHelper.putProfile(file.getName());
                    //                        getApplicationContext는 액티비티 화면이 아닌 앱의 context 를 반환한다.
//                        load : 이미지 경로, override:이미지 가로, 세로 크기 조정, into 이미지를 출력할 IMageView 객체
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }


        });
    }


    private void GetImage() {
        s_id = preferenceHelper.getID();
        Log.e(TAG, "sp에 저장한 아이디 가져오기" + s_id);
        Interface_getProfile getProfileInterface = ApiClient.getApiClient().create(Interface_getProfile.class);
        Call<DTOMessage> call = getProfileInterface.getProfile(s_id);

        call.enqueue(new Callback<DTOMessage>() {

            @Override
            public void onResponse(Call<DTOMessage> call, Response<DTOMessage> response) {

                Log.e(TAG, "응답1 =" + response.body());
                Log.e(TAG, "응답2 =" + response);

                if (response.isSuccessful() && response.body() != null) {

                    Boolean status = response.body().getStatus();
//                    Log.e ("onSuccess", String.valueOf(status));
                    String message = response.body().getMessage();
//                    Log.e ("onSuccess", message);

                    if (status) {
                        String BASEURL = "http://43.200.106.233/test/upload/profile/";
                        String URL = BASEURL + message;
                        Log.e(TAG, "로드할 최종 URL" + URL);

                        Glide.with(getActivity().
                                getApplicationContext()).
                                load(URL).
                                diskCacheStrategy(DiskCacheStrategy.NONE).
//                            디스크 캐시 저장 off
        skipMemoryCache(true).
//                            메모리 캐시 저장 off
        into(profile);
                    } else {
//                        CheckDialog(message);
                    }

                }

            }

            @Override
            public void onFailure(Call<DTOMessage> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }


        });
    }


}