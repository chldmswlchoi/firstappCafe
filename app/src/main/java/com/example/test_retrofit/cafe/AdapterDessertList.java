package com.example.test_retrofit.cafe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.test_retrofit.R;
import com.example.test_retrofit.group.NSAdpater;

import java.util.ArrayList;
import java.util.List;

public class AdapterDessertList extends RecyclerView.Adapter<AdapterDessertList.DessertViewHolder> {
    private final String TAG = this.getClass().getSimpleName();

    List<DTOCafe.CafeData> cafeData = new ArrayList<>();
//    Context context;

    public AdapterDessertList(List<DTOCafe.CafeData> cafeData) {
        this.cafeData = cafeData;
//        this.context = context;
    }

    @NonNull
    @Override
    public DessertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cafe_item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cafe, parent, false);
        DessertViewHolder holder = new DessertViewHolder(cafe_item);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DessertViewHolder holder,  int position) {

        DTOCafe.CafeData cafeList = cafeData.get(position);
        holder.cafe.setText(cafeList.getCafe());
        holder.title.setText(cafeList.getTitle());
        holder.location.setText(cafeList.getRoad_address());

        //        프로필 사진 홀드 해주는 과정
        Glide.with(holder.cafe_image.getContext()).
                load("http://43.200.106.233/test/upload/cafe/" + cafeList.getImage_array().get(0)).
                transform(new CenterCrop(), new RoundedCorners(20)).
                into(holder.cafe_image);



        if(cafeList.getIs_loved()==1)
        {
            holder.tb_love.setChecked(true);
            holder.tb_love.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
//                        Toast.makeText(buttonView.getContext(), "좋아요2"+holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();

                    } else {
//                        Toast.makeText(buttonView.getContext(), "좋아요 취소2+"+holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();

                        Log.e(TAG,cafeData.get(holder.getAdapterPosition()).getIs_loved().toString());
                    }
                }
            });
        }
        else {
            holder.tb_love.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
//                        Toast.makeText(buttonView.getContext(), "좋아요1"+holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();

                        Log.e(TAG,cafeData.get(holder.getAdapterPosition()).getIs_loved().toString());
                    } else {
//                        Toast.makeText(buttonView.getContext(), "좋아요 취소1"+holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return cafeData.size();
    }


    public interface OnItemClickListener {
        void onItemClick(View v, int position);
        void toggleLoveButton(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


//    interface OnCheckedChangeListener
//    {}
//
//    private OnCheckedChangeListener bListener = null;
//
//    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
//        this.bListener = listener;
//    }

    public class DessertViewHolder extends RecyclerView.ViewHolder {
        protected TextView cafe, title, location;
        protected ImageView cafe_image;
        protected ToggleButton tb_love;

        public DessertViewHolder(@NonNull View itemView) {
            super(itemView);

            cafe = itemView.findViewById(R.id.cafe);
            title = itemView.findViewById(R.id.title);
            location = itemView.findViewById(R.id.location);
            cafe_image = itemView.findViewById(R.id.cafe_image);
            tb_love = itemView.findViewById(R.id.like);

            tb_love.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        if (mListener != null) {
                            mListener.toggleLoveButton(v, position);
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        if (mListener != null) {
                            mListener.onItemClick(view, position);
                        }
                    }
                }
            });

        }
    }
}
