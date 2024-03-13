package com.example.test_retrofit.cafe;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.test_retrofit.MyDialog;
import com.example.test_retrofit.R;
import com.example.test_retrofit.Retrofit.NetWorkHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentCafeReview extends Fragment {
    private View view;
    private final String TAG = this.getClass().getSimpleName();
    //    레이아웃 아이디
    private TextView review, nickname, maxText, star_number;
    private ImageView profile, gallery;
    private RecyclerView recyclerView, cafe_image;
    private EditText etreview;
    private Button register, modify;
    private RatingBar ratingBar;
    private ProgressBar progressBar;

    private AdapterReviewList adapterReviewList;
    private LinearLayoutManager linearLayoutManager;
    private List<DTOReview.Review> reviewList;
    MyDialog myDialog;
    //    수정할 아이템의 위치 값 저장하는 변수
    private Integer modifyPosition = -1;
    private boolean noLoading = true;

    // 1페이지에 10개씩 데이터를 불러온다
    int page = 1, limit = 5;

    //    사진 선택 후 결과 구분 숫자
    private Integer IMAGERESULT = 1;

    //갤러리에서 최종적으로 선택한 상대 uri 경로
    private ArrayList<Uri> galleryUriList = new ArrayList<>();
    private AdapterMultiImage adapterMultiImage;
    private ArrayList<String> galleryRealPath = new ArrayList<>();
    private ImageHelper imageHelper;
    private HashMap<String, RequestBody> map;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cafe_review, container, false);
        review = view.findViewById(R.id.review);
        nickname = view.findViewById(R.id.nickname);
        profile = view.findViewById(R.id.profile);
        recyclerView = view.findViewById(R.id.review_list);
        etreview = view.findViewById(R.id.etreview);
        register = view.findViewById(R.id.register);
        maxText = view.findViewById(R.id.maxText);
        ratingBar = view.findViewById(R.id.ratingBar);
        star_number = view.findViewById(R.id.rating);
        modify = view.findViewById(R.id.modify);
        progressBar = view.findViewById(R.id.progress_bar);
        gallery = view.findViewById(R.id.image);
        cafe_image = view.findViewById(R.id.cafe_image);

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        reviewList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        myDialog = new MyDialog(getActivity());
        imageHelper = new ImageHelper(getContext());


        // 댓글 페이징 부분을 위한 변수
        page = 1;
        noLoading = true;

        // 유저의 카페 추천 이유 부분 세팅해주기
        Bundle bundle = getArguments();
        if (bundle != null) {
            review.setText(bundle.getString("review"));
        }

        nickname.setText(bundle.getString("nickname") + "의 카페 추천 이유");


        Glide.with(requireActivity()).
                load("http://43.200.106.233/test/upload/profile/" + bundle.getString("profile")).
                transform(new CenterCrop(), new RoundedCorners(70)).
                into(profile);

        // 유저의 카페 추천 이유 부분 세팅해주기

        SetReview(page, limit);

        recyclerView.setLayoutManager(linearLayoutManager);
        adapterReviewList = new AdapterReviewList(reviewList,getContext());

        if (reviewList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                star_number.setText(String.valueOf(rating));
            }
        });


        gallery.setOnClickListener(new View.OnClickListener() {
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

        //리뷰 리사이클러뷰 스크롤이 최하단으로 가면 댓글 불러오기
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                int itemTotalCount = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();

                if (!recyclerView.canScrollVertically(1) && noLoading) //스크롤이 최하단으로 가면
                {
                    page++;
                    Log.e(TAG, lastItemPosition + "/" + itemTotalCount);
                    progressBar.setVisibility(View.VISIBLE);
                    SetReview(page, limit);

                }
            }
        });


        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                수정 취소를 눌렀을 때
                if ("취소".equals(register.getText())) {
//                    수정 버튼 비활성화, 리뷰 작성 et에 있는 글 삭제,버튼 취소->등록 으로 글 변경
                    modify.setVisibility(View.INVISIBLE);
                    etreview.setText("");
                    register.setText("등록");
                    modifyPosition = -1;
                    ratingBar.setRating(3);
//                    myDialog.CheckDialog("취소");
                } else {
                    if(etreview.getText().toString().isEmpty())
                    {
                        myDialog.CheckDialog("리뷰 작성을 해주세요");
                    }

                    else{
                        SendReview();
                    }

                }
            }
        });

        //리뷰 수정버튼을 눌렀을 때
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modifyPosition == -1) {
                    myDialog.CheckDialog("수정 오류 다시 시도하세요");
                } else {
                    ModifyReview(modifyPosition);
                }
            }
        });

