package com.example.test_retrofit.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test_retrofit.R;
import com.example.test_retrofit.group.DTO.DTOGroupResponse;

import java.util.ArrayList;
import java.util.List;

public class AdapterGroup extends RecyclerView.Adapter <AdapterGroup.GroupViewHolder> {

    private List<DTOGroupResponse.GroupDTO> groutItemList = new ArrayList<>();
    private Context context;
    //전체 데이터 값이 답겨 있는 리스트 -> (리스트는 동적이다)

    public AdapterGroup(List<DTOGroupResponse.GroupDTO> groutItemList, Context context) {
        this.groutItemList = groutItemList;
        this.context = context;
    }

    @NonNull
    @Override

    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_items,parent,false);
        GroupViewHolder holder = new GroupViewHolder(item);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        DTOGroupResponse.GroupDTO itemlist = groutItemList.get(position);

//        프로필 사진 홀드 해주는 과정
        Glide.with(holder.profile.getContext()).
                load("http://43.200.106.233/test/upload/profile/"+itemlist.getProfile()).
                centerCrop().
                into(holder.profile);

        holder.cafe.setText(itemlist.getCafe());
        holder.title.setText(itemlist.getTitle());
        holder.date.setText(itemlist.getGdate());
        holder.location.setText(itemlist.getRoad_address());
        holder.total_member.setText( String.valueOf(itemlist.getTotal_member()));
        holder.people.setText( String.valueOf(itemlist.getPeople()));

    }


    interface OnItemClickListener {
        void onItemClick(View v, int position);
        void onDeleteClick(View v, int position);

    }

    private AdapterGroup.OnItemClickListener mListener = null;

    public void setOnItemClickListener(AdapterGroup.OnItemClickListener listener) {
        this.mListener = listener;
    }
    @Override
    public int getItemCount() {
        return groutItemList.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        protected TextView title,cafe,date,location,total_member,people;
        protected ImageView profile;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            cafe = itemView.findViewById(R.id.cafe);
            date = itemView.findViewById(R.id.date);
            location = itemView.findViewById(R.id.location);
            profile = itemView.findViewById(R.id.profile);
            total_member = itemView.findViewById(R.id.total_member);
            people = itemView.findViewById(R.id.people);


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
                            mListener.onDeleteClick(view, position);
                        }
                    }
                    return true;
                }

            });//롱클릭 리스너함수

        }
    }
}
