package com.example.test_retrofit.chat;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.test_retrofit.R;
import com.example.test_retrofit.group.AdapterGroup;

import java.util.ArrayList;
import java.util.List;


public class AdapterUserList extends RecyclerView.Adapter<AdapterUserList.UserListViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private List <DTOChat> userItemList = new ArrayList<>();

    public AdapterUserList (List<DTOChat> userItemList){
        this.userItemList = userItemList;
    }

    @NonNull
    @Override
    public AdapterUserList.UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list,parent,false);
        UserListViewHolder holder = new UserListViewHolder(item);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUserList.UserListViewHolder holder, int position) {
        Glide.with(holder.profile.getContext()).
                load("http://3.39.153.170/test/upload/profile/"+userItemList.get(position).getProfile_name()).
                transform(new CenterCrop(), new RoundedCorners(70)).
                into( holder.profile);

        holder.nickname.setText(userItemList.get(position).getNickname());

        if(ActivityChatRoom.host.equals(userItemList.get(position).getId())) {
            holder.host.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return userItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    interface OnItemClickListener {
        void onItemClick(View v, int position);
        void onExitClick(View v, int position);

    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {
        protected ImageView profile,host;
        protected TextView nickname;
        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profile);
            host = itemView.findViewById(R.id.host);
            nickname = itemView.findViewById(R.id.nickname);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position =getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION)
                    {
                        if(mListener != null)
                        {
                            mListener.onItemClick(v,position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onExitClick(view, position);
                        }
                    }
                    return true;
                }

            });//롱클릭 리스너함수
        }
    }
}
