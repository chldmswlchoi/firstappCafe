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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();
    private ArrayList<DTOChat> chatList = null;


    AdapterChat(ArrayList<DTOChat> chatList)
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
        else if(viewType == ViewType.RIGHT_CHAT)
        {
            view = inflater.inflate(R.layout.item_right_chat, parent, false);
            return new RightViewHolder(view);
        }

        else if (viewType == ViewType.CENTER_TIME) {
            view = inflater.inflate(R.layout.item_center_time, parent, false);
            return new CenterTimeHolder(view);
        }
        else if (viewType == ViewType.LEFT_IMAGE){
            view = inflater.inflate(R.layout.item_left_image, parent, false);
            return new LeftImageHolder(view);
        }

        else {
            view = inflater.inflate(R.layout.item_right_image, parent, false);
            return new RightImageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.e(TAG,"onBindViewHolder");
        if(holder.getItemViewType() == ViewType.CENTER_JOIN)
        {
            Log.e(TAG,"ViewType Center");
            ((CenterViewHolder) holder).content.setText(chatList.get(position).getMessage());
        }
        else if(holder.getItemViewType() == ViewType.LEFT_CHAT)
        {
            Log.e(TAG,"ViewType Left");
            Glide.with(((LeftViewHolder) holder).profile.getContext()).
                    load("http://3.39.153.170/test/upload/profile/"+chatList.get(position).getProfile_name()).
                    transform(new CenterCrop(), new RoundedCorners(70)).
                    into(((LeftViewHolder) holder).profile);
            ((LeftViewHolder) holder).name.setText(chatList.get(position).getNickname());
            ((LeftViewHolder) holder).content.setText(chatList.get(position).getMessage());
            ((LeftViewHolder) holder).time.setText(chatList.get(position).getDate());
        }
        else if (holder.getItemViewType() == ViewType.RIGHT_CHAT)
        {
            Log.e(TAG,"ViewType Right");
            ((RightViewHolder) holder).content.setText(chatList.get(position).getMessage());
            ((RightViewHolder) holder).time.setText(chatList.get(position).getDate());
        }

        else if (holder.getItemViewType() == ViewType.CENTER_TIME){
            Log.e(TAG,"ViewType Center_Time");
            ((CenterTimeHolder) holder).content.setText(chatList.get(position).getMessage());
        }

        else if (holder.getItemViewType() == ViewType.LEFT_IMAGE)
        {
            Log.e(TAG,"ViewType LEFT_IMAGE");
            Glide.with(((LeftImageHolder) holder).profile.getContext()).
                    load("http://3.39.153.170/test/upload/profile/"+chatList.get(position).getProfile_name()).
                    transform(new CenterCrop(), new RoundedCorners(70)).
                    into(((LeftImageHolder) holder).profile);

            Glide.with(((LeftImageHolder) holder).image.getContext())
                    .load("http://3.39.153.170/test/upload/chat/"+chatList.get(position).getMessage())
                    .error(R.drawable.image)
                    .into((((LeftImageHolder) holder).image));

            ((LeftImageHolder) holder).name.setText(chatList.get(position).getNickname());
            ((LeftImageHolder) holder).time.setText(chatList.get(position).getDate());
        }

        else{
            Log.e(TAG,"ViewType Right_Image");

            Glide.with(((RightImageHolder) holder).image.getContext())
                    .load("http://3.39.153.170/test/upload/chat/"+chatList.get(position).getMessage())
                    .error(R.drawable.image)
                    .into((((RightImageHolder) holder).image));

            ((RightImageHolder) holder).time.setText(chatList.get(position).getDate());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getView_type();
    }
    //여기서 뷰타입 지정해준다!

    public class CenterViewHolder extends RecyclerView.ViewHolder{
        TextView content;

        CenterViewHolder(View itemView)
        {
            super(itemView);
            content = itemView.findViewById(R.id.content);
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder{
        TextView content,name,time;
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

    public class CenterTimeHolder extends RecyclerView.ViewHolder{
        TextView content;

        CenterTimeHolder(View itemView)
        {
            super(itemView);
            content = itemView.findViewById(R.id.content);
        }
    }

    public class LeftImageHolder extends RecyclerView.ViewHolder{
        TextView name,time;
        ImageView profile,image;

        LeftImageHolder(View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            profile = itemView.findViewById(R.id.profile);
            image = itemView.findViewById(R.id.image);
        }
    }

    public class RightImageHolder extends RecyclerView.ViewHolder{
        TextView time;
        ImageView image;

        RightImageHolder(View itemView)
        {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);
        }
    }
}
