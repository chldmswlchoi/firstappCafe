package com.example.test_retrofit.group.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DTOGroupResponse {

    @Expose
    @SerializedName("status")
    private String status;

    @Expose
    @SerializedName("data")
    private List<GroupDTO> data;

    @Override
    public String toString() {
        return "GroupResponse{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<GroupDTO> getData() {
        return data;
    }

    public void setData(List<GroupDTO> data) {
        this.data = data;
    }



public class GroupDTO {
    @Expose
    @SerializedName("id_meeting")
    private Integer id_meeting;

    @Expose
    @SerializedName("cafe")
    private String cafe;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("gdate")
    private String gdate;


    @Expose
    @SerializedName("gtime")
    private String gtime;


    @Expose
    @SerializedName("people")
    private Integer people;


    @Expose
    @SerializedName("id")
    private String id;




    @Expose
    @SerializedName("profile")
    private String profile;


    @Expose
    @SerializedName("mapx")
    private Integer mapx;

    @Expose
    @SerializedName("mapy")
    private Integer mapy;


    @Expose
    @SerializedName("road_address")
    private String road_address;


    @Expose
    @SerializedName("total_member")
    private Integer total_member;



    @Expose
    @SerializedName("finish")
    private Integer finish;


    public Integer getId_meeting() {
        return id_meeting;
    }

    public void setId_meeting(Integer id_meeting) {
        this.id_meeting = id_meeting;
    }

    public String getCafe() {
        return cafe;
    }

    public void setCafe(String cafe) {
        this.cafe = cafe;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGdate() {
        return gdate;
    }

    public void setGdate(String gdate) {
        this.gdate = gdate;
    }

    public String getRoad_address() {
        return road_address;
    }

    public void setRoad_address(String road_address) {
        this.road_address = road_address;
    }


    public String getGtime() {
        return gtime;
    }

    public void setGtime(String gtime) {
        this.gtime = gtime;
    }

    public Integer getPeople() {
        return people;
    }

    public void setPeople(Integer people) {
        this.people = people;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMapx() {
        return mapx;
    }

    public void setMapx(Integer mapx) {
        this.mapx = mapx;
    }

    public Integer getMapy() {
        return mapy;
    }

    public void setMapy(Integer mapy) {
        this.mapy = mapy;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }


    public Integer getTotal_member() {
        return total_member;
    }

    public void setTotal_member(Integer total_member) {
        this.total_member = total_member;
    }

    public Integer getFinish() {
        return finish;
    }

    public void setFinish(Integer finish) {
        this.finish = finish;
    }

    @Override
    public String toString() {
        return "GroupDTO{" +
                "id_meeting=" + id_meeting +
                ", cafe='" + cafe + '\'' +
                ", title='" + title + '\'' +
                ", gdate='" + gdate + '\'' +
                ", gtime='" + gtime + '\'' +
                ", people=" + people +
                ", id='" + id + '\'' +
                ", profile='" + profile + '\'' +
                ", mapx=" + mapx +
                ", mapy=" + mapy +
                ", road_address='" + road_address + '\'' +
                ", total_member=" + total_member +
                ", finish=" + finish +
                '}';
    }
}
}
