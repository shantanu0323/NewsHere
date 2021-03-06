package com.shaan.newshere;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by Shaan on 26-01-18.
 */

class NewsLoader extends AsyncTaskLoader<List<News>> {

    private static final String TAG = "NewsLoader";
    private String queryUrl;

    public NewsLoader(Context context, String topHeadlinesUrl) {
        super(context);
        queryUrl = topHeadlinesUrl;
//        Log.i(TAG, "NewsLoader: URL = " + queryUrl);
    }

    @Override
    protected void onStartLoading() {
//        Log.i(TAG, "onStartLoading: forceLoad : CALLED");
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
//        Log.i(TAG, "loadInBackground: CALLED");
        if (queryUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of news.
        List<News> newsList = null;
        try {
            newsList = QueryUtils.fetchNewsData(queryUrl);
            Log.d("NEWSLIST", newsList.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "loadInBackground: PROBLEM FETCHING DATA : ", e);
        } catch (NullPointerException e) {
            Log.e("NoData", "newsList is empty");
        }
        return newsList;
    }
}
