package com.shaan.newshere;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class CategoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "CategoryActivity";

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager rootViewPager;

    String[] countryNames = {"India", "Australia", "Canada", "China", "France", "Germany", "Italy", "Japan"};
    int flags[] = {R.drawable.india_in, R.drawable.australia_au, R.drawable.canada_ca, R.drawable.china_cn, R.drawable.france_fr,
            R.drawable.germany_de, R.drawable.italy_it, R.drawable.japan_jp};

    private static Spinner spinner;

    private static Typeface ROBOTO_THIN = null;
    private static Typeface ROBOTO_LIGHT = null;

    private ProgressBar loadingIndicator;
    private static TabLayout tabLayout;

    private static String BASE_URL = "https://newsapi.org/v2/top-headlines?apiKey=d98a0731ab834d3cb605d8ac24dd7072";
    public static String[] countryCodes = {"in", "au", "ca", "cn", "fr", "de", "it", "jp"};
    public static String[] categories = {"Business", "Entertainment", "Politics", "General", "Health", "Science", "Sports", "Technology"};

    private static final String POSITION = "position";
    private static final String TAB_POSITION = "spinner_pos";

    private static View fragmentView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        findViews();
        initFonts();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
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

                getSupportFragmentManager().beginTransaction().replace(fragmentView.getId(),
                        PlaceholderFragment.newInstance(position, spinner.getSelectedItemPosition())).commit();

//                previousCountryCode = COUNTRY_CODE;
//                COUNTRY_CODE = countryCodes[position];
//                if (TOP_HEADLINES_URL.endsWith("=")) {
//                    TOP_HEADLINES_URL += COUNTRY_CODE;
//                } else {
//                    TOP_HEADLINES_URL = TOP_HEADLINES_URL.substring(0,TOP_HEADLINES_URL.length()-2) + COUNTRY_CODE;
//                }
//                Log.i(TAG, "onItemSelected: CALLED : " + TOP_HEADLINES_URL);
//                initiateLoader();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                previousCountryCode = COUNTRY_CODE;
//                COUNTRY_CODE = "in";
//                if (TOP_HEADLINES_URL.endsWith("=")) {
//                    TOP_HEADLINES_URL += COUNTRY_CODE;
//                } else {
//                    TOP_HEADLINES_URL = TOP_HEADLINES_URL.substring(0,TOP_HEADLINES_URL.length()-2) + COUNTRY_CODE;
//                }
//                Log.i(TAG, "onNothingSelected: CALLED : " + TOP_HEADLINES_URL);
//                initiateLoader();
            }
        });

        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), flags, countryNames);
        spinner.setAdapter(spinnerAdapter);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        rootViewPager.setOffscreenPageLimit(0);
        rootViewPager.setAdapter(mSectionsPagerAdapter);

        // Disabling the scroll feature
        rootViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        rootViewPager.setOffscreenPageLimit(0);
        rootViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(rootViewPager));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabSelected: " + tab.getText());
                if (tab.getText().equals(categories[0])) {
                    getSupportFragmentManager().beginTransaction().replace(fragmentView.getId(),
                            PlaceholderFragment.newInstance(0, spinner.getSelectedItemPosition())).commit();
                }
                if (tab.getText().equals(categories[7])) {
                    getSupportFragmentManager().beginTransaction().replace(fragmentView.getId(),
                            PlaceholderFragment.newInstance(7, spinner.getSelectedItemPosition())).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public static class PlaceholderFragment extends Fragment {

        private static String CATEGORY_URL = "https://newsapi.org/v2/top-headlines?apiKey=d98a0731ab834d3cb605d8ac24dd7072";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int position, int spinnerItemPosition) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(POSITION, position);
            args.putInt(TAB_POSITION, spinnerItemPosition);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_category, container, false);
            fragmentView = rootView;
            int position = getArguments().getInt(POSITION);
            final String message = "onCreateView: CALLED : COUNTRY = " + countryCodes[getSpinnerPosition()] +
                    "-" + getSpinnerPosition() + " : CATEGORY = " + categories[getTabPosition()] +
                    "-" + getTabPosition();
            String QUERY_URL = BASE_URL + "&country=" + countryCodes[getSpinnerPosition()] +
                    "&category=" + categories[getTabPosition()].toLowerCase();
            Log.d(TAG, QUERY_URL);
            ((TextView) rootView.findViewById(R.id.textview)).setText(message);

            return rootView;
        }

    }

    private static int getTabPosition() {
        return tabLayout.getSelectedTabPosition();
    }

    private static int getSpinnerPosition() {
        return spinner.getSelectedItemPosition();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            PlaceholderFragment fragment = new PlaceholderFragment();//newInstance(position, spinner.getSelectedItemPosition());
            Bundle args = new Bundle();
            args.putInt(POSITION, position);
            args.putInt(TAB_POSITION, getTabPosition());
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 8;
        }
    }

//    public static String generateURL() {
//        String url;
//        url = BASE_URL + "&country=" + countryCodes[spinner.getSelectedItemPosition()] +
//                "&category=" + categories[tabLayout.getSelectedTabPosition()];
//        Log.i(TAG, "generateURL: URL GENERATED : " + url);
//        return url;
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
// Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_categories) {
            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
            startActivity(intent);
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
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        rootViewPager = (ViewPager) findViewById(R.id.container);
    }

    private void initFonts() {
        ROBOTO_LIGHT = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto_light.ttf");
        ROBOTO_THIN = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto_thin.ttf");
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

}
