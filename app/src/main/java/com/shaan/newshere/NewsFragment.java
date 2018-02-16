package com.shaan.newshere;


import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluejamesbond.text.DocumentView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";

    TextView textView;
    private Animation animReadMore;

    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        Bundle bundle = getArguments();
        ((TextView) view.findViewById(R.id.title)).setText(bundle.getString("title"));

        String description = bundle.getString("description");
        if (description == null || description.trim().equals("null")) {
            description = "";
        }

        TextView tvDescription = (TextView) view.findViewById(R.id.description);
        tvDescription.setMovementMethod(new ScrollingMovementMethod());
        try {
            tvDescription.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_light.ttf"));
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: TYPEFACE : " + e.getMessage(), e);
        }
        tvDescription.setText(description);

        try {
            String dateStr = bundle.getString("publishedAt");
            dateStr = dateStr.replace("T", " ");
            dateStr = dateStr.substring(0, dateStr.length() - 1);
            SimpleDateFormat toFullDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fullDate = toFullDate.parse(dateStr);
            SimpleDateFormat sdf = new SimpleDateFormat("E d MMM yyyy, hh:mm a");
            String shortTimeStr = sdf.format(fullDate);
            ((TextView) view.findViewById(R.id.publishedAt)).setText(shortTimeStr);
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: DATE FORMAT" + e.getMessage());
        }

        String author = bundle.getString("author");
        if (author == null || author.equals("null"))
            author = "";
        ((TextView) view.findViewById(R.id.author)).setText(author);

        final Intent intent;
        try {
            String url = bundle.getString("url");
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

            final Button bReadMore = (Button) view.findViewById(R.id.readMore);
            bReadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intent);
                }
            });
            animReadMore = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_readmore);
            bReadMore.startAnimation(animReadMore);
            animReadMore.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    bReadMore.startAnimation(animReadMore);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            ((TextView) view.findViewById(R.id.title)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onCreateView: " + e.getMessage(), e);
        }

        String urlToImage = bundle.getString("urlToImage");
        if (urlToImage != null) {
            if (!urlToImage.trim().equals("")) {
                Picasso.with(getActivity().getApplicationContext())
                        .load(urlToImage)
                        .placeholder(R.drawable.sample_news_cover)
                        .into((ImageView) view.findViewById(R.id.ivNewsCover));
            }
        }
        return view;
    }

}