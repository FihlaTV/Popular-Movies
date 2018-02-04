package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Arjun Vidyarthi on 02-Feb-18.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " ("+
       MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
       MovieContract.MovieEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL UNIQUE, " +
       MovieContract.MovieEntry.COLUMN_MOVIE_POSTER + " BLOB NOT NULL, " +
       MovieContract.MovieEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL, " +
       MovieContract.MovieEntry.COLUMN_MOVIE_TRAILER_LINK + " TEXT NOT NULL, "+
       MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, "+
       MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
       MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_CONTENT + " TEXT, " +
       MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_AUTHOR + " TEXT, "+
       MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_LINK + " TEXT);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS "+ MovieContract.MovieEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "moviesdatabase.db";

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
