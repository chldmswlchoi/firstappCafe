package com.example.test_retrofit.chat;

public class ItemChat {
    private String content;
    private String nickname;
    private String time;
    private int ViewType;

    public ItemChat(String content, String nickname , String time, int viewType) {
        this.content = content;
        this.nickname = nickname;
        this.time = time;
        this.ViewType = viewType;
    }

    public String getContent() {
        return content;
    }

    public String getNickname() {
        return nickname;
    }

    public String getTime() {
        return time;
    }

    public int getViewType() {
        return ViewType;
    }
}
