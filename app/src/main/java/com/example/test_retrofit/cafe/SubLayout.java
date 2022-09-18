package com.example.test_retrofit.cafe;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.test_retrofit.R;

import java.util.ArrayList;

public class SubLayout extends LinearLayout {


    private ArrayList<String> image_array = new ArrayList<>();

    public SubLayout(Context context, AttributeSet attrs, ArrayList<String> image_array) {
        super(context, attrs);
        init(context, image_array);
    }

    public SubLayout(Context context, ArrayList<String> image_array) {
        super(context);
        init(context, image_array);
    }

    private void init(Context context, ArrayList<String> image_array) {

        for(int i =0 ; i<image_array.size();i++) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.layout_sub_layout, this, true);

            ImageView img = (ImageView) findViewById(R.id.glide_imageview);



            // 이미지 로드 라이브러리 사용 ImageUrl to Image
            Glide.with(this)
                    .load("http://3.39.153.170/test/upload/review/" + image_array.get(i))
                    .override(300, 300)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(img);

        }
    }
}

