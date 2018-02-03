package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Arjun Vidyarthi on 02-Feb-18.
 */

public class MovieContract {
    private MovieContract(){

    }

    static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    static final String PATH_MOVIES = "movies";

    public static class MovieEntry implements BaseColumns{

        static final String TABLE_NAME = "movies";
        static final String _ID = BaseColumns._ID;
        static final String COLUMN_MOVIE_NAME = "name";
        static final String COLUMN_MOVIE_POSTER = "poster";
        static final String COLUMN_MOVIE_RATING = "rating";
        static final String COLUMN_MOVIE_TRAILER_LINK = "trailer";
        static final String COLUMN_MOVIE_RELEASE_DATE = "date";
        static final String COLUMN_MOVIE_SYNOPSIS = "synopsis";
        static final String COLUMN_MOVIE_REVIEW_CONTENT = "review";
        static final String COLUMN_MOVIE_REVIEW_AUTHOR = "author";
        static final String COLUMN_MOVIE_REVIEW_LINK = "review link";

        static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIES;

        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
    }
}
