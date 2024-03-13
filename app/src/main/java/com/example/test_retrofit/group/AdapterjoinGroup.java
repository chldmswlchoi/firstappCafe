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

public class AdapterjoinGroup extends RecyclerView.Adapter<AdapterjoinGroup.joinGroupViewHolder> {
    private List <DTOGroupResponse.GroupDTO> joinGroupItemList = new ArrayList<>();
    private Context context;
    private final String TAG = this.getClass().getSimpleName();

    public AdapterjoinGroup(List<DTOGroupResponse.GroupDTO>joinGroupItemList, Context context)
    {
        this.joinGroupItemList = joinGroupItemList;
        this.context=context;
    }
    @NonNull
    @Override
    public joinGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_join_group,parent,false);
        joinGroupViewHolder holder = new joinGroupViewHolder(item);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull joinGroupViewHolder holder, int position) {
        DTOGroupResponse.GroupDTO specificItems = joinGroupItemList.get(position);

        Glide.with(holder.profile.getContext()).
                load("http://43.200.106.233/test/upload/profile/"+specificItems.getProfile()).
                into(holder.profile);

        holder.cafe.setText(specificItems.getCafe());
        holder.title.setText(specificItems.getTitle());
        holder.date.setText(specificItems.getGdate());
        holder.location.setText(specificItems.getRoad_address());
        holder.total_member.setText( String.valueOf(specificItems.getTotal_member()));
        holder.people.setText( String.valueOf(specificItems.getPeople()));
    }

    @Override
    public int getItemCount() {
        return joinGroupItemList.size();

    }

    interface OnItemClickListener {
        void onItemClick(View v, int position);

    }
    private AdapterjoinGroup.OnItemClickListener mListener =null;


    public void setOnItemClickListener(AdapterjoinGroup.OnItemClickListener listener) {
        this.mListener = listener;


    }



    public class joinGroupViewHolder extends RecyclerView.ViewHolder {
        protected TextView title,cafe,date,location,total_member,people;
        protected ImageView profile;

        public joinGroupViewHolder(@NonNull View itemView) {
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

        }
    }
}
