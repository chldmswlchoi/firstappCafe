package com.example.test_retrofit.cafe;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.test_retrofit.R;
import com.example.test_retrofit.user.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

public class AdapterReviewList extends RecyclerView.Adapter<AdapterReviewList.ReviewViewHolder> {

    private final String TAG = this.getClass().getSimpleName();
    List<DTOReview.Review> reviewList = new ArrayList<>();
    ArrayList<String> arrayList = new ArrayList<>();
    String sampleItem = "https://cdn.pixabay.com/photo/2018/01/10/23/53/rabbit-3075088_1280.png";
    Context context;
    SubLayout subLayout;
    int j = 0;

    public AdapterReviewList(List<DTOReview.Review> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
    }

    @NonNull
    @Override

    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View r_item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cafe_review, parent, false);

        ReviewViewHolder holder = new ReviewViewHolder(r_item);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {

        if (reviewList.get(position).getId().equals(ActivityCafeContent.login_id)) {
            holder.modify.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);
        }
        holder.nickname.setText(reviewList.get(position).getNickname());
        holder.date.setText(reviewList.get(position).getGdate());
        holder.review.setText(reviewList.get(position).getReview());
        holder.star.setRating(reviewList.get(position).getStar());

        Glide.with(holder.profile.getContext()).
                load("http://3.39.153.170/test/upload/profile/" + reviewList.get(position).getProfile()).
                transform(new CenterCrop(), new RoundedCorners(50)).
                into(holder.profile);


//        if (!reviewList.get(position).getImage_array().get(0).isEmpty()) {
//            reviewList.get(position).getImage_array();
//            subLayout = new SubLayout(context, reviewList.get(position).getImage_array());
//            holder.layout.addView(subLayout);
//        }


    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    interface OnItemClickListener {
        void onModifyClick(View v, int position);

        void onDeleteClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        protected TextView nickname, date, review, modify, delete;
        protected ImageView profile;
        protected RatingBar star;
        protected RecyclerView button_group;
//        LinearLayout layout;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            nickname = itemView.findViewById(R.id.nickname);
            date = itemView.findViewById(R.id.date);
            review = itemView.findViewById(R.id.review);
            modify = itemView.findViewById(R.id.modify);
            delete = itemView.findViewById(R.id.delete);
            profile = itemView.findViewById(R.id.profile);
            star = itemView.findViewById(R.id.ratingBar);
//            layout = (LinearLayout) itemView.findViewById(R.id.input_here_image);

            modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        if (mListener != null) {
                            mListener.onModifyClick(v, position);
                        }
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        if (mListener != null) {
                            mListener.onDeleteClick(v, position);
                        }
                    }
                }
            });


        }
    }
}
