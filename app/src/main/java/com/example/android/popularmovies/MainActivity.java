package com.example.android.popularmovies;


import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, android.app.LoaderManager.LoaderCallbacks<Cursor> {

    //Put the API key here
    private String key = "";

    private String LOG_TAG;
    private Toast mToast;
    private MovieAdapter movieAdapter;
    private MovieAdapter movieAdapterForDb;
    private RecyclerView mRecyclerView;
    private String request_url_popular = "http://api.themoviedb.org/3/movie/popular?api_key=" + key;
    private String request_url_top_rated = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + key;
    private TextView Empty;
    private ProgressBar progressBar;
    private Boolean loadFromDB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        movieAdapter = new MovieAdapter(this, this);
        movieAdapterForDb = new MovieAdapter(this, this);
        String requestURL_default = "http://api.themoviedb.org/3/movie/popular?api_key=" + key;
        Empty = (TextView) findViewById(R.id.empty);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        loadMovies(requestURL_default);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int popularID = R.id.action_most_popular;
        int topID = R.id.action_top_rated;
        int favID = R.id.action_favourites;
        int clickedID = item.getItemId();
        if (clickedID == popularID) {
            loadFromDB = false;
            loadMovies(request_url_popular);
            return true;
        }

        if (clickedID == topID) {
            loadFromDB = false;
            loadMovies(request_url_top_rated);
            return true;
        }

        if (clickedID == favID){
            loadFromDB = true;
            getLoaderManager().initLoader(1, null, this);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void loadMovies(String requestURL) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = connMgr.getActiveNetworkInfo();

        if (nInfo == null || !nInfo.isConnected()) {
            mRecyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            Empty.setText("No connection.");
            Empty.setVisibility(View.VISIBLE);
        } else {
            new MovieTask().execute(requestURL);
        }
    }

    @Override
    public void onClick(MovieDataClass thisMovie) {
        if(loadFromDB == false) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = connMgr.getActiveNetworkInfo();

            if (nInfo == null || !nInfo.isConnected()) {
                mToast.makeText(this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Bundle extras = new Bundle();
        extras.putBoolean("LOAD_FROM_DATABASE", loadFromDB);
        extras.putString("EXTRA_CURRENT_NAME", thisMovie.getName());
        if(loadFromDB==false) {
            extras.putString("EXTRA_CURRENT_IMAGE", thisMovie.getImageURL());
        }
        if(loadFromDB==true){
            extras.putByteArray("EXTRA_CURRENT_IMAGE", thisMovie.getMovie_poster_db());
        }
        extras.putString("EXTRA_CURRENT_RELEASE", thisMovie.getDate());
        extras.putString("EXTRA_CURRENT_VOTE", thisMovie.getVoteAvg());
        extras.putString("EXTRA_CURRENT_SYNOPSIS", thisMovie.getSynopsis());
        if(thisMovie.getReviewAuthor()!=null||thisMovie.getReviewContent()!=null||thisMovie.getReviewURL()!=null) {
            extras.putString("EXTRA_CURRENT_REVIEW_AUTHOR", thisMovie.getReviewAuthor());
            extras.putString("EXTRA_CURRENT_REVIEW_CONTENT", thisMovie.getReviewContent());
            extras.putString("EXTRA_CURRENT_REVIEW_URL", thisMovie.getReviewURL());
        }
        else{
            extras.putString("EXTRA_CURRENT_REVIEW_AUTHOR", null);
            extras.putString("EXTRA_CURRENT_REVIEW_CONTENT", null);
            extras.putString("EXTRA_CURRENT_REVIEW_URL", null);
        }
        if(thisMovie.getYoutube_URL()!=null) {
            extras.putString("EXTRA_CURRENT_YOUTUBE_URL", thisMovie.getYoutube_URL());
        }
        else{
            extras.putString("EXTRA_CURRENT_YOUTUBE_URL", null);
        }
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        return new CursorLoader(this,
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<MovieDataClass> moviesFromDB = new ArrayList<>();
        cursor.moveToFirst();
        for(int i = 0; i<cursor.getCount();i++){
            int movieNameColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_NAME);
            int moviePosterColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
            int movieRatingColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RATING);
            int movieTrailerColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TRAILER_LINK);
            int movieReleaseDateColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
            int movieSynopsisColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS);
            int movieReviewContentColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_CONTENT);
            int movieReviewAuthorColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_AUTHOR);
            int movieReviewLinkColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_LINK);

            MovieDataClass movie = new MovieDataClass(
                    cursor.getString(movieNameColumnIndex),
                    cursor.getString(movieReleaseDateColumnIndex),
                    cursor.getString(movieRatingColumnIndex),
                    cursor.getString(movieSynopsisColumnIndex),
                    cursor.getBlob(moviePosterColumnIndex),
                    cursor.getString(movieReviewAuthorColumnIndex),
                    cursor.getString(movieReviewContentColumnIndex),
                    cursor.getString(movieReviewLinkColumnIndex),
                    cursor.getString(movieTrailerColumnIndex)
                   );
            moviesFromDB.add(movie);
            cursor.moveToNext();
        }
        setupDatabaseView(moviesFromDB);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void setupDatabaseView(ArrayList<MovieDataClass> movieListFromDB){
        if(movieListFromDB==null){
            progressBar.setVisibility(View.GONE);
            Empty.setVisibility(View.VISIBLE);
            return;
        }
        movieAdapterForDb.setImageBitmaps(movieListFromDB);
        mRecyclerView.setAdapter(movieAdapterForDb);
        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

    }

    private class MovieTask extends AsyncTask<String, Void, ArrayList<MovieDataClass>> {
        private Boolean running = true;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            Empty.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<MovieDataClass> doInBackground(String... strings) {
                if (strings.length == 0) {
                    return null;
                }

                String URL = strings[0];

                try {
                    return NetworkUtils.networkReq(URL, key);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Null returned as MovieDataClasses' ArrayList");
                    e.printStackTrace();
                    return null;
                }

        }

        @Override
        protected void onPostExecute(ArrayList<MovieDataClass> movieDataClasses) {

            if (movieDataClasses != null) {
                movieAdapter.setImageURLs(movieDataClasses);
                mRecyclerView.setAdapter(movieAdapter);
                progressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                return;
            }
        }
    }

}
