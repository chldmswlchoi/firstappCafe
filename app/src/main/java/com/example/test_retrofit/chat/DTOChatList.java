package com.example.test_retrofit.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DTOChatList {

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ChatData> getData() {
        return data;
    }

    public void setData(List<ChatData> data) {
        this.data = data;
    }

    @Expose
    @SerializedName("status")
    private String status;

    @Expose
    @SerializedName("data")
    private List<ChatData> data;

    @Override
    public String toString() {
        return "DTOChat{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }

    public class ChatData {
        @Override
        public String toString() {
            return "ChatData{" +
                    "id_chat=" + id_chat +
                    ", id_meeting=" + id_meeting +
                    ", title='" + title + '\'' +
                    ", last_chat='" + last_chat + '\'' +
                    ", last_date='" + last_date + '\'' +
                    ", chat_list_image='" + chat_list_image + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }

        public int getId_chat() {
            return id_chat;
        }

        public void setId_chat(int id_chat) {
            this.id_chat = id_chat;
        }

        public int getId_meeting() {
            return id_meeting;
        }

        public void setId_meeting(int id_meeting) {
            this.id_meeting = id_meeting;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLast_chat() {
            return last_chat;
        }

        public void setLast_chat(String last_chat) {
            this.last_chat = last_chat;
        }

        public String getLast_date() {
            return last_date;
        }

        public void setLast_date(String last_date) {
            this.last_date = last_date;
        }

        public String getChat_list_image() {
            return chat_list_image;
        }

        public void setChat_list_image(String chat_list_image) {
            this.chat_list_image = chat_list_image;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @SerializedName("id_chat")
        @Expose
        private int id_chat;

        @SerializedName("id_meeting")
        @Expose
        private int id_meeting;

        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("last_chat")
        @Expose
        private String last_chat;

        @SerializedName("ldate")
        @Expose
        private String last_date;

        @SerializedName("chat_image")
        @Expose
        private String chat_list_image;

        @SerializedName("id")
        @Expose
        private String id; // 모임 생성자

    }
}
