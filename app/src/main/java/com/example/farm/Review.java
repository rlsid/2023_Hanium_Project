package com.example.farm;

import java.io.Serializable;

public class Review implements Serializable {

    private String fruit_name;
    private String review_time;
    private String user_id;
    private String content;
    private String flavor;
    private String review_id;
    private String good; //good변수는 좋아요를 본인이 누른 경우 true or false가 반환되어 온다.

    // 이미지 이름은 DB로부터 게시물 ID와 user_id, fruit_name을 합쳐서 할 예정
    // 요청 Review객체
//    public Review(String fruit_name, String review_time, String user_id, String content, String flavor) {
//        this.fruit_name = fruit_name;
//        this.review_time = review_time;
//        this.user_id = user_id;
//        this.content = content;
//        this.flavor = flavor;
//    }

    // 서버로부터 받을 Review객체
    public Review(String fruit_name, String review_time, String user_id, String content, String flavor, String review_id, String good) {
        this.fruit_name = fruit_name;
        this.review_time = review_time;
        this.user_id = user_id;
        this.content = content;
        this.flavor = flavor;
        this.review_id = review_id;
        this.good = good;
    }

    public Review(){}

    public String getReview_id() {
        return review_id;
    }

    public String getFruit_name() {
        return fruit_name;
    }

    public String getReview_time() {
        return review_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getContent() {
        return content;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFruit_name(String fruit_name) {
        this.fruit_name = fruit_name;
    }

    public void setReview_time(String review_time) {
        this.review_time = review_time;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public String getGood(){return good;}

}
