package com.example.test_retrofit.cafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.test_retrofit.MyDialog;
import com.example.test_retrofit.R;
import com.example.test_retrofit.Retrofit.NetWorkHelper;
import com.example.test_retrofit.group.Activity_findCafe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityModifyCafe extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private EditText review, title, cafe, road_address, number, sns_url;
    private Spinner number_spinner, last_spinner, dessert_spinner, dessert_spinner2, dessert_tema;

    private Button modify;
    private View location;
    private ImageView image;

    RecyclerView recyclerView;
    AdapterMultiImage adapterMultiImage;
    private HashMap<String, RequestBody> map;
    private ArrayList<String> imagePath = new ArrayList<>();
//    갤러리의 사진 상대경로 담는 배열

    //    카페 글 수정하기 버튼 눌렀을 때 인텐트로 전달 받은 값
    private String i_fnumber, i_last_order, i_dessert, i_tema;
    private Integer cafe_id, mapx, mapy, i_food;

    //    스피너에서 가져온 값
    private String gnumber, glast_order, gdessert;
    private Integer gtema, gfood;

    // 디저트 카테고리 어뎁터
    private ArrayAdapter dadapter;
    private MyDialog myDialog;

    //    카페 검색 결과 선택 후 결과 구분 숫자, 결과값 저장할 변수
    private Integer CAFERESULT = 0;
    private String get_road_address, cafe_result, area;

    //    사진 선택 후 결과 구분 숫자
    private Integer IMAGERESULT = 1;
    private ArrayList<Uri> uriList = new ArrayList<>();
//    이미지의 uri를 담을 arraylist 객체

    private ArrayList<String> serverImage = new ArrayList<>();
    // 남아 있는 서버 이미지 명을 모아둔 리스트
    private ArrayList<String> images = new ArrayList<>();
    //    인텐트로 받은 이미지명 리스트
    private String BaseURL = "http://43.200.106.233/test/upload/cafe/";

    private String ereview, etitle, ecafe, eroad_address, enumber, esns_url, final_number;
    //    editText에 있는 값을 받을 변수


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.test_retrofit.R.layout.activity_modify_cafe);


        image = findViewById(R.id.image);
        review = findViewById(R.id.review);
        title = findViewById(R.id.title);
        cafe = findViewById(R.id.cafe);
        road_address = findViewById(R.id.road_address);
        number = findViewById(R.id.number);
        sns_url = findViewById(R.id.sns_url);
        last_spinner = findViewById(R.id.time_spinner);
        number_spinner = findViewById(R.id.number_spinner);
        dessert_spinner = findViewById(R.id.dessert_spinner);
        dessert_spinner2 = findViewById(R.id.dessert_spinner2);
        dessert_tema = findViewById(R.id.dessert_tema);

        recyclerView = findViewById(R.id.cafe_image);

        modify = findViewById(R.id.modify);
        location = findViewById(R.id.location);

        myDialog = new MyDialog(this);


//        -----------------> 인텐트에서 보낸 값 받고 세팅해주기
        cafe_id = getIntent().getIntExtra("cafe_id", -1);
        Log.e(TAG, "cae_id" + cafe_id);
        mapx = getIntent().getIntExtra("mapx", -1);
        mapy = getIntent().getIntExtra("mapy", -1);


        review.setText(getIntent().getStringExtra("review"));
        title.setText(getIntent().getStringExtra("title"));
        cafe.setText(getIntent().getStringExtra("cafe"));
        road_address.setText(getIntent().getStringExtra("road_address"));
        get_road_address = getIntent().getStringExtra("road_address");
        area = get_road_address.substring(0, get_road_address.indexOf(" "));
        number.setText("-" + getIntent().getStringExtra("number"));
        sns_url.setText(getIntent().getStringExtra("url"));

        i_food = getIntent().getIntExtra("food", -1);
        i_last_order = getIntent().getStringExtra("last_order");
        i_fnumber = getIntent().getStringExtra("fnumber");
        i_tema = getIntent().getStringExtra("tema");
        i_dessert = getIntent().getStringExtra("dessert");
        images = getIntent().getStringArrayListExtra("images");

