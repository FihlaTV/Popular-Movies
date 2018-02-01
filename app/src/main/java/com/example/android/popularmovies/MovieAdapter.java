package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Arjun Vidyarthi on 05-Jan-18.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private String LOG_TAG;
    private ArrayList<String> imageURLs = new ArrayList<>(10);
    private Context activity_context;
    private ArrayList<MovieDataClass> movieList;

    private final MovieAdapterOnClickHandler handler;

    public interface MovieAdapterOnClickHandler {
        void onClick(MovieDataClass thisMovie);
    }

    void setImageURLs(ArrayList<MovieDataClass> movieList) {

        this.movieList = movieList;
        if (movieList == null) {
            Log.e(LOG_TAG, "movieList passes as null in Adapter.");
        } else {
            imageURLs.clear();
            for (int i = 0; i < movieList.size(); i++) {
                imageURLs.add(i, movieList.get(i).getImageURL());
            }
            notifyDataSetChanged();
        }
    }


    public MovieAdapter(Context context, MovieAdapterOnClickHandler handler) {
        this.handler = handler;
        activity_context = context;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        if (imageURLs == null) {
            holder.moviePoster.setImageResource(R.mipmap.ic_launcher);
            //Picasso.with(activity_context).load("http://i.imgur.com/DvpvklR.png").into(holder.moviePoster);
        } else {
            String currentImageURL = imageURLs.get(position);
            Picasso.with(activity_context).load(currentImageURL).into(holder.moviePoster);
        }
    }

    @Override
    public int getItemCount() {
        if (imageURLs == null) {
            return 1;
        }

        return imageURLs.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView moviePoster;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieDataClass dataForThisMovie = movieList.get(adapterPosition);
            handler.onClick(dataForThisMovie);
        }
    }

}
