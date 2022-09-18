package com.example.test_retrofit.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_retrofit.R;

import java.util.ArrayList;

public class AdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemChat> chatList = null;


    AdapterChat(ArrayList<ItemChat> chatList)
    {
        this.chatList = chatList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == ViewType.CENTER_JOIN)
        {
            view = inflater.inflate(R.layout.item_center_join,parent,false);
            return new CenterViewHolder(view);
        }
        else if(viewType == ViewType.LEFT_CHAT)
        {
            view = inflater.inflate(R.layout.item_left_chat, parent, false);
            return new LeftViewHolder(view);
        }
        else
        {
            view = inflater.inflate(R.layout.item_right_chat, parent, false);
            return new RightViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder.getItemViewType() == ViewType.CENTER_JOIN)
        {
            ((CenterViewHolder) holder).content.setText(chatList.get(position).getContent());
        }
        else if(holder instanceof LeftViewHolder)
        {
            ((LeftViewHolder) holder).name.setText(chatList.get(position).getNickname());
            ((LeftViewHolder) holder).content.setText(chatList.get(position).getContent());
            ((LeftViewHolder) holder).time.setText(chatList.get(position).getTime());
        }
        else
        {
            ((RightViewHolder) holder).content.setText(chatList.get(position).getContent());
            ((RightViewHolder) holder).time.setText(chatList.get(position).getTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getViewType();
    }

    public class CenterViewHolder extends RecyclerView.ViewHolder{
        TextView content;

        CenterViewHolder(View itemView)
        {
            super(itemView);

            content = itemView.findViewById(R.id.content);
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView name;
        TextView time;

        LeftViewHolder(View itemView)
        {
            super(itemView);

            content = itemView.findViewById(R.id.content);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView time;

        RightViewHolder(View itemView)
        {
            super(itemView);

            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
        }
    }
}
