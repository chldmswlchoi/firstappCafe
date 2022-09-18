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


    @Override
    public String toString() {
        return "MessageDTO{" +
                "status=" + status +
                ", message='" + message + '\'' +
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
}