//       이미지 uri 배열에 값 추가
        for (int i = 0; i < images.size(); i++) {
            uriList.add(Uri.parse(BaseURL + images.get(i)));
        }

//        이미지 어댑터 세팅
        adapterMultiImage = new AdapterMultiImage(uriList);
        recyclerView.setAdapter(adapterMultiImage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

//        -----------------> 인텐트에서 보낸 값 받기

//        -----------------> 스피너 아이템 항목 세팅 해주기
        String[] first_number = {"02", "031", "032", "033", "041", "042", "043", "044", "050", "051", "052", "053", "054", "055", "061", "062", "063", "064", "010", "011", "016", "017",
                "018", "019", "0303", "0502", "0503", "0504", "0505", "0506", "0507", "0508", "070", "080", "1433", "1522", "1533",
                "1544", "1566", "1577", "1588", "1599", "1600", "1644", "1660", "1661", "1666", "1668", "1670", "1688", "1800",
                "1811", "1833", "1855", "1877", "1899"};

        String[] last_order = {"5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60"};

        String[] tema = {"데이트하기 좋은", "조용한", "공원카페", "모임하기 좋은", "특별한 날",
                "야외카페", "감성카페", "인스타카페", "대형카페", "주택개조", "로스팅 직접 하는"};

        String[] food = {"디저트", "브런치", "과일/야채", "비건"};

        String[] dessert = {"일반", "베이커리", "전통 디저트", "도넛", "와플", "마카롱", "케이크"
                , "타르트", "초콜릿", "쿠키", "마들렌", "카눌레", "스콘", "머랭쿠키", "푸딩", "아이스크림", "빙수", "젤라또"};

        String[] brunch = {"일반", "샌드위치", "샐러드", "토스트", "요거트", "팬케이크", "베이글"};

        String[] fruit = {"과일라떼", "과일스무디", "과일에이드", "과일주스", "생과일", "야채주스"};

        String[] vegan = {"비건", "키토"};

//      ----------------->   스피너 아이템 항목 세팅 해주기


//      ----------------->  어뎁터에 스피터 아이템 항목, 스타일 세팅
        ArrayAdapter<String> tadapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, tema);
//        위 배열 값을 String으로 선언했기 때문에 <String>이라고 선언한다.
//        인자값 1: 앱의 정보를 담고 있는 객체 2: 스피너 디자인 3: 내가 작성한 배열

        ArrayAdapter<String> fadapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, food);

        ArrayAdapter<String> ladapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, last_order);

        ArrayAdapter<String> nadapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, first_number);

//      ----------------->  어뎁터에 스피터 아이템 항목, 스타일 세팅


//      ----------------->  스피너에 어뎁터 세팅
        dessert_spinner.setAdapter(fadapter);
        last_spinner.setAdapter(ladapter);
        number_spinner.setAdapter(nadapter);
        dessert_tema.setAdapter(tadapter);

        switch (i_food - 1) {
            case 0:
                setDessertSpinner_byIntent(dessert);
                break;

            case 1:
                setDessertSpinner_byIntent(brunch);
                break;

            case 2:
                setDessertSpinner_byIntent(fruit);
                break;

            case 3:
                setDessertSpinner_byIntent(vegan);
                break;

            default:
                break;
        }
//      ----------------->  스피너에 어뎁터 세팅


//      ----------------->  인텐트에서 받은 값으로 스피너 초기 세팅 해주기
        dessert_spinner.setSelection(i_food - 1, true);
        gfood = i_food;
        last_spinner.setSelection(Arrays.asList(last_order).indexOf(i_last_order), true);
        glast_order = i_last_order;
        number_spinner.setSelection(Arrays.asList(first_number).indexOf(i_fnumber), true);
        gnumber = i_fnumber;
        dessert_tema.setSelection(Arrays.asList(tema).indexOf(i_tema), true);
        gtema = Arrays.asList(tema).indexOf(i_tema);

