package com.example.test_retrofit.cafe;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.test_retrofit.R;
import com.example.test_retrofit.Retrofit.NetWorkHelper;
import com.example.test_retrofit.user.PreferenceHelper;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.MarkerIcons;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentCafeDetailData extends Fragment implements OnMapReadyCallback {

    private View view;
    private final String TAG = this.getClass().getSimpleName();

    private PreferenceHelper preferenceHelper;
    private String login_id;
    public String cafe_id;

    private TextView title, location, number;
    private ArrayList<DTOCafe.CafeData> cafeData;

    ActivityCafeContent activityCafeContent;

    private FragmentManager fm;
    private MapFragment mapFragment;
    private NaverMap naverMap;

    private Tm128 tm128;
    private LatLng latLng;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cafe_detail_data, container, false);

        preferenceHelper = new PreferenceHelper(getContext());
        activityCafeContent = new ActivityCafeContent();

        login_id = preferenceHelper.getID();
        cafe_id = preferenceHelper.getCafeId();

        Log.e(TAG,cafe_id);

        title = view.findViewById(R.id.title);
        location = view.findViewById(R.id.location);
        number = view.findViewById(R.id.number);

        // 프레그먼트 위에 프레그먼트를 세팅하기 위해 필요한 함수
        fm =getChildFragmentManager();
        mapFragment = (MapFragment)fm.findFragmentById(R.id.map);


        getDessertList();
        return view;
    }


//    NaverMap 객체가 준비된 후 onMapReady 콜백 메서드가 호출됨
    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

       naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING,true);
//       지도옵션 선택
       CameraPosition cameraPosition = new CameraPosition(
               latLng,17
       );
//       지도에 보여줄 위도경도와, 줌 확대 정도
        naverMap.setCameraPosition(cameraPosition);
//        세팅해줌


        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(latLng)
                .animate(CameraAnimation.Easing);
//        지도를 움직이기 위한 애니메이션 선택 후 등록
       naverMap.moveCamera(cameraUpdate);
//       세팅해줌

       Marker marker = new Marker();
       marker.setPosition(latLng);
       marker.setIcon(MarkerIcons.BLACK);
        marker.setIconTintColor(Color.parseColor("#FF9800"));
//        마커 위치와 색깔 지정해줌
       marker.setMap(naverMap);
//       마커 세팅
    }

    void getDessertList() {

        Call<DTOCafe> call = NetWorkHelper.getInstance().getApiService().getCafeFragmentContent(cafe_id, login_id);
        call.enqueue(new Callback<DTOCafe>() {
            @Override
            public void onResponse(Call<DTOCafe> call, Response<DTOCafe> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "서버에서 받아온 값 형태 " + response + response.body());
                    DTOCafe DTOGroupResponse = response.body();
                    Log.e(TAG, "응답 성공 후 dto 파싱해준 값1" + DTOGroupResponse);

                    if (DTOGroupResponse.getStatus().equals("true")) {
                        List<DTOCafe.CafeData> items = DTOGroupResponse.getData();
                        Log.e(TAG, "응답 성공 후 dto 파싱해준 값2" + items);

                        generateDataList(items);
                    } else {
                        EmptyDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }
                } else {
                    Log.e(TAG, "실패" + response.body());
                }

            }

            @Override
            public void onFailure(Call<DTOCafe> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });

    }

    private void generateDataList(List<DTOCafe.CafeData> items) {

        title.setText(items.get(0).getTitle());
        location.setText(items.get(0).getRoad_address());
        number.setText(items.get(0).getPhone_number());


//        네이버 지역 검색 api로 받아온 mapx,mapy를 위도 경도로 변환하는 과정
        tm128 = new Tm128(items.get(0).getMapx(),items.get(0).getMapy());
        latLng = tm128.toLatLng();


//        지도를 프래그먼트 내에 배치하는 코드
        if(mapFragment == null)
        {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

//        네이버 api 호출하는 과정
        mapFragment.getMapAsync(this::onMapReady);
//        getMapAsync 메서드로 onMapReadyCallback을 등록하고, 비동기로 NaverMap 객체를 얻는다
//        naverMap 객체가 준비되면 onMapReady() 콜백 메서드가 호출된다.
    }
    


    public void EmptyDialog(String a) {//아이디 비번 입력 알람창
        Log.e("다이얼로그", "1");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(a);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            //            확인 버튼 클릭시 실행하는 코드 onclick 안에 넣기
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }
}