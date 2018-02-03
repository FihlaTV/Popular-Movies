package com.example.android.popularmovies;

/**
 * Created by Arjun Vidyarthi on 05-Jan-18.
 */

public class MovieDataClass {
    private String imageURL;
    private String name;
    private String date;
    private String voteAvg;
    private String synopsis;
    private String review_author;
    private String review_content;
    private String review_URL;
    private String youtube_URL;
    private byte[] movie_poster_db;

    public MovieDataClass(String name, String date, String voteAvg, String synopsis, String imageURL, String review_author, String review_content, String review_URL, String youtube_URL ) {
        this.name = name;
        this.date = date;
        this.voteAvg = voteAvg;
        this.synopsis = synopsis;
        this.imageURL = imageURL;
        this.review_author = review_author;
        this.review_content = review_content;
        this.review_URL = review_URL;
        this.youtube_URL = youtube_URL;
    }

    public MovieDataClass(String name, String date, String voteAvg, String synopsis, byte[] movie_poster_db, String review_author, String review_content, String review_URL, String youtube_URL){
        this.name = name;
        this.date = date;
        this.voteAvg = voteAvg;
        this.synopsis = synopsis;
        this.movie_poster_db = movie_poster_db;
        this.review_author = review_author;
        this.review_content = review_content;
        this.review_URL = review_URL;
        this.youtube_URL = youtube_URL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getVoteAvg() {
        return voteAvg;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getReviewAuthor(){ return review_author;}

    public String getReviewContent(){ return review_content;}

    public String getReviewURL(){return review_URL;}

    public String getYoutube_URL(){ return youtube_URL;}

    public byte[] getMovie_poster_db(){ return movie_poster_db;}
}