//      ----------------->  인텐트에서 받은 값으로 스피너 초기 세팅 해주기


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
//                setType은 데이터가 아닌 특정한 유형인 인텐트를 생성하기 위해 사용된다.
//                예를 들어 반환할 데이터 유형을 표시하기 위해,
//                또한 이 메서드는 이전에 설정된 모든 데이터를 자동으로 지운다.
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                EXTRA_ALLOW_MULTIPLE 은 사용자가 여러 항목을 선택하고 반환하도록 허용한다.
//                getClipData() 부분으로 반환된다.
                intent.setAction(Intent.ACTION_PICK);

                startActivityForResult(intent, IMAGERESULT);
            }
        });

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ereview = review.getText().toString();
                etitle = title.getText().toString();
                ecafe = cafe.getText().toString();
                eroad_address = road_address.getText().toString();
                enumber = number.getText().toString();
                esns_url = sns_url.getText().toString();
                final_number = gnumber + enumber;


                Log.e(TAG, "register 눌렀을 때 받아온 값\n" + ereview + etitle + ecafe
                        + eroad_address + esns_url + final_number);

                if (etitle.isEmpty() || ecafe.isEmpty() || ereview.isEmpty()
                        || eroad_address.isEmpty() || gdessert.isEmpty()) {// 모임 작성 글 폼에서 sp에  빈 값이 있었을 때
                    myDialog.CheckDialog("작성폼을 모두 입력해주세요");
                } else {

                    for (int i = 0; i < uriList.size(); i++) {
                        if ("http".equals(uriList.get(i).getScheme())) {

                            for (int j = 0; j < images.size(); j++) {
                                if (uriList.get(i).equals(Uri.parse(BaseURL + images.get(j)))) {
                                    serverImage.add(images.get(j));
                                    Log.e(TAG, "남은 serverImage:" + serverImage);
                                    uriList.remove(i);
                                    Log.e(TAG, "urlist에서 서버 이미지는 제거 후 상태" + uriList);
                                }
                            }
                        }
                    }
                    updateCafe();
                    //서버 통신하는 함수
                }
            }
        });


        //        전화번호 스피너 선택
        number_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                gnumber = number_spinner.getSelectedItem().toString();
                Log.e(TAG, "번호 스피너 결과" + gnumber);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                gnumber = "02";
//                Log.e(TAG,"선택하지 않았을 때 번호 스피너 결과"+gnumber);
            }
        });

//        마지막 주문 n분 전 스피너 선택
        last_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                glast_order = last_spinner.getSelectedItem().toString();
                Log.e(TAG, "마지막 주문 n 분전 선택 결과" + glast_order);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        디저트 스피너 1 선택시
        dessert_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gfood = dessert_spinner.getSelectedItemPosition() + 1;

                Log.e(TAG, "디저트 카테고리1 gfood" + gfood);
                switch (position) {
                    case 0:
                        setDessertSpinner(dessert);
                        break;

                    case 1:
                        setDessertSpinner(brunch);
                        break;

                    case 2:
                        setDessertSpinner(fruit);
                        break;

                    case 3:
                        setDessertSpinner(vegan);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        dessert_tema.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
//            스피너에서 선택했을 때
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                매개변수1: 내가 만든 어댑터를 가리키게 된다. 2: 내가 선택한 뷰 객체 3: 내가 선택한 index 4: id 값

                gtema = dessert_tema.getSelectedItemPosition();
                Log.e(TAG, "디저트 카테고리1 스피너 선택 결과" + gtema);

            }

            @Override
