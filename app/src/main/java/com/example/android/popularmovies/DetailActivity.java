package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.example.android.popularmovies.data.MovieProvider.LOG_TAG;

public class DetailActivity extends AppCompatActivity {
    private ImageView movie_poster;
    private TextView movie_name;
    private TextView movie_date;
    private RatingBar movie_rating;
    private TextView movie_synopsis;
    private TextView movie_review_content;
    private TextView movie_review_author;
    private ImageButton movie_trailer_button;
    private ImageButton movie_share_button;
    private CardView movie_review_card;
    private FloatingActionButton favouriteButton;
    private TrailerListAdapter mTrailerAdapter;
    private ReviewListAdapter mReviewAdapter;
    private ListView reviewList;
    private ListView trailerList;

    private byte[] moviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        super.setTitle("Details about " + extras.getString("EXTRA_CURRENT_NAME"));
        Boolean loadingFromDB = extras.getBoolean("LOAD_FROM_DATABASE");

        movie_name = (TextView) findViewById(R.id.detail_title);
        movie_name.setText(extras.getString("EXTRA_CURRENT_NAME"));

        reviewList = findViewById(R.id.list_reviews_extra);
        mReviewAdapter = new ReviewListAdapter(DetailActivity.this, new ArrayList<TrailersAndReviewsDataClass>());

        trailerList = findViewById(R.id.list_trailers_extra);
        mTrailerAdapter = new TrailerListAdapter(DetailActivity.this, new ArrayList<TrailersAndReviewsDataClass>());

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
        if(loadingFromDB==false) {
            Picasso.with(this).load(extras.getString("EXTRA_CURRENT_IMAGE")).into(movie_poster);
            favouriteButton = (FloatingActionButton) findViewById(R.id.favourite_fab);
            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveMovie(extras);
                }
            });
            ReviewTaskClass reviewTask = new ReviewTaskClass();
            reviewTask.execute(extras.getString("EXTRA_MOVIE_ID"));
            TrailerTaskClass trailerTask = new TrailerTaskClass();
            trailerTask.execute(extras.getString("EXTRA_MOVIE_ID"));
        }
        if(loadingFromDB==true){
            byte[] bitmapData = extras.getByteArray("EXTRA_CURRENT_IMAGE");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
            movie_poster.setImageBitmap(bitmap);
            favouriteButton = findViewById(R.id.favourite_fab);
            favouriteButton.setImageResource(R.drawable.ic_delete_black_24dp);
            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                    builder.setMessage("Confirm deletion");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteMovie(extras);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
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

        movie_share_button = findViewById(R.id.share_button);
        movie_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, extras.getString("EXTRA_CURRENT_NAME"));
                    String sAux = "\nWatch this awesome trailer of "+extras.getString("EXTRA_CURRENT_NAME")+" from the popular movies app!\n\n";
                    sAux = sAux + extras.getString("EXTRA_CURRENT_YOUTUBE_URL");
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch(Exception e) {
                    Toast.makeText(DetailActivity.this, "Error sharing trailer!", Toast.LENGTH_SHORT);
                    return;
                }
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

        Uri newUri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "Insertion failed. Already added.", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, "Inserted item", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteMovie(Bundle extras){
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_NAME + "=?";
        String[] selectionArgs = {extras.getString("EXTRA_CURRENT_NAME")};
        getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, selection, selectionArgs);
        finish();
    }

    public class TrailerTaskClass extends AsyncTask<String, Void, ArrayList<TrailersAndReviewsDataClass>>{
        @Override
        protected void onPreExecute() {
            mTrailerAdapter.clear();
        }

        @Override
        protected void onPostExecute(ArrayList<TrailersAndReviewsDataClass> trailersAndReviewsDataClasses) {
            if(trailersAndReviewsDataClasses!=null) {
                mTrailerAdapter.addAll(trailersAndReviewsDataClasses);
                ViewGroup.LayoutParams params = trailerList.getLayoutParams();
                params.height = 150 * trailersAndReviewsDataClasses.size();
                trailerList.setLayoutParams(params);
                trailerList.setAdapter(mTrailerAdapter);
            }
        }

        @Override
        protected ArrayList<TrailersAndReviewsDataClass> doInBackground(String... strings) {
            if (strings.length == 0) {
                return null;
            }
            String id = strings[0];

            try {
                return NetworkUtilsReviewTrailer.networkReqForTrailers(id,MainActivity.key);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Null returned");
                e.printStackTrace();
                return null;
            }
        }
    }

    public class ReviewTaskClass extends AsyncTask<String, Void, ArrayList<TrailersAndReviewsDataClass>>{

        @Override
        protected void onPreExecute() {
            mReviewAdapter.clear();

        }

        @Override
        protected void onPostExecute(final ArrayList<TrailersAndReviewsDataClass> trailersAndReviewsDataClasses) {
            if(trailersAndReviewsDataClasses!=null) {
                mReviewAdapter.addAll(trailersAndReviewsDataClasses);
                ViewGroup.LayoutParams params = reviewList.getLayoutParams();
                params.height = 415 * trailersAndReviewsDataClasses.size();
                reviewList.setLayoutParams(params);
                reviewList.setAdapter(mReviewAdapter);
                reviewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(trailersAndReviewsDataClasses.get(i).getReviewLink()));
                        startActivity(webIntent);
                    }
                });
            }
        }

        @Override
        protected ArrayList<TrailersAndReviewsDataClass> doInBackground(String... strings) {
            if (strings.length == 0) {
                return null;
            }
            String id = strings[0];

            try {
                return NetworkUtilsReviewTrailer.networkReqForReviews(id,MainActivity.key);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Null returned");
                e.printStackTrace();
                return null;
            }
        }
    }
}