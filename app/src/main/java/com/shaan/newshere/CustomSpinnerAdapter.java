package com.shaan.newshere;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Shaan on 26-01-18.
 */

class CustomSpinnerAdapter extends BaseAdapter{
    Context context;
    int flags[];
    String[] countryNames;
    LayoutInflater inflter;

    public CustomSpinnerAdapter(Context applicationContext, int[] flags, String[] countryNames) {
        this.context = applicationContext;
        this.flags = flags;
        this.countryNames = countryNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return flags.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.country_item, null);
        ImageView icon = (ImageView) view.findViewById(R.id.ivFlag);
        TextView names = (TextView) view.findViewById(R.id.tvCountry);
        icon.setImageResource(flags[i]);
        names.setText(countryNames[i]);
        names.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf"));
        return view;
    }
}
