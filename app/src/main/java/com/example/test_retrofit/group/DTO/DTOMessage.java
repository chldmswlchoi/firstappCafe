package com.example.test_retrofit.group.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DTOMessage {

    @Expose
    @SerializedName("status")
    private Boolean status;
    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("id_meeting")
    private int id_meeting;

    @Expose
    @SerializedName("title")
    private String title;


    @Override
    public String toString() {
        return "DTOMessage{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", id_meeting=" + id_meeting +
                ", title=" + title +
                '}';
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean success) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
