package com.example.test_retrofit.cafe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DTOReview {

    @Expose
    @SerializedName("status")
    private String status;

    @Expose
    @SerializedName("data")
    private List<Review> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Review> getData() {
        return data;
    }

    public void setData(List<Review> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DTOReview{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }


    public class Review {

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("nickname")
        @Expose
        private String nickname;

        @SerializedName("r_id")
        @Expose
        private Integer review_id;

        @SerializedName("review")
        @Expose
        private String review;

        @SerializedName("profile")
        @Expose
        private String profile;

        @SerializedName("gdate")
        @Expose
        private String gdate;


        @SerializedName("star")
        @Expose
        private float star;

        @SerializedName("id_cafe")
        @Expose
        private String id_cafe;

        @Expose
        @SerializedName("image_array")
        private ArrayList<String> image_array;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public Integer getReview_id() {
            return review_id;
        }

        public void setReview_id(Integer review_id) {
            this.review_id = review_id;
        }

        public String getReview() {
            return review;
        }

        public void setReview(String review) {
            this.review = review;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public String getGdate() {
            return gdate;
        }

        public void setGdate(String gdate) {
            this.gdate = gdate;
        }

        public String getId_cafe() {
            return id_cafe;
        }

        public void setId_cafe(String id_cafe) {
            this.id_cafe = id_cafe;
        }

        public float getStar() {
            return star;
        }

        public void setStar(float star) {
            this.star = star;
        }

        public ArrayList<String> getImage_array() {
            return image_array;
        }

        public void setImage_array(ArrayList<String> image_array) {
            this.image_array = image_array;
        }

        @Override
        public String toString() {
            return "Review{" +
                    "id='" + id + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", review_id=" + review_id +
                    ", review='" + review + '\'' +
                    ", profile='" + profile + '\'' +
                    ", gdate='" + gdate + '\'' +
                    ", star=" + star +
                    ", id_cafe='" + id_cafe + '\'' +
                    ", image_array=" + image_array +
                    '}';
        }
    }
}
