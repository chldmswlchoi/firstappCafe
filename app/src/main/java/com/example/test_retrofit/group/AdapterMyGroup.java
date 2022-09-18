package com.example.test_retrofit.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_retrofit.R;
import com.example.test_retrofit.group.DTO.DTOGroupResponse;

import java.util.ArrayList;
import java.util.List;

public class AdapterMyGroup extends RecyclerView.Adapter<AdapterMyGroup.MyGroupViewHolder> {

    List<DTOGroupResponse.GroupDTO> mygroupList = new ArrayList<>();
    private Context context;


    public AdapterMyGroup(List<DTOGroupResponse.GroupDTO> mygroupList, Context context) {
        this.mygroupList = mygroupList;
        this.context = context;
    }


    @NonNull
    @Override
    public MyGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_group_item, parent, false);
        MyGroupViewHolder holder = new MyGroupViewHolder(item);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyGroupViewHolder holder, int position) {

        DTOGroupResponse.GroupDTO itemList = mygroupList.get(position);

        holder.cafe.setText(itemList.getCafe());
        holder.title.setText(itemList.getTitle());
        holder.date.setText(itemList.getGdate());
        holder.location.setText(itemList.getRoad_address());
        holder.total_member.setText(String.valueOf(itemList.getTotal_member()));
        holder.people.setText(String.valueOf(itemList.getPeople()));


//        마감여부에 따라 마감하기 or 마감취소 버튼
        if (itemList.getFinish() == 0) {
        } else {
            holder.finish.setText("마감취소");
        }
    }

    @Override
    public int getItemCount() {
        return mygroupList.size();
    }


    interface OnItemClickListener {
        void onItemClick(View v, int position);

        void onButtonClick(View v, int position);

        void onDeleteClick(View v, int position);

    }

    private AdapterMyGroup.OnItemClickListener mListener = null;


    public void setOnItemClickListener(AdapterMyGroup.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class MyGroupViewHolder extends RecyclerView.ViewHolder {
        protected TextView title, cafe, date, location, total_member, people;
        protected Button finish;

        public MyGroupViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            cafe = itemView.findViewById(R.id.cafe);
            date = itemView.findViewById(R.id.date);
            location = itemView.findViewById(R.id.location);
            total_member = itemView.findViewById(R.id.total_member);
            people = itemView.findViewById(R.id.people);
            finish = itemView.findViewById(R.id.finish);

            finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onButtonClick(v, position);
                        }
                    }

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, position);
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
