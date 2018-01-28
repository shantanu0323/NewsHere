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

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<List<News>> {

    private static final String TAG = "CategoryActivity";
    private static int NEWS_LOADER_ID = 0;

    private static NewsAdapter newsAdapter;

    String[] countryNames = {"India", "Australia", "Canada", "China", "France", "Germany", "Italy", "Japan", "Worldwide"};
    int flags[] = {R.drawable.india_in, R.drawable.australia_au, R.drawable.canada_ca, R.drawable.china_cn, R.drawable.france_fr,
            R.drawable.germany_de, R.drawable.italy_it, R.drawable.japan_jp, R.drawable.worldwide};

    private Spinner spinner;

    private static Typeface ROBOTO_THIN = null;
    private static Typeface ROBOTO_LIGHT = null;

    private ProgressBar loadingIndicator;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button bPrev, bNext;
    private LinearLayout llPagerDots;
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
    private boolean LOADER_INITIATED = false;

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
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {

            MenuItem mi = m.getItem(i);

            SpannableString s = new SpannableString(mi.getTitle());
            s.setSpan(new CustomTypefaceSpan("", ROBOTO_LIGHT), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mi.setTitle(s);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: " + countryNames[getSpinnerPosition()]);
                initiateLoader();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected: " + countryNames[getSpinnerPosition()]);
            }
        });

        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), flags, countryNames);
        spinner.setAdapter(spinnerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabSelected: " + tab.getText());
                initiateLoader();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initiateLoader() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            loadingIndicator.setVisibility(View.VISIBLE);
            LoaderManager loaderManager = getSupportLoaderManager();
            int tabPos = getTabPosition();
            int spinnerPos = getSpinnerPosition();
            NEWS_LOADER_ID = 9 * tabPos + spinnerPos;
            Log.i(TAG, "initiateLoader: NEWS_LOADER_ID = " + NEWS_LOADER_ID);
            Bundle args = new Bundle();
            args.putInt(TAB_POSITION, tabPos);
            args.putInt(SPINNER_POSITION, spinnerPos);
            LOADER_INITIATED = true;
            loaderManager.initLoader(NEWS_LOADER_ID, args, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "NO INTERNET CONNECTION", Toast.LENGTH_LONG).show();
        }
    }

    private int getItem(int i) {
        int index = viewPager.getCurrentItem() + i;
        if (index == 0) {
            bPrev.setEnabled(false);
            bPrev.setTextColor(Color.GRAY);
        } else if (index == MAXIMUM_PAGE - 1) {
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
            ivArrayDotsPager[i] = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);
            ivArrayDotsPager[i].setLayoutParams(params);
            ivArrayDotsPager[i].setImageResource(R.drawable.unselected_dot);
            ivArrayDotsPager[i].setRotation(13);
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
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        String QUERY_URL;
        if (args.getInt(SPINNER_POSITION) == 8) {
            QUERY_URL = BASE_URL + "&category=" + categories[args.getInt(TAB_POSITION)].toLowerCase();
        } else {
            QUERY_URL = BASE_URL + "&country=" + countryCodes[args.getInt(SPINNER_POSITION)] +
                    "&category=" + categories[args.getInt(TAB_POSITION)].toLowerCase();
        }
        Log.i(TAG, "onCreateLoader: URL SENT " + QUERY_URL);
        return new NewsLoader(this, QUERY_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        Log.i(TAG, "onLoadFinished: CALLED");
        loadingIndicator.setVisibility(View.GONE);

        if (!LOADER_INITIATED) {
            return;
        }
        newsAdapter = new NewsAdapter(getSupportFragmentManager(), newsList);
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

    private int getTabPosition() {
        return tabLayout.getSelectedTabPosition();
    }

    private int getSpinnerPosition() {
        return spinner.getSelectedItemPosition();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
// Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_categories) {
            // Do Nothing ...
        } else if (id == R.id.nav_top_headlines) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search) {
            // Open the Search Activity
            Toast.makeText(getApplicationContext(), "Search Activity is Under Development", Toast.LENGTH_SHORT).show();
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
