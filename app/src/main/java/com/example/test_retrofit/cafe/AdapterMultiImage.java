package com.example.test_retrofit.cafe;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test_retrofit.R;

import java.util.ArrayList;

public class AdapterMultiImage extends RecyclerView.Adapter<AdapterMultiImage.ViewHolder> {

    private ArrayList<Uri> mData = null;
    private final String TAG = this.getClass().getSimpleName();

    AdapterMultiImage(ArrayList<Uri> list){
        mData = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View image_item = LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_image_item,parent,false);
        ViewHolder holder = new ViewHolder(image_item);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri image_uri = mData.get(position);

        Glide.with(holder.image.getContext()).
                load(image_uri).into(holder.image);
    }


    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageButton clear_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            뷰 객체에 대한 참조
            image = itemView.findViewById(R.id.image);
            clear_image = itemView.findViewById(R.id.clear_image);

            clear_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if( position != RecyclerView.NO_POSITION){
                        mData.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mData.size());
                    }
                }
            });

        }
    }
}
