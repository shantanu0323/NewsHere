package com.shaan.newshere;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Shaan on 26-01-18.
 */

public class Utils {
    public static void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                Typeface currentTypeface = ((TextView) v).getTypeface();
                if (currentTypeface != Typeface.createFromAsset(context.getAssets(), "fonts/roboto_bold.ttf") &&
                        currentTypeface != Typeface.createFromAsset(context.getAssets(), "fonts/roboto_light.ttf") &&
                        currentTypeface != Typeface.createFromAsset(context.getAssets(), "fonts/roboto_medium.ttf") &&
                        currentTypeface != Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf") &&
                        currentTypeface != Typeface.createFromAsset(context.getAssets(), "fonts/roboto_thin.ttf")) {
                    ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/roboto_thin.ttf"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
