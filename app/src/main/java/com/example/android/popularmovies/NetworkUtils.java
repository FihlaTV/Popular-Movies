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
 * Created by Arjun Vidyarthi on 06-Jan-18.
 */

public class NetworkUtils {

    private static final String LOG_TAG = "";

    private NetworkUtils() {
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

    private static ArrayList<MovieDataClass> parseResponse(String JSONResponse, String apiKey) {

        if (TextUtils.isEmpty(JSONResponse)) {
            return null;
        }
        ArrayList<MovieDataClass> movieList = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(JSONResponse);
            JSONArray resultsArray = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject currentMovie = resultsArray.getJSONObject(i);

                String title = currentMovie.getString("title");
                String releaseDate = currentMovie.getString("release_date");
                String voteAvg = currentMovie.getString("vote_average");
                String imageURLRaw = currentMovie.getString("poster_path");
                String imageURL = "http://image.tmdb.org/t/p/w342/" + imageURLRaw;
                String synopsis = currentMovie.getString("overview");

                String id  = currentMovie.getString("id");
                String review_JSON_URL = "http://api.themoviedb.org/3/movie/"+id+"/reviews?api_key="+apiKey;
                String trailer_JSON_URL = "http://api.themoviedb.org/3/movie/"+id+"/videos?api_key="+apiKey;
                String author = null, content= null, review_URL= null, youtube_URL = null;

                try {
                    String review_JSON = makeHTTPRequest(convertToURL(review_JSON_URL));
                    JSONObject baseJsonResponse_reviews = new JSONObject(review_JSON);
                    JSONArray reviews_array = baseJsonResponse_reviews.getJSONArray("results");
                    JSONObject review = null;
                    try {
                        review = reviews_array.getJSONObject(0);
                        author = review.getString("author");
                        content = review.getString("content");
                        review_URL = review.getString("url");
                    }catch (JSONException e){
                        Log.e(LOG_TAG, "No review.");
                        author = null;
                        content = null;
                        review_URL = null;
                    }

                    String trailer_JSON = makeHTTPRequest(convertToURL(trailer_JSON_URL));
                    JSONObject baseJsonResponse_trailer = new JSONObject(trailer_JSON);
                    JSONArray trailers_array = baseJsonResponse_trailer.getJSONArray("results");
                    try{
                        JSONObject trailer = trailers_array.getJSONObject(0);
                        String youtube_key = trailer.getString("key");
                        youtube_URL = "https://www.youtube.com/watch?v="+youtube_key;
                    }catch (JSONException e){
                        Log.e(LOG_TAG, "No trailer.");
                        youtube_URL = null;
                    }


                } catch (IOException e) {
                    Log.e(LOG_TAG, "Problem with trailer/reviews.");
                    e.printStackTrace();

                }

                finally {
                    MovieDataClass movie = new MovieDataClass(title, releaseDate, voteAvg, synopsis, imageURL, author, content, review_URL, youtube_URL);
                    movieList.add(movie);
                }
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }

        return movieList;
    }

    public static ArrayList<MovieDataClass> networkReq(String url, String apiKey) throws JSONException {

        Log.e(LOG_TAG, "onNetworkReq");

        URL URL = convertToURL(url);

        String jsonResponse = null;

        try {
            jsonResponse = makeHTTPRequest(URL);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        ArrayList<MovieDataClass> movieList = parseResponse(jsonResponse, apiKey);

        return movieList;
    }
}
