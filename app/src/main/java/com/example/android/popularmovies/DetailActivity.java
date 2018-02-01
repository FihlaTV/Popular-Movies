package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private ImageView movie_poster;
    private TextView movie_name;
    private TextView movie_date;
    private RatingBar movie_rating;
    private TextView movie_synopsis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        super.setTitle("Details about " + extras.getString("EXTRA_CURRENT_NAME"));

        movie_name = (TextView) findViewById(R.id.detail_title);
        movie_name.setText(extras.getString("EXTRA_CURRENT_NAME"));

        movie_date = (TextView) findViewById(R.id.detail_date);
        movie_date.setText("Released on " + extras.getString("EXTRA_CURRENT_RELEASE"));

        movie_synopsis = (TextView) findViewById(R.id.detail_synopsis);
        movie_synopsis.setText(extras.getString("EXTRA_CURRENT_SYNOPSIS"));

        String string_rating = extras.getString("EXTRA_CURRENT_VOTE");

        float rounded_rating = Float.parseFloat(string_rating) / 2;

        movie_rating = (RatingBar) findViewById(R.id.ratingBar);
        movie_rating.setRating(rounded_rating);

        movie_poster = (ImageView) findViewById(R.id.detail_poster);
        Picasso.with(this).load(extras.getString("EXTRA_CURRENT_IMAGE")).resize(600, 0).into(movie_poster);

    }
}