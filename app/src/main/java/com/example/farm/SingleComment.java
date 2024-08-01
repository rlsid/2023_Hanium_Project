package com.example.farm;

public class SingleComment {

    private String review_id;
    private String user_id;
    private String comment;
    private String date;

    public SingleComment(){}

    public SingleComment(String user_id, String comment, String date, String review_id){
        this.user_id = user_id;
        this.comment = comment;
        this.date = date;
        this.review_id = review_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public String getReview_id(){return review_id;}
}
