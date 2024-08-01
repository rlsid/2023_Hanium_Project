package com.example.farm;

public class ReviewPath {

    private Review review;
    private String filePath;

    public ReviewPath(){

    }

    public Review getReview() {
        return review;
    }

    public String getFilePath() {
        return filePath;
    }

    public ReviewPath(Review review, String filePath){
        this.review = review;
        this.filePath = filePath;
    }
}
