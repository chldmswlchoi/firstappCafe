package com.example.test_retrofit.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DTOChat {

    public DTOChat(String message, String nickname , String date, int view_type,
                   String profile_name) {
        this.message = message;
        this.nickname = nickname;
        this.date = date;
        this.view_type = view_type;
        this.profile_name = profile_name;
    }

    @Override
    public String toString() {
        return "DTOChat{" +
                "id_meeting=" + id_meeting +
                ", sender='" + sender + '\'' +
                ", detail_date='" + detail_date + '\'' +
                ", date='" + date + '\'' +
                ", view_type=" + view_type +
                ", message='" + message + '\'' +
                ", profile_name='" + profile_name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", em_id='" + em_id + '\'' +
                ", id='" + id + '\'' +
                ", removeUserList=" + removeUserList +
                ", exit_id='" + exit_id + '\'' +
                '}';
    }

    public int getId_meeting() {
        return id_meeting;
    }

    public void setId_meeting(int id_meeting) {
        this.id_meeting = id_meeting;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDetail_date() {
        return detail_date;
    }

    public void setDetail_date(String detail_date) {
        this.detail_date = detail_date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getView_type() {
        return view_type;
    }

    public void setView_type(int view_type) {
        this.view_type = view_type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEm_id() {
        return em_id;
    }

    public void setEm_id(String em_id) {
        this.em_id = em_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRemoveUserList() {
        return removeUserList;
    }

    public void setRemoveUserList(int removeUserList) {
        this.removeUserList = removeUserList;
    }

    public String getExit_id() {
        return exit_id;
    }

    public void setExit_id(String exit_id) {
        this.exit_id = exit_id;
    }

    @SerializedName("id_meeting")
    @Expose
    private int id_meeting;

    @SerializedName("sender")
    @Expose
    private String sender;

    @SerializedName("detail_date")
    @Expose
    private String detail_date;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("view_type")
    @Expose
    private int view_type;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("profile")
    @Expose
    private String profile_name;

    @SerializedName("nickname")
    @Expose
    private String nickname;

    @SerializedName("em_id")
    @Expose
    private String em_id;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("removeUserList")
    @Expose
    private int removeUserList;

    @SerializedName("exit_id")
    @Expose
    private String exit_id;

}
