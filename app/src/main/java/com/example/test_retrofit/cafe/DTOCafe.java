package com.example.test_retrofit.cafe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DTOCafe {


    @Expose
    @SerializedName("status")
    private String status;

    @Expose
    @SerializedName("data")
    private List<CafeData> data;

    @Override
    public String toString() {
        return "DTOCafe" +
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

    public List<CafeData> getData() {
        return data;
    }

    public void setData(List<CafeData> data) {
        this.data = data;
    }


    public class CafeData {

        @SerializedName("id_cafe")
        //Json 으로 직렬화 또는 역질렬화 할 떄 사용되는 이름
        @Expose
        // 해당 값이 null 일 경우, json으로 만들 필드들을 자동 생략해준다.
        private Integer id_cafe;

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("cafe")
        @Expose
        private String cafe;

        @SerializedName("road_address")
        @Expose
        private String road_address;

        //    지역 카테고리
        @SerializedName("a_id")
        @Expose
        private Integer a_id;

        @SerializedName("mapx")
        @Expose
        private Integer mapx;

        @SerializedName("mapy")
        @Expose
        private Integer mapy;

        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("phone_number")
        @Expose
        private String phone_number;

        @SerializedName("last_order")
        @Expose
        private Integer last_order;

        @SerializedName("sns_url")
        @Expose
        private String sns_url;

        @SerializedName("d_id")
        @Expose
        private Integer d_id;

        @SerializedName("t_id")
        @Expose
        private Integer t_id;

        @SerializedName("image")
        @Expose
        private String image;

        @SerializedName("is_loved")
        @Expose
        private Integer is_loved;


        @Expose
        @SerializedName("d_name")
        private String d_name;

        @Expose
        @SerializedName("t_name")
        private String t_name;


        @Expose
        @SerializedName("review")
        private String review;

        @Expose
        @SerializedName("image_array")
        private ArrayList<String> image_array;

        @Expose
        @SerializedName("profile")
        private String profile;


        @Expose
        @SerializedName("nickname")
        private String nickname;

        public Integer getId_cafe() {
            return id_cafe;
        }

        public void setId_cafe(Integer id_cafe) {
            this.id_cafe = id_cafe;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCafe() {
            return cafe;
        }

        public void setCafe(String cafe) {
            this.cafe = cafe;
        }

        public String getRoad_address() {
            return road_address;
        }

        public void setRoad_address(String road_address) {
            this.road_address = road_address;
        }

        public Integer getA_id() {
            return a_id;
        }

        public void setA_id(Integer a_id) {
            this.a_id = a_id;
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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public Integer getLast_order() {
            return last_order;
        }

        public void setLast_order(Integer last_order) {
            this.last_order = last_order;
        }

        public String getSns_url() {
            return sns_url;
        }

        public void setSns_url(String sns_url) {
            this.sns_url = sns_url;
        }

        public Integer getD_id() {
            return d_id;
        }

        public void setD_id(Integer d_id) {
            this.d_id = d_id;
        }

        public Integer getT_id() {
            return t_id;
        }

        public void setT_id(Integer t_id) {
            this.t_id = t_id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Integer getIs_loved() {
            return is_loved;
        }

        public void setIs_loved(Integer is_loved) {
            this.is_loved = is_loved;
        }


        public String getD_name() {
            return d_name;
        }

        public void setD_name(String d_name) {
            this.d_name = d_name;
        }

        public String getT_name() {
            return t_name;
        }

        public void setT_name(String t_name) {
            this.t_name = t_name;
        }

        public String getReview() {
            return review;
        }

        public void setReview(String review) {
            this.review = review;
        }

        public ArrayList<String> getImage_array() {
            return image_array;
        }

        public void setImage_array(ArrayList<String> image_array) {
            this.image_array = image_array;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        @Override
        public String toString() {
            return "CafeData{" +
                    "id_cafe=" + id_cafe +
                    ", id='" + id + '\'' +
                    ", cafe='" + cafe + '\'' +
                    ", road_address='" + road_address + '\'' +
                    ", a_id=" + a_id +
                    ", mapx=" + mapx +
                    ", mapy=" + mapy +
                    ", title='" + title + '\'' +
                    ", phone_number='" + phone_number + '\'' +
                    ", last_order=" + last_order +
                    ", sns_url='" + sns_url + '\'' +
                    ", d_id=" + d_id +
                    ", t_id=" + t_id +
                    ", image='" + image + '\'' +
                    ", is_loved=" + is_loved +
                    ", d_name='" + d_name + '\'' +
                    ", t_name='" + t_name + '\'' +
                    ", review='" + review + '\'' +
                    ", image_array=" + image_array +
                    ", profile='" + profile + '\'' +
                    ", nickname='" + nickname + '\'' +
                    '}';
        }
    }
}