//            스피너가 선택되지 않았을 때
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 카페 검색해서 클릭한 결과물 받아와서 et 에 세팅 해주는 과정

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityModifyCafe.this, Activity_findCafe.class);
                startActivityForResult(intent, CAFERESULT);
                // FindCafeActivity에서 값을 받아 온다.
                //두번째 인자 값은 여러 여러 액티비티들이 있을 경우 어떤 액티비티인지 식별하는 값이다.
            }
        });

    }

    private void updateCafe() {

        //        filepath는 String 변수로 갤러리에서 이미지를 가져올 때 photoUri.getPath()를 통해 받아온다
        getRealPathFromURL(uriList);

        ArrayList<MultipartBody.Part> galleryImage = new ArrayList<>();
        for (int i = 0; i < imagePath.size(); i++) {
            File file = new File(imagePath.get(i));

            // -------------> 이미지 압축 과정 시작
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uriList.get(i));
//                contentResolver 주소를 통해 데이터를 접근해서 결과를 가져온다.
//                openInputStream은 uri와 연결된 콘텐츠에 대한 스트림(데이터가 전송되는 통로)을 연다.
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            입력스트림을 비트맵으로 디코딩한다.
//            매개변수는 비트맵으로 디코딩할 수 원시데이터를 가지고 있는 입력 스트림
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            이 클래스는 출력 스트림이며(데이터가 나가는 통로의 역할에 대해 규정),
//            데이터가 바이트 배열에 기록된다.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
//            지정된 출력 스트림에대한 비트맵 버전을 압축한다. 즉 사진 용량 줄이는 과정
//            매개변수 1 : 압축된 이미지의 형식, 품질 (0-100),
//            압축된 데이터를 쓰기 위한(데이터를 내보낸다는 뜻) 출력 스트림

            // -------------> 이미지 압축 과정 끝

//        사진 requestbody -> multipart
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), byteArrayOutputStream.toByteArray());
//        http 요청 또는 응답 본문의 콘텐츠 유형을 설명한다.
            galleryImage.add(MultipartBody.Part.createFormData("uploaded_file[]", file.getName(), requestFile));
            Log.e(TAG, "uploaded_file" + galleryImage.get(i));
//        인터페이스에서 파일을 보내기 위해 컨탠트 파입을 multipart 로 명시해주었기 때문에 requestbody를  multipart로 변경한다.
//        그리고 images라는 배열에 넣어준다.
        }

//        텍스트 데이터 보내는 과정
        map = new HashMap<>();

        for (int i = 0; i < serverImage.size(); i++) {
            RequestBody serverImageName = RequestBody.create(MediaType.parse("text/plain"), serverImage.get(i));
            map.put(i + "serverImageName", serverImageName);
        }
        RequestBody serverImageCount = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(serverImage.size()));
        map.put("serverImageCount", serverImageCount);


        RequestBody sreview = RequestBody.create(MediaType.parse("text/plain"), ereview);
        RequestBody stitle = RequestBody.create(MediaType.parse("text/plain"), etitle);
        RequestBody scafe = RequestBody.create(MediaType.parse("text/plain"), ecafe);
        RequestBody sroad_address = RequestBody.create(MediaType.parse("text/plain"), eroad_address);
        RequestBody ssns_url = RequestBody.create(MediaType.parse("text/plain"), esns_url);
        RequestBody sfinal_number = RequestBody.create(MediaType.parse("text/plain"), final_number);

        RequestBody slast_order = RequestBody.create(MediaType.parse("text/plain"), glast_order);
        RequestBody sdessert = RequestBody.create(MediaType.parse("text/plain"), gdessert);
        RequestBody sarea = RequestBody.create(MediaType.parse("text/plain"), area);

        RequestBody smapx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(mapx));
        RequestBody smapy = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(mapy));
        RequestBody stema = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(gtema + 1));
        RequestBody sfood = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(gfood));
        RequestBody scafe_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(cafe_id));
        RequestBody image_count = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(galleryImage.size()));


        map.put("cafe_id", scafe_id);
        map.put("cafe", scafe);
        map.put("road_address", sroad_address);
        map.put("a_id", sarea);
        map.put("mapx", smapx);
        map.put("mapy", smapy);
        map.put("title", stitle);
        map.put("phone_number", sfinal_number);
        map.put("last_order", slast_order);
        map.put("sns_url", ssns_url);
        map.put("dessert_name", sdessert);
        map.put("t_id", stema);
