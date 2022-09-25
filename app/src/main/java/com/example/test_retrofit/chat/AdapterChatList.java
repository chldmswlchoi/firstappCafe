package com.example.test_retrofit.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_retrofit.R;
import com.example.test_retrofit.cafe.AdapterDessertList;

import java.util.ArrayList;
import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.ChatListViewHolder> {

    private final String TAG = this.getClass().getSimpleName();
    List<DTOChat.ChatData> chatList = new ArrayList<>();

    public  AdapterChatList(List<DTOChat.ChatData> chatList)
    {
        this.chatList = chatList;
    }
    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chat_item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list,parent,false);
        ChatListViewHolder holder = new ChatListViewHolder(chat_item);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {

        holder.title.setText(chatList.get(position).getTitle());
        holder.last_chat.setText(chatList.get(position).getLast_chat());
        holder.last_date.setText(chatList.get(position).getLast_date());

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


    public class ChatListViewHolder extends RecyclerView.ViewHolder {
        protected ImageView chat_image;
        protected TextView title,last_chat,last_date;
        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            chat_image = itemView.findViewById(R.id.chat_list);
            title = itemView.findViewById(R.id.title);
            last_chat = itemView.findViewById(R.id.last_chat);
            last_date = itemView.findViewById(R.id.last_date);

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
