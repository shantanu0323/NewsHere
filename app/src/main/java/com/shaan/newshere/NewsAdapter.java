package com.shaan.newshere;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Shaan on 26-01-18.
 */

public class NewsAdapter extends FragmentStatePagerAdapter {

    private static final int MAXIMUM_PAGE = 20;

    public NewsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("count", position + 1);
        newsFragment.setArguments(bundle);

        return newsFragment;
    }

    @Override
    public int getCount() {
        return MAXIMUM_PAGE;
    }
}
