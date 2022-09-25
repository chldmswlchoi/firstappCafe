package com.example.test_retrofit.chat;

import android.content.Context;
import android.util.Log;
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

import java.util.ArrayList;

public class AdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();
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
        Log.e(TAG,"onBindViewHolder");
        if(holder.getItemViewType() == ViewType.CENTER_JOIN)
        {
            Log.e(TAG,"ViewType Center");
            ((CenterViewHolder) holder).content.setText(chatList.get(position).getContent());
        }
        else if(holder.getItemViewType() == ViewType.LEFT_CHAT)
        {
            Log.e(TAG,"ViewType Left");
            Glide.with(((LeftViewHolder) holder).profile.getContext()).
                    load("http://3.39.153.170/test/upload/profile/"+chatList.get(position).getProfile_name()).
                    transform(new CenterCrop(), new RoundedCorners(70)).
                    into(((LeftViewHolder) holder).profile);
            ((LeftViewHolder) holder).name.setText(chatList.get(position).getNickname());
            ((LeftViewHolder) holder).content.setText(chatList.get(position).getContent());
            ((LeftViewHolder) holder).time.setText(chatList.get(position).getTime());
        }
        else
        {
            Log.e(TAG,"ViewType Right");
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
        ImageView profile;

        LeftViewHolder(View itemView)
        {
            super(itemView);

            content = itemView.findViewById(R.id.content);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            profile = itemView.findViewById(R.id.profile);
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
