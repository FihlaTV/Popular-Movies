package com.example.android.popularmovies;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Arjun Vidyarthi on 08-Feb-18.
 */

public class NetworkUtilsReviewTrailer {

    private static final String LOG_TAG = "";

    private NetworkUtilsReviewTrailer() {
        //to prevent instantiating this class.
    }

    private static URL convertToURL(String url) {
        URL URL = null;
        try {
            URL = new URL(url);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return URL;
    }

    private static String getFromStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();

    }

    private static String makeHTTPRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = getFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static ArrayList<TrailersAndReviewsDataClass> parseResponse(String JSONResponse, Boolean isTrailer) {

        if (TextUtils.isEmpty(JSONResponse)) {
            return null;
        }

        ArrayList<TrailersAndReviewsDataClass> trailersAndReviewsDataClasses = new ArrayList<>();
        if(isTrailer==true){
            try {
                JSONObject baseJsonResponse = new JSONObject(JSONResponse);
                JSONArray resultsArray = baseJsonResponse.getJSONArray("results");

                for (int i = 1; i < resultsArray.length(); i++) {

                    JSONObject currentTrailer = resultsArray.getJSONObject(i);
                    String youtube_key = currentTrailer.getString("key");
                    String youtube_URL = "https://www.youtube.com/watch?v="+youtube_key;
                    trailersAndReviewsDataClasses.add(new TrailersAndReviewsDataClass(youtube_URL));
                }
            } catch (JSONException e) {
                Log.e("QueryUtils", "Problem parsing the JSON results for extra trailers!", e);
            }
            return trailersAndReviewsDataClasses;
        }

        else{
            try {
                JSONObject baseJsonResponse = new JSONObject(JSONResponse);
                JSONArray resultsArray = baseJsonResponse.getJSONArray("results");

                for (int i = 1; i < resultsArray.length(); i++) {

                    JSONObject currentReview = resultsArray.getJSONObject(i);
                    trailersAndReviewsDataClasses.add(new TrailersAndReviewsDataClass(
                            currentReview.getString("url"),
                            currentReview.getString("author"),
                            currentReview.getString("content")));
                }
            } catch (JSONException e) {
                Log.e("QueryUtils", "Problem parsing the JSON results for extra reviews!", e);
            }
            return trailersAndReviewsDataClasses;
        }

    }

    public static ArrayList<TrailersAndReviewsDataClass> networkReqForTrailers(String id, String apiKey) throws JSONException {

        Log.e(LOG_TAG, "onNetworkReq");
        String urlForTrailers = "http://api.themoviedb.org/3/movie/"+id+"/videos?api_key="+apiKey;
        URL URL = convertToURL(urlForTrailers);

        String jsonResponse = null;

        try {
            jsonResponse = makeHTTPRequest(URL);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        ArrayList<TrailersAndReviewsDataClass> trailerList = parseResponse(jsonResponse, true);

        return trailerList;
    }

    public static ArrayList<TrailersAndReviewsDataClass> networkReqForReviews(String id, String apiKey) throws JSONException {

        Log.e(LOG_TAG, "onNetworkReq");
        String urlForTrailers = "http://api.themoviedb.org/3/movie/"+id+"/reviews?api_key="+apiKey;
        URL URL = convertToURL(urlForTrailers);

        String jsonResponse = null;

        try {
            jsonResponse = makeHTTPRequest(URL);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        ArrayList<TrailersAndReviewsDataClass> reviewList = parseResponse(jsonResponse, false);

        return reviewList;
    }
}
