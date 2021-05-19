package com.shaan.newshere;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shaan on 26-01-18.
 */

public class NewsAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "NewsAdapter";

    private static final int MAXIMUM_PAGE = 20;
    private List<News> newsList;

    public NewsAdapter(FragmentManager fm, List<News> newsList) {
        super(fm);
        this.newsList = newsList;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("count", position + 1);
        try {
            Log.d("DEBUG", newsList.toString());
            bundle.putString("author", newsList.get(position).getAuthor());
            bundle.putString("title", newsList.get(position).getTitle());
            bundle.putString("description", newsList.get(position).getDescription());
            bundle.putString("url", newsList.get(position).getUrl());
            bundle.putString("urlToImage", newsList.get(position).getUrlToImage());
            bundle.putString("publishedAt", newsList.get(position).getPublishedAt());
            bundle.putInt("totalResults", newsList.get(position).getTotalResults());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getItem: " + e.getMessage(), e);
        }
        newsFragment.setArguments(bundle);

        return newsFragment;
    }

    @Override
    public int getCount() {
        return MAXIMUM_PAGE;
    }
}
