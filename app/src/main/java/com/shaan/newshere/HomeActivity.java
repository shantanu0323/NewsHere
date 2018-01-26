package com.shaan.newshere;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private static final String API_KEY = "d98a0731ab834d3cb605d8ac24dd7072";
    private static String COUNTRY_CODE = "in";
    private static final String TOP_HEADLINES_URL = "https://newsapi.org/v2/top-headlines?apiKey=d98a0731ab834d3cb605d8ac24dd7072&country=" + COUNTRY_CODE;
    private static final String SEARCH_URL = "https://newsapi.org/v2/everything?apiKey=d98a0731ab834d3cb605d8ac24dd7072&q=";
    private static final int MAXIMUM_PAGE = 20;

    private static Typeface ROBOTO_THIN = null;
    private static Typeface ROBOTO_LIGHT = null;

    String[] countryNames = {"India", "Australia", "Canada", "China", "France", "Germany", "Italy", "Japan"};
    String[] countryCodes = {"in", "au", "ca", "cn", "fr", "de", "it", "jp"};
    int flags[] = {R.drawable.india_in, R.drawable.australia_au, R.drawable.canada_ca, R.drawable.china_cn, R.drawable.france_fr,
            R.drawable.germany_de, R.drawable.italy_it, R.drawable.japan_jp};

    ViewPager viewPager;
    private ImageView[] ivArrayDotsPager;
    private LinearLayout llPagerDots;

    private Button bPrev, bNext;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        Utils.overrideFonts(this, findViewById(android.R.id.content));

        findViews();
        initFonts();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView tvTitle = (TextView) toolbar.getChildAt(0);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                COUNTRY_CODE = countryCodes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                COUNTRY_CODE = "in";
            }
        });

        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), flags, countryNames);
        spinner.setAdapter(spinnerAdapter);

        NewsAdapter newsAdapter = new NewsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(newsAdapter);

        setupPagerIndidcatorDots();
        ivArrayDotsPager[0].setImageResource(R.drawable.selected_dot);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getItem(0);
                for (int i = 0; i < ivArrayDotsPager.length; i++) {
                    ivArrayDotsPager[i].setImageResource(R.drawable.unselected_dot);
                }
                ivArrayDotsPager[position].setImageResource(R.drawable.selected_dot);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(getItem(-1), true);
            }
        });

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(getItem(+1), true);
            }
        });

    }

    private int getItem(int i) {
        int index = viewPager.getCurrentItem() + i;
        if (index == 0) {
            bPrev.setEnabled(false);
            bPrev.setTextColor(Color.GRAY);
        } else if (index == MAXIMUM_PAGE-1) {
            bNext.setEnabled(false);
            bNext.setTextColor(Color.GRAY);
        } else {
            bPrev.setEnabled(true);
            bPrev.setTextColor(getResources().getColor(R.color.colorAccent));
            bNext.setEnabled(true);
            bNext.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        return index;
    }

    private void setupPagerIndidcatorDots() {
        ivArrayDotsPager = new ImageView[20];
        for (int i = 0; i < ivArrayDotsPager.length; i++) {
            ivArrayDotsPager[i] = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);
            ivArrayDotsPager[i].setLayoutParams(params);
            ivArrayDotsPager[i].setImageResource(R.drawable.unselected_dot);
            //ivArrayDotsPager[i].setAlpha(0.4f);
            ivArrayDotsPager[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setAlpha(1);
                }
            });
            llPagerDots.addView(ivArrayDotsPager[i]);
            llPagerDots.bringToFront();
            getItem(0);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void findViews() {
        spinner = (Spinner) findViewById(R.id.spinner);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        llPagerDots = (LinearLayout) findViewById(R.id.pager_dots);
        bPrev = (Button) findViewById(R.id.bPrev);
        bNext = (Button) findViewById(R.id.bNext);
    }

    private void initFonts() {
        ROBOTO_LIGHT = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto_light.ttf");
        ROBOTO_THIN = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto_thin.ttf");
    }
}
