package com.example.test_retrofit.cafe;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.test_retrofit.R;


public class FragmentCafeHome extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    private View view;
    private View dessert,brunch,fruit,vegan;
    private LinearLayout made_cafe;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG, "카페 클릭 프레그먼트 ONCREATEVIEW");
        view = inflater.inflate(R.layout.fragment_cafe_home, container, false);
        dessert = view.findViewById(R.id.dessert);
        brunch = view.findViewById(R.id.brunch);
        fruit = view.findViewById(R.id.fruit);
        vegan = view.findViewById(R.id.vegan);
        made_cafe = view.findViewById(R.id.made_cafe);

        made_cafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getActivity(), Activity_madeCafe.class);
                startActivity(intent);
            }
        });

        dessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Activity_dessertList.class);
                intent.putExtra("f_id",1);
                startActivity(intent);
            }
        });

        brunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Activity_dessertList.class);
                intent.putExtra("f_id",2);
                startActivity(intent);
            }
        });

        fruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Activity_dessertList.class);
                intent.putExtra("f_id",3);
                startActivity(intent);
            }
        });

        vegan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Activity_dessertList.class);
                intent.putExtra("f_id",4);
                startActivity(intent);
            }
        });

        return view;
    }
}