//        cafe db 에 저장할 것
        map.put("review", sreview);
        map.put("f_id", sfood);
//        리뷰 db 에 저장할 것
        map.put("image_count", image_count);
//        새로 업데이트할 이미지가 몇개 있는지


        Call<String> call = NetWorkHelper.getInstance().getApiService().modifyCafe(galleryImage, map);
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

                    if(ActivityCafeContent.activityCafeContent != null) // 액티비티가 살아있다면
                    {
                        ActivityCafeContent activity = ActivityCafeContent.activityCafeContent;
                        activity.finish();
                    }

                    if(Activity_dessertList.activity != null) // 액티비티가 살아있다면
                    {
                        Activity_dessertList activity = Activity_dessertList.activity;
                        activity.finish();
//                            액티비티 종료 해주기
                    }

                    Toast.makeText(ActivityModifyCafe.this, "변경완료 되었습니다.", Toast.LENGTH_SHORT).show();
//                    myDialog.CheckDialog("변경완료 ");
                    Intent intent = new Intent(ActivityModifyCafe.this, Activity_dessertList.class);
//                    intent.putExtra("cafe_id", cafe_id);
                    intent.putExtra("f_id", gfood);
                    startActivity(intent);
                    finish();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });
    }

    //    디저트 스피너 인텐트로 받은 값 세팅
    private void setDessertSpinner_byIntent(String[] resource) {
        Log.e(TAG, "setDessertSpinner_byIntent" + resource);

        dadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, resource);
        dessert_spinner2.setAdapter(dadapter);
        dessert_spinner2.setSelection(Arrays.asList(resource).indexOf(i_dessert), true);
        gdessert = i_dessert;
        Log.e(TAG, String.valueOf(Arrays.asList(resource).indexOf(i_dessert)));

        dessert_spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                gdessert = dessert_spinner2.getSelectedItem().toString();
                Log.e(TAG, "디저트 카테고리1 스피너 선택 결과" + gdessert);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //    디저트 스피너 첫번째 선택 후
    private void setDessertSpinner(String[] resource) {
        Log.e(TAG, "setDessertSpinner" + gdessert);
        dadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, resource);
        dessert_spinner2.setAdapter(dadapter);

        dessert_spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                gdessert = dessert_spinner2.getSelectedItem().toString();
                Log.e(TAG, "디저트 카테고리1 스피너 선택 결과" + gdessert);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //        image 의 절대경로를 가져오는 메소드 실제 sd카드의 경로를 리턴해주는 함수이다.
    private ArrayList<String> getRealPathFromURL(ArrayList<Uri> uri) {
        Log.e(TAG, "getRealPathFromURL : " + uri.size());
        Cursor cursor = null;
//        쿼리한 데이터를 순회회하는 역할을 cursor가 한다.
//        cursor에는 쿼리할때 projection으로 요청한 컬럼들이 포함되어 있다. 즉 db의 칼럼들이 저장되어 있다.
        try {

            for (Integer i = 0; i < uri.size(); i++) {

                String[] proj = {MediaStore.Images.Media.DATA};
//            MediaStore.Images는 타입이 image인 미디어의 모음집이다.
//            MediaStore.Images.Media.DATA 는 캐시된 열의 인덱스인듯
//            쿼리에서 받는 인자 값 형태가 string 배열 형태이기 떄문에

                Log.e(TAG, "타입이 image인 미디어의 모음집" + proj);
                cursor = getContentResolver().query(uri.get(i), proj, null, null, null);
//            쿼리의 인자값 1. uri 찾고자 하는 데이터의 uri /2. projection db의 칼럼과 같다. 결과로 받고 싶은 데이터의 종류를 알려준다.
//            3.Selection : DB 의 where 키워드와 같다. 어떤 조건으로 필터링된 결과를 받을 때 사용 /4.selection과 함께 사용 /5. 쿼리 결과 데이터를 분류 할 때 사용
                Log.e(TAG, "절대 경로3/" + cursor);

                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            컬럼의 인덱스를 얻는 코드이다 왜 컬럼의 인덱스를 얻지?
//            커서로 컬럼을 읽을 때는 getLOng 또는 getString 함수에 인자값으로 컬럼의 index를 넣어야 하기 때문
                Log.e(TAG, "절대 경로4/" + column_index);
                cursor.moveToFirst();
                Log.e(TAG, "절대 경로5/" + cursor.getString(column_index));
                imagePath.add(cursor.getString(column_index));
                Log.e(TAG, "imagePath 값 : " + imagePath.toString() + "//" + uri.size());
//                getString 은 욫청된 열의 값을 문자열로 반환
            }

            return imagePath;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //결과값 받는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 인자 설명 1: 액티비티 식별값, 2:setResult()에서 보낸 값,
        // 3: putExtra() 를 통해 인텐트에서 보내준 값

        Log.e(TAG, "onActivityResult ");

        if (requestCode == CAFERESULT) {
            if (resultCode == RESULT_OK) {

                Log.e(TAG, "CAFERESULT RESULT_OK 시작");
                Log.e(TAG, "Intent 로 전달한 값 받기");

                get_road_address = data.getStringExtra("road_address");
                cafe_result = data.getStringExtra("cafe");
//                받는 값이 int면 만약 오는 값이 없을 때 저장되는 기본값을 설정 꼭 해줘야 한다.
                mapx = data.getIntExtra("mapx", 0);
                mapy = data.getIntExtra("mapy", 0);
//                카페 찾기를 통해 받아온 값을 et 세팅해준다.
                cafe.setText(Html.fromHtml(cafe_result));
                road_address.setText(get_road_address);

                Log.e(TAG, "주소에서 지역만 자른 후 area 변수에 저장");
                //                '0'부터 ~ '공백'까지 자른다는 뜻
                area = get_road_address.substring(0, get_road_address.indexOf(" "));
                Log.e(TAG, get_road_address + cafe_result + mapx + mapy + area);
            }

        } else if (requestCode == IMAGERESULT) {
            if (resultCode == RESULT_OK) {

//                이미지를 하나 선택한 경우
                if (data.getClipData() == null) {
                    Log.e("single choice: ", String.valueOf(data.getData()));
                    Uri imageUri = data.getData();
                    uriList.add(imageUri);
                    Log.e(TAG, String.valueOf(uriList.size()));
                    adapterMultiImage.notifyItemInserted(uriList.size());
                }
//                이미지를 여러장 선택한 경우
                else {
                    ClipData clipData = data.getClipData();
                    Log.e("clipData: 사진이 몇개 반환되었는지", String.valueOf(clipData.getItemCount()));

                    if (clipData.getItemCount() > 3) {  //선택한 이미지가 4장 이상인 경우
                        Toast.makeText(getApplicationContext(), "사진은 3장까지만 선택 가능합니다.", Toast.LENGTH_LONG).show();
                    } else {  //선택한 이미지가 1장 이상 4장 이하인 경우
                        Log.e(TAG, "multiple choice");

                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri imageUri = clipData.getItemAt(i).getUri();
//                            선택한 이미지의 uri 를 가져온다.
                            try {
                                Log.e(TAG, String.valueOf(uriList.size()));
                                uriList.add(imageUri); // uri를 list에 담는다
                                Log.e(TAG, String.valueOf(uriList.size()));
                                adapterMultiImage.notifyItemInserted(uriList.size());

                            } catch (Exception e) {
                                Log.e(TAG, "File select error", e);
                            }
                        }
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //                    취소시 호출할 행동 쓰기
                Toast.makeText(this, TAG + "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }

    }
}