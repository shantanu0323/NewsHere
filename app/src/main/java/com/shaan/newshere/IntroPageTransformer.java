package com.shaan.newshere;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class IntroPageTransformer implements ViewPager.PageTransformer {

    private static final String TAG = "IntroPageTransformer";

    @Override
    public void transformPage(View page, float position) {
        // Get the page index from the tag. This makes
        // it possible to know which page index you're
        // currently transforming - and that can be used
        // to make some important performance improvements.
        int pageNo = (int) page.getTag();
        Log.i(TAG, "transformPage: page = " + pageNo + " : position = " + position);

        // Here you can do all kinds of stuff, like get the
        // width of the page and perform calculations based
        // on how far the user has swiped the page.
        int pageWidth = page.getWidth();
        float pageWidthTimesPosition = pageWidth * position;
        float absPosition = Math.abs(position);
        TextView title = (TextView) page.findViewById(R.id.title);
        View description = page.findViewById(R.id.description);
        View image = page.findViewById(R.id.slide_image);

        // Now it's time for the effects
        if (position <= -1.0f || position >= 1.0f) {

            // The page is not visible. This is a good place to stop
            // any potential work / animations you may have running.

        } else if (position == 0.0f) {

            // The page is selected. This is a good time to reset Views
            // after animations as you can't always count on the PageTransformer
            // callbacks to match up perfectly.

        } else {
            // The page is currently being scrolled / swiped. This is
            // a good place to show animations that react to the user's
            // swiping as it provides a good user experience.

            if (pageNo == 0) {
                image.setScaleX(1f - 0.6f * absPosition);
                image.setScaleY(1f - 0.6f * absPosition);
                image.setTranslationY(pageWidthTimesPosition / 2f);
                image.setTranslationX(-pageWidthTimesPosition / 2f);
            } else {


//                page.setAlpha(1.0f - absPosition);
                title.setAlpha(1.0f - absPosition);

                description.setTranslationY(-pageWidthTimesPosition / 2f);
                description.setAlpha(1.0f - absPosition);


                image.setAlpha(1.0f - absPosition);
                image.setTranslationX(-pageWidthTimesPosition * 1.5f);

            }
            // Finally, it can be useful to know the direction
            // of the user's swipe - if we're entering or exiting.
            // This is quite simple:
            if (position < 0) {
                // Create your out animation here
            } else {
                // Create your in animation here
            }
        }
    }

}