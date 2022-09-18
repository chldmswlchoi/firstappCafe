package com.example.test_retrofit.group.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NSDTO {

    @SerializedName("total")
    //Json 으로 직렬화 또는 역질렬화 할 떄 사용되는 이름
    @Expose
    // 해당 값이 null 일 경우, json으로 만들 필드들을 자동 생략해준다.
    private Integer total;



    @SerializedName("items")
    @Expose
    private List<NSItems> items = null;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<NSItems> getItems() {
        return items;
    }

    public void setItems(List<NSItems> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "NSDTO{" +
                "total=" + total +
                ", items=" + items +
                '}';
    }

    public class NSItems {


        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("roadAddress")
        @Expose
        private String roadAddress;

        @SerializedName("mapx")
        @Expose
        private Integer mapx;

        @SerializedName("mapy")
        @Expose
        private Integer mapy;




        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRoadAddress() {
            return roadAddress;
        }

        public void setRoadAddress(String roadAddress) {
            this.roadAddress = roadAddress;
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

        @Override
        public String toString() {
            return "{" +
                    "title='" + title + '\'' +
                    ", roadAddress='" + roadAddress + '\'' +
                    ", mapx=" + mapx +
                    ", mapy=" + mapy +
                    '}';
        }
    }
}
