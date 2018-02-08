package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Arjun Vidyarthi on 08-Feb-18.
 */

public class TrailerListAdapter extends ArrayAdapter<TrailersAndReviewsDataClass> {
    public TrailerListAdapter(@NonNull Context context, ArrayList<TrailersAndReviewsDataClass> resource) {
        super(context,0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View trailerListItemView = convertView;
        if(trailerListItemView==null){
            trailerListItemView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_item, parent, false);
        }

        final TrailersAndReviewsDataClass currentTrailer = getItem(position);

        TextView title = trailerListItemView.findViewById(R.id.trailer_id);
        title.setText("Trailer "+String.valueOf(position+2));

        ImageButton playButton = trailerListItemView.findViewById(R.id.trailer_button_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(currentTrailer.getTrailerLink()));
                getContext().startActivity(webIntent);
            }
        });

        ImageButton shareButtom = trailerListItemView.findViewById(R.id.share_button_trailer);
        shareButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Movie Trailer");
                    String sAux = "\nWatch this awesome trailer from the popular movies app!\n\n";
                    sAux = sAux + currentTrailer.getTrailerLink();
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    getContext().startActivity(Intent.createChooser(i, "choose one"));
                } catch(Exception e) {
                    Toast.makeText(getContext(), "Error sharing trailer!", Toast.LENGTH_SHORT);
                    return;
                }
            }
        });

        return trailerListItemView;
    }
}
