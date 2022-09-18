package com.example.test_retrofit.cafe;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class ImageHelper {

    private Context context;
    private ArrayList<String> galleryRealPath = new ArrayList<>();

    ImageHelper(Context context){
        this.context = context;
    }

    public ArrayList<String> getRealPathFromURL(ArrayList<Uri> uri) {
        Log.e("ImageHelper", "getRealPathFromURL : " + uri.size());
        Cursor cursor = null;
//        쿼리한 데이터를 순회회하는 역할을 cursor가 한다.
//        cursor에는 쿼리할때 projection으로 요청한 컬럼들이 포함되어 있다. 즉 db의 칼럼들이 저장되어 있다.
        try {
            for (Integer i = 0; i < uri.size(); i++) {
                String[] proj = {MediaStore.Images.Media.DATA};
//            MediaStore.Images는 타입이 image인 미디어의 모음집이다.
//            MediaStore.Images.Media.DATA 는 캐시된 열의 인덱스인듯
//            쿼리에서 받는 인자 값 형태가 string 배열 형태이기 떄문에

                Log.e("ImageHelper", "타입이 image인 미디어의 모음집" + proj);
                cursor = context.getContentResolver().query(uri.get(i), proj, null, null, null);
//            쿼리의 인자값 1. uri 찾고자 하는 데이터의 uri /2. projection db의 칼럼과 같다. 결과로 받고 싶은 데이터의 종류를 알려준다.
//            3.Selection : DB 의 where 키워드와 같다. 어떤 조건으로 필터링된 결과를 받을 때 사용 /4.selection과 함께 사용 /5. 쿼리 결과 데이터를 분류 할 때 사용
                Log.e("ImageHelper", "절대 경로3/" + cursor);

                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            컬럼의 인덱스를 얻는 코드이다 왜 컬럼의 인덱스를 얻지?
//            커서로 컬럼을 읽을 때는 getLOng 또는 getString 함수에 인자값으로 컬럼의 index를 넣어야 하기 때문
                Log.e("ImageHelper", "절대 경로4/" + column_index);
                cursor.moveToFirst();
                Log.e("ImageHelper", "절대 경로5/" + cursor.getString(column_index));
                galleryRealPath.add(cursor.getString(column_index));
                Log.e("ImageHelper", i + "번째 imagePath 값 : " + galleryRealPath.get(i));

//                getString 은 욫청된 열의 값을 문자열로 반환
            }
            return galleryRealPath;
        } finally {
            if (cursor != null) {
                cursor.close();
            }


        }
    }
}
