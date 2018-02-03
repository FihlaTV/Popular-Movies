package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;

public class DetailActivity extends AppCompatActivity {
    private ImageView movie_poster;
    private TextView movie_name;
    private TextView movie_date;
    private RatingBar movie_rating;
    private TextView movie_synopsis;
    private TextView movie_review_content;
    private TextView movie_review_author;
    private ImageButton movie_trailer_button;
    private CardView movie_review_card;
    private FloatingActionButton favouriteButton;

    private byte[] moviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
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

        movie_review_content = (TextView) findViewById(R.id.review_content);
        if(extras.getString("EXTRA_CURRENT_REVIEW_CONTENT")==null){
            movie_review_content.setText("No reviews found.");
        }
        else{
            movie_review_content.setText("'"+extras.getString("EXTRA_CURRENT_REVIEW_CONTENT")+"'");
        }

        movie_review_author = findViewById(R.id.review_author);
        movie_review_author.setText(extras.getString("EXTRA_CURRENT_REVIEW_AUTHOR"));


        movie_poster = (ImageView) findViewById(R.id.detail_poster);
        Picasso.with(this).load(extras.getString("EXTRA_CURRENT_IMAGE")).resize(600, 0).into(movie_poster);

        movie_trailer_button = findViewById(R.id.trailer_button);
        movie_trailer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = extras.getString("EXTRA_CURRENT_YOUTUBE_URL");

                if(id!=null&&id!="") {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(id));
                    startActivity(webIntent);

                }
                else{
                    Toast.makeText(DetailActivity.this, "No trailer found!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        movie_review_card = findViewById(R.id.review_card);
        movie_review_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String review_URL = extras.getString("EXTRA_CURRENT_REVIEW_URL");

                if(review_URL!=null&&review_URL!="") {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(review_URL));
                    startActivity(webIntent);

                }
                else{
                    Toast.makeText(DetailActivity.this, "No review found!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        favouriteButton = (FloatingActionButton) findViewById(R.id.favourite_fab);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMovie(extras);
            }
        });

    }

    private void saveMovie(Bundle extras){
        String movieName = extras.getString("EXTRA_CURRENT_NAME");
        Picasso.with(this)
                .load(extras.getString("EXTRA_CURRENT_IMAGE"))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        moviePoster = stream.toByteArray();

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
        String movieRating = extras.getString("EXTRA_CURRENT_VOTE");
        String movieTrailer = extras.getString("EXTRA_CURRENT_YOUTUBE_URL");
        String movieReleaseDate = extras.getString("EXTRA_CURRENT_RELEASE");
        String movieSynopsis = extras.getString("EXTRA_CURRENT_RELEASE");
        String movieReviewContent = extras.getString("EXTRA_CURRENT_REVIEW_CONTENT");
        String movieReviewAuthor = extras.getString("EXTRA_CURRENT_REVIEW_AUTHOR");
        String movieReviewLink = extras.getString("EXTRA_CURRENT_REVIEW_URL");

        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, movieName);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, moviePoster);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, movieRating);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TRAILER_LINK, movieTrailer);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movieReleaseDate);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, movieSynopsis);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_CONTENT, movieReviewContent);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_AUTHOR, movieReviewAuthor);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_LINK, movieReviewLink);

        //add if to check if current movie uri is null here...
//        String[] selectionArgs = {movieName};
//        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null, MovieContract.MovieEntry.COLUMN_MOVIE_NAME, selectionArgs, null);
//        if (cursor!=null){
//            Toast.makeText(this, "Insertion failed. Item already added.", Toast.LENGTH_SHORT).show();
//            return;
//        }

        Uri newUri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "Insertion failed.", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, "Inserted item", Toast.LENGTH_SHORT).show();
        }
    }
}