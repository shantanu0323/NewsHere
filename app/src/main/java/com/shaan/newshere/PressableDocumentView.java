package com.shaan.newshere;

/*
 * Copyright 2015 Mathew Kurian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * PressableDocumentView.java
 * @author Mathew Kurian
 *
 * From TextJustify-Android Library v2.0
 * https://github.com/bluejamesbond/TextJustify-Android
 *
 * Please report any issues
 * https://github.com/bluejamesbond/TextJustify-Android/issues
 *
 * Date: 1/27/15 3:35 AM
 */

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.bluejamesbond.text.DocumentView;
import com.bluejamesbond.text.hyphen.SqueezeHyphenator;

@SuppressWarnings("unused")
public class PressableDocumentView extends DocumentView {

    public PressableDocumentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPressableDocumentView();
    }

    public PressableDocumentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPressableDocumentView();
    }

    public PressableDocumentView(Context context) {
        super(context);
        initPressableDocumentView();
    }

    public PressableDocumentView(Context context, int type) {
        super(context, type);
        initPressableDocumentView();
    }

    public PressableDocumentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPressableDocumentView();
    }

    private void initPressableDocumentView() {
        getDocumentLayoutParams().setHyphenator(SqueezeHyphenator.getInstance());
        getDocumentLayoutParams().setHyphenated(true);
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        super.dispatchSetPressed(pressed);

        if (pressed) {
            getDocumentLayoutParams().setTextColor(Color.BLACK);
            setDisallowInterceptTouch(true);
        } else {
            getDocumentLayoutParams().setTextColor(Color.WHITE);
            setDisallowInterceptTouch(false);
        }

        invalidate();
    }
}