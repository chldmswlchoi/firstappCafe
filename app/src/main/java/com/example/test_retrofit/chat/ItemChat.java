package com.example.test_retrofit.chat;

public class ItemChat {
    private String content;
    private String nickname;
    private String time;
    private String profile_name;
    private int ViewType;


    public ItemChat(String content, String nickname , String time, int viewType,
    String profile_name) {
        this.content = content;
        this.nickname = nickname;
        this.time = time;
        this.ViewType = viewType;
        this.profile_name = profile_name;
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

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

}
