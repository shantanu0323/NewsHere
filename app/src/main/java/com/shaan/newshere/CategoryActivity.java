package com.shaan.newshere;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class CategoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "CategoryActivity";

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager rootViewPager;
    private static NewsAdapter newsAdapter;

    String[] countryNames = {"India", "Australia", "Canada", "China", "France", "Germany", "Italy", "Japan"};
    int flags[] = {R.drawable.india_in, R.drawable.australia_au, R.drawable.canada_ca, R.drawable.china_cn, R.drawable.france_fr,
            R.drawable.germany_de, R.drawable.italy_it, R.drawable.japan_jp};

    private static Spinner spinner;

    private static Typeface ROBOTO_THIN = null;
    private static Typeface ROBOTO_LIGHT = null;

    private static ProgressBar loadingIndicator;
    private static TabLayout tabLayout;
    private static ViewPager viewPager;
    private static Button bPrev, bNext;
    private static LinearLayout llPagerDots;
    private static ImageView[] ivArrayDotsPager;

    private static String BASE_URL = "https://newsapi.org/v2/top-headlines?apiKey=d98a0731ab834d3cb605d8ac24dd7072";
    public static String[] countryCodes = {"in", "au", "ca", "cn", "fr", "de", "it", "jp"};
    public static String[] categories = {"Business", "Entertainment", "Politics", "General", "Health", "Science", "Sports", "Technology"};
    private static String prevUrl = "blank";

    private static final String POSITION = "position";
    private static final String TAB_POSITION = "tab_position";
    private static final String SPINNER_POSITION = "spinner_position";
    private static final String LOADER_URL_KEY = "loader_url";
    private static final int MAXIMUM_PAGE = 20;

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

    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>> {

        private static String CATEGORY_URL = "https://newsapi.org/v2/top-headlines?apiKey=d98a0731ab834d3cb605d8ac24dd7072";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int position, int spinnerItemPosition) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(TAB_POSITION, position);
            args.putInt(SPINNER_POSITION, spinnerItemPosition);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_category, container, false);
            fragmentView = rootView;
            String QUERY_URL = BASE_URL + "&country=" + countryCodes[getSpinnerPosition()] +
                    "&category=" + categories[getTabPosition()].toLowerCase();
            Log.d(TAG, QUERY_URL);
            viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
            llPagerDots = (LinearLayout) rootView.findViewById(R.id.pager_dots);
            bPrev = (Button) rootView.findViewById(R.id.bPrev);
            bNext = (Button) rootView.findViewById(R.id.bNext);initiateLoader(getTabPosition(), getSpinnerPosition(), QUERY_URL);
            return rootView;
        }

        private void initiateLoader(int tabPosition, int spinnerPosition, String url) {
            // Get a reference to the ConnectivityManager to check state of network connectivity
            ConnectivityManager connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            // Get details on the currently active default data network
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            // If there is a network connection, fetch data
            if (networkInfo != null && networkInfo.isConnected()) {
                // Get a reference to the LoaderManager, in order to interact with loaders.
                loadingIndicator.setVisibility(View.VISIBLE);
                int LOADER_ID = tabPosition + tabPosition * spinnerPosition;
                Bundle args = new Bundle();
                args.putInt(TAB_POSITION, tabPosition);
                args.putInt(SPINNER_POSITION, spinnerPosition);
                args.putString(LOADER_URL_KEY, url);
                android.support.v4.app.LoaderManager loaderManager = getLoaderManager();
                if (!prevUrl.equals(url)) {
                    Log.e(TAG, "initiateLoader: LOADER INITIATED");
                    loaderManager.initLoader(LOADER_ID, args, this);
                    prevUrl = url;
                }
            } else {
                loadingIndicator.setVisibility(View.GONE);
                Toast.makeText(getActivity().getApplicationContext(), "NO INTERNET CONNECTION", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public Loader<List<News>> onCreateLoader(int id, Bundle args) {
            Log.d(TAG, "onCreateLoader: LOADING-URL : " + args.getString(LOADER_URL_KEY));
            return new NewsLoader(getActivity(), args.getString(LOADER_URL_KEY));
        }

        @Override
        public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
            Log.i(TAG, "onLoadFinished: CALLED");
            loadingIndicator.setVisibility(View.GONE);

            newsAdapter = new NewsAdapter(getActivity().getSupportFragmentManager(),newsList);
            Log.i(TAG, "onLoadFinished: LIST SIZE : " + newsList.size());
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
            viewPager.setPageTransformer(false, new FlipPageViewTransformer());

            bPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(getItem(-1), true);
                }
            });
            bPrev.setVisibility(View.VISIBLE);

            bNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(getItem(+1), true);
                }
            });
            bNext.setVisibility(View.VISIBLE);

        }

        @Override
        public void onLoaderReset(Loader<List<News>> loader) {

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
            llPagerDots.removeAllViews();
            ivArrayDotsPager = new ImageView[20];
            for (int i = 0; i < ivArrayDotsPager.length; i++) {
                ivArrayDotsPager[i] = new ImageView(getActivity().getApplicationContext());
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
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        llPagerDots = (LinearLayout) findViewById(R.id.pager_dots);
        bPrev = (Button) findViewById(R.id.bPrev);
        bNext = (Button) findViewById(R.id.bNext);
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