//        리뷰글 작성시 문자개수 변경
        etreview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                maxText.setText(etreview.getText().length() + "/400");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        adapterReviewList.setOnItemClickListener(new AdapterReviewList.OnItemClickListener() {
            @Override
            public void onModifyClick(View v, int position) {
                if (etreview.getText().length() > 0) {

                    builder.setTitle("작성하던 글을 삭제하고 수정하시겠습니까?").setPositiveButton("네",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    register.setText("취소");
                                    modify.setVisibility(View.VISIBLE);
                                    etreview.setText("");
                                    etreview.setText(reviewList.get(position).getReview());
                                    ratingBar.setRating(reviewList.get(position).getStar());
                                    modifyPosition = position;
                                }
                            })
                            .setNeutralButton("취소", null).show();
                } else {
                    register.setText("취소");
                    modify.setVisibility(View.VISIBLE);
                    etreview.setText(reviewList.get(position).getReview());
                    ratingBar.setRating(reviewList.get(position).getStar());
                    modifyPosition = position;
                }


            }

            @Override
            public void onDeleteClick(View v, int position) {

                builder.setTitle("삭제하기").setPositiveButton("삭제하기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteReview(position);
                            }
                        })
                        .setNeutralButton("취소", null).show();
            }
        });

        return view;
    }

    public void ModifyReview(int position) {

        Call<DTOReview> call = NetWorkHelper.getInstance().getApiService().modifyReview(reviewList.get(position).getReview_id(), etreview.getText().toString(),
                ratingBar.getRating());
        call.enqueue(new Callback<DTOReview>() {
            @Override
            public void onResponse(Call<DTOReview> call, Response<DTOReview> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Log.e("onSuccess", String.valueOf(response.body()));
                    Log.e(TAG, "서버에서 받아온 값 형태 " + response + response.body());
                    DTOReview DTOGroupResponse = response.body();
                    Log.e(TAG, "응답 성공 후 dto 파싱해준 값1" + DTOGroupResponse);

                    if (DTOGroupResponse.getStatus().equals("true")) {
                        reviewList.get(position).setReview(etreview.getText().toString());
                        reviewList.get(position).setStar(ratingBar.getRating());

                        modify.setVisibility(View.INVISIBLE);
                        register.setText("등록");
                        etreview.setText("");
                        ratingBar.setRating(3);
                        adapterReviewList.notifyItemChanged(position);
                        recyclerView.scrollToPosition(position);


                    } else {
                        myDialog.CheckDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }

                } else {
                    Log.e(TAG, "실패" + response.body());
                }
            }

            @Override
            public void onFailure(Call<DTOReview> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
                myDialog.CheckDialog("삭제 실패 다시 시도해주세요");
            }

        });
    }

    public void deleteReview(int position) {

        Call<DTOReview> call = NetWorkHelper.getInstance().getApiService().deleteReview(reviewList.get(position).getReview_id());
        call.enqueue(new Callback<DTOReview>() {
            @Override
            public void onResponse(Call<DTOReview> call, Response<DTOReview> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Log.e("onSuccess", String.valueOf(response.body()));
                    Log.e(TAG, "서버에서 받아온 값 형태 " + response + response.body());
                    DTOReview DTOGroupResponse = response.body();
                    Log.e(TAG, "응답 성공 후 dto 파싱해준 값1" + DTOGroupResponse);

                    if (DTOGroupResponse.getStatus().equals("true")) {
                        reviewList.remove(position);
                        adapterReviewList.notifyItemRemoved(position);
                    } else {
                        myDialog.CheckDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }

                } else {
                    Log.e(TAG, "실패" + response.body());
                }
            }

            @Override
            public void onFailure(Call<DTOReview> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
                myDialog.CheckDialog("삭제 실패 다시 시도해주세요");
            }

        });
    }

    public void SetReview(int page, int limit) {
        Call<DTOReview> call = NetWorkHelper.getInstance().getApiService().
                setReview(page, limit, ActivityCafeContent.activityCafeContent.cafe_id);
        call.enqueue(new Callback<DTOReview>() {
            @Override
            public void onResponse(Call<DTOReview> call, Response<DTOReview> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "서버에서 받아온 값 형태 " + response + response.body());
                    DTOReview DTOGroupResponse = response.body();
                    Log.e(TAG, "응답 성공 후 dto 파싱해준 값1" + DTOGroupResponse);

                    if (DTOGroupResponse.getStatus().equals("true")) {
                        List<DTOReview.Review> items = DTOGroupResponse.getData();
                        Log.e(TAG, "응답 성공 후 dto 파싱해준 값2" + items);
                        progressBar.setVisibility(View.GONE);
                        setDataList(items);
                    } else {
                        myDialog.CheckDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }
                } else {
                    Log.e(TAG, "실패" + response.body());
                }

            }

            @Override
            public void onFailure(Call<DTOReview> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });

    }

    public void SendReview() {
        galleryRealPath = imageHelper.getRealPathFromURL(galleryUriList);
        Log.e(TAG,"SENDREVIEW"+ galleryRealPath.size());

        ArrayList<MultipartBody.Part> images = new ArrayList<>();

        for(Integer i =0 ; i<galleryRealPath.size(); i++) {
            File file = new File(galleryRealPath.get(i));

            // -------------> 이미지 압축 과정 시작
            InputStream inputStream = null;
            try {
                inputStream = getContext().getContentResolver().openInputStream(galleryUriList.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

            // -------------> 이미지 압축 과정 끝
//        사진 requestbody -> multipart
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), byteArrayOutputStream.toByteArray());

//        http 요청 또는 응답 본문의 콘텐츠 유형을 설명한다.
            images.add( MultipartBody.Part.createFormData("uploaded_file[]", file.getName(), requestFile));
            Log.e(TAG,"uploaded_file"+images.get(i));
        }

        map = new HashMap<>();

        RequestBody cafe_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(ActivityCafeContent.activityCafeContent.cafe_id));
        RequestBody login_id = RequestBody.create(MediaType.parse("text/plain"), ActivityCafeContent.login_id);
        RequestBody review = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(etreview.getText()));
        RequestBody star = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(star_number.getText()));
        RequestBody image_count = RequestBody.create(MediaType.parse("text/plain"),String.valueOf(images.size()));

        map.put("id_cafe",cafe_id);
        map.put("id",login_id);
        map.put("review",review);
        map.put("star",star);
        map.put("image_count",image_count);

        Call<DTOReview> call = NetWorkHelper.getInstance().getApiService().sendReview(images,map);
        call.enqueue(new Callback<DTOReview>() {
            @Override
            public void onResponse(Call<DTOReview> call, Response<DTOReview> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "서버에서 받아온 값 형태 " + response + response.body());
                    DTOReview DTOGroupResponse = response.body();
                    Log.e(TAG, "응답 성공 후 dto 파싱해준 값1" + DTOGroupResponse);

                    if (DTOGroupResponse.getStatus().equals("true")) {
                        List<DTOReview.Review> items = DTOGroupResponse.getData();
                        Log.e(TAG, "응답 성공 후 dto 파싱해준 값2" + items);
                        generateDataList(items);
                    } else {
                        myDialog.CheckDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }
                } else {
                    Log.e(TAG, "실패" + response.body());
                }

            }

            @Override
            public void onFailure(Call<DTOReview> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });

    }

    private void setDataList(List<DTOReview.Review> items) {
        Log.e(TAG, "setDataList 받은 인자값" + items);
        for (int i = 0; i < items.size(); i++) {
            reviewList.add(items.get(i));
        }
        recyclerView.setVisibility(View.VISIBLE);
        if (page > 1 && !items.isEmpty()) {
            recyclerView.scrollToPosition(reviewList.size() - 5);
        }

        if (items.isEmpty() && page > 1) {

            recyclerView.scrollToPosition(reviewList.size() - 2);
            Toast.makeText(getContext(), "마지막 리뷰입니다", Toast.LENGTH_SHORT).show();
            noLoading = false;
        }

        recyclerView.setAdapter(adapterReviewList);
    }

    private void generateDataList(List<DTOReview.Review> items) {
        Log.e(TAG, "items" + items);

        reviewList.add(0, (items.get(0)));

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.scrollToPosition(0);
//        리사이클러뷰의 스크롤을 특정 위치로 이동하고 싶을 때 설정해준다.
//        인자 값은 positon
        adapterReviewList.notifyItemInserted(0);
        etreview.setText("");
    }

    //결과값 받는 함수
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // 인자 설명 1: 액티비티 식별값, 2:setResult()에서 보낸 값,
//        // 3: putExtra() 를 통해 인텐트에서 보내준 값
//
//
//        Log.e(TAG, "onActivityResult 시작");
//
//        if (requestCode == IMAGERESULT) {
//            if (resultCode == RESULT_OK) {
//
////                이미지를 하나라도 선택한 경우
//                if (data.getClipData() == null) {
//                    Log.e("single choice: ", String.valueOf(data.getData()));
//                    Uri imageUri = data.getData();
//                    galleryUriList.add(imageUri);
//
//                    adapterMultiImage = new AdapterMultiImage(galleryUriList);
//                    cafe_image.setAdapter(adapterMultiImage);
//                    cafe_image.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
//                    cafe_image.setVisibility(View.VISIBLE);
//
//
//                }
////                이미지를 여러장 선택한 경우
//                else {
//                    ClipData clipData = data.getClipData();
//                    Log.e("clipData: 사진이 몇개 반환되었는지", String.valueOf(clipData.getItemCount()));
//
//                    if (clipData.getItemCount() > 3) {  //선택한 이미지가 4장 이상인 경우
//                        Toast.makeText(getContext(), "사진은 3장까지만 선택 가능합니다.", Toast.LENGTH_LONG).show();
//                    } else {  //선택한 이미지가 1장 이상 4장 이하인 경우
//                        Log.e(TAG, "multiple choice");
//
//                        for (int i = 0; i < clipData.getItemCount(); i++) {
//                            Uri imageUri = clipData.getItemAt(i).getUri();
////                            선택한 이미지의 uri 를 가져온다.
//                            try {
//                                galleryUriList.add(imageUri); // uri를 list에 담는다
//                            } catch (Exception e) {
//                                Log.e(TAG, "File select error", e);
//                            }
//                        }
//                    }
//                    adapterMultiImage = new AdapterMultiImage(galleryUriList);
//                    cafe_image.setAdapter(adapterMultiImage);
//                    cafe_image.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//                    cafe_image.setVisibility(View.VISIBLE);
////                    리사이클러뷰 수평 스크롤 적용
//
//                }
//
//
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                //                    취소시 호출할 행동 쓰기
//                Toast.makeText(getContext(), TAG + "사진 선택 취소", Toast.LENGTH_LONG).show();
//            }
//        }
//
//    }

}