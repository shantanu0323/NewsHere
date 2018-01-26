package com.shaan.newshere;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.graphics.TypefaceCompatUtil;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shaan on 26-01-18.
 */

public class NewsHere extends Application {

    private static final String TAG = "NewsHere";

    @Override
    public void onCreate() {
        super.onCreate();

        overrideFont(getApplicationContext(), "SERIF", "fonts/roboto_light.ttf");
    }

    public void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {

        final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i(TAG, "overrideFont: FONT CHANGE INITIATED");
            Map<String, Typeface> newMap = new HashMap<String, Typeface>();
            newMap.put("serif", customFontTypeface);
            try {
                final Field staticField = Typeface.class
                        .getDeclaredField("sSystemFontMap");
                staticField.setAccessible(true);
                staticField.set(null, newMap);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
                defaultFontTypefaceField.setAccessible(true);
                defaultFontTypefaceField.set(null, customFontTypeface);
            } catch (Exception e) {
                Log.e(TAG, "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
            }
        }
    }

}
