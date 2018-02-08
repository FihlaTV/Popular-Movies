package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Arjun Vidyarthi on 08-Feb-18.
 */

public class ReviewListAdapter extends ArrayAdapter<TrailersAndReviewsDataClass> {
    public ReviewListAdapter(@NonNull Context context, ArrayList<TrailersAndReviewsDataClass> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View reviewListItemView = convertView;
        if(reviewListItemView==null){
            reviewListItemView = LayoutInflater.from(getContext()).inflate(R.layout.review_item, parent, false);
            }

        final TrailersAndReviewsDataClass currentReview = getItem(position);

        TextView content = reviewListItemView.findViewById(R.id.review_content_extra);
        content.setText("'"+currentReview.getReviewContent()+"'");

        TextView author = reviewListItemView.findViewById(R.id.review_author_extra);
        author.setText(currentReview.getReviewAuthor());

        return reviewListItemView;
    }
}
