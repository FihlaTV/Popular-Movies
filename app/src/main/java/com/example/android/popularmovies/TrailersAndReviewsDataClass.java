package com.example.android.popularmovies;

/**
 * Created by Arjun Vidyarthi on 08-Feb-18.
 */

public class TrailersAndReviewsDataClass {
    String trailerLink = null;
    String reviewLink = null;
    String reviewAuthor = null;
    String reviewContent = null;
    public TrailersAndReviewsDataClass(String trailerLink){
        this.trailerLink = trailerLink;

    }

    public  TrailersAndReviewsDataClass(String reviewLink, String reviewAuthor, String reviewContent){
        this.reviewLink = reviewLink;
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
    }

    public String getTrailerLink() {
        return trailerLink;
    }

    public String getReviewLink() {
        return reviewLink;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }
}
