package com.shaan.newshere;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.transition.Explode;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.eftimoff.viewpagertransformers.DrawFromBackTransformer;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<List<News>> {

    private static final String TAG = "HomeActivity";

    private static final String API_KEY = "d98a0731ab834d3cb605d8ac24dd7072";
    private static String COUNTRY_CODE = "in";
    private static String TOP_HEADLINES_URL = "https://newsapi.org/v2/top-headlines?apiKey=d98a0731ab834d3cb605d8ac24dd7072&country=";
    private static final String SEARCH_URL = "https://newsapi.org/v2/everything?apiKey=d98a0731ab834d3cb605d8ac24dd7072&q=";
    private static final int MAXIMUM_PAGE = 20;

    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter newsAdapter;

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
    private LinearLayout loadingIndicator;
    private String previousCountryCode;
    private int savedInstanceIndex = 0;
    private ConstraintLayout errorContainer;
    private ImageButton bRefresh;
    private int LAST_LOADER_ID;
    private boolean forceLoad = false;
    private ActivityOptionsCompat compat;

    private AdView adView;
    // Testing purposes
    private final String BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    // Original
//    private final String BANNER_AD_UNIT_ID = "ca-app-pub-2383503724460446/8403933355";

    private InterstitialAd interstitialAd;
    private boolean showAd = false;
    private final String TEST_DEVICE_ID = "1F5E03F3D435ACF9A110CAEC4896FCEB";
    // Testing purposes
    private final String INTERSTITAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    // Original
    //    private final String INTERSTITAL_AD_UNIT_ID = "ca-app-pub-2383503724460446/8618721628";
    private Calendar calendar;
    private long currentTime, lastMinTime;
    private NewsHere newsHere;
    private boolean skipMethod = false;
    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        setContentView(R.layout.activity_home);

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
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {

            MenuItem mi = m.getItem(i);

            Typeface typeface = ROBOTO_LIGHT;

            SpannableString s = new SpannableString(mi.getTitle());
            s.setSpan(new CustomTypefaceSpan("", ROBOTO_LIGHT), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mi.setTitle(s);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                previousCountryCode = COUNTRY_CODE;
                COUNTRY_CODE = countryCodes[position];
                if (TOP_HEADLINES_URL.endsWith("=")) {
                    TOP_HEADLINES_URL += COUNTRY_CODE;
                } else {
                    TOP_HEADLINES_URL = TOP_HEADLINES_URL.substring(0, TOP_HEADLINES_URL.length() - 2) + COUNTRY_CODE;
                }
                Log.i(TAG, "onItemSelected: CALLED : " + TOP_HEADLINES_URL);
                initiateLoader();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                previousCountryCode = COUNTRY_CODE;
                COUNTRY_CODE = "in";
                if (TOP_HEADLINES_URL.endsWith("=")) {
                    TOP_HEADLINES_URL += COUNTRY_CODE;
                } else {
                    TOP_HEADLINES_URL = TOP_HEADLINES_URL.substring(0, TOP_HEADLINES_URL.length() - 2) + COUNTRY_CODE;
                }
                Log.i(TAG, "onNothingSelected: CALLED : " + TOP_HEADLINES_URL);
                initiateLoader();
            }
        });

        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), flags, countryNames);
        spinner.setAdapter(spinnerAdapter);

        bRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forceLoad = true;
                viewPager.setVisibility(View.GONE);
                bPrev.setVisibility(View.GONE);
                bNext.setVisibility(View.GONE);
                llPagerDots.setVisibility(View.GONE);
                initiateLoader();
            }
        });

        MobileAds.initialize(this, BANNER_AD_UNIT_ID);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(TEST_DEVICE_ID).addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(INTERSTITAL_AD_UNIT_ID);
        interstitialAd.loadAd(new AdRequest.Builder().addTestDevice(TEST_DEVICE_ID).addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().addTestDevice(TEST_DEVICE_ID).addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
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
            avi.show();

            errorContainer.setVisibility(View.GONE);
            LoaderManager loaderManager = getSupportLoaderManager();
            if (forceLoad) {
                loaderManager.restartLoader(LAST_LOADER_ID, null, this);
                forceLoad = false;
            } else {
                LAST_LOADER_ID = NEWS_LOADER_ID;
            }
            if (previousCountryCode.equals(COUNTRY_CODE)) {
                loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            } else {
                savedInstanceIndex = 0;
                loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
            }
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            avi.hide();

            ((TextView) errorContainer.findViewById(R.id.tvErrorDesc)).setText("There seems to be an issue with your internet connectivity");
            errorContainer.setVisibility(View.VISIBLE);
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

        calendar = Calendar.getInstance();
        currentTime = calendar.getTimeInMillis();
        if (interstitialAd.isLoaded() && (index % 7 == 0) && index != 0 && (currentTime - lastMinTime > 1000 * 30)) {
            interstitialAd.show();
            lastMinTime = currentTime;
            newsHere.setLastMinTime(lastMinTime);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            TextView tvTitle = new TextView(this);
            tvTitle.setText("Sure to exit ?");
            tvTitle.setTypeface(ROBOTO_LIGHT);
            tvTitle.setTextColor(Color.WHITE);
            tvTitle.setTextSize(20f);
            tvTitle.setPadding(50, 20, 20, 20);
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialog));
            builder.setCustomTitle(tvTitle)
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                            HomeActivity.super.onBackPressed();
                            newsHere.setShouldExit(true);
                            finish();
                        }
                    })
                    .setNegativeButton("No", null);
            AlertDialog dialog = builder.show();
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            positiveButton.setTextColor(getResources().getColor(R.color.colorAccent2));
            positiveButton.setTypeface(ROBOTO_LIGHT);
            negativeButton.setTextColor(getResources().getColor(R.color.colorAccent2));
            negativeButton.setTypeface(ROBOTO_LIGHT);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_categories) {
            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            newsHere.setSourceActivity("home");
            newsHere.setTargetActivity("category");
            startActivity(intent, compat.toBundle());

        } else if (id == R.id.nav_top_headlines) {
            // Do nothing
        } else if (id == R.id.nav_search) {
            // Open the Search Activity
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            newsHere.setSourceActivity("home");
            newsHere.setTargetActivity("search");
            startActivity(intent, compat.toBundle());
//            finish();
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
            startActivity(intent);
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
        loadingIndicator = (LinearLayout) findViewById(R.id.loading_indicator);
        errorContainer = (ConstraintLayout) findViewById(R.id.errorContainer);
        bRefresh = (ImageButton) findViewById(R.id.bRefresh);
        adView = (AdView) findViewById(R.id.adView);
        compat = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this, null);
        newsHere = (NewsHere) getApplication();
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
    }

    private void initFonts() {
        ROBOTO_LIGHT = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto_light.ttf");
        ROBOTO_THIN = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto_thin.ttf");
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader: CALLED");
        Log.i(TAG, "onCreateLoader: URL SENT " + TOP_HEADLINES_URL);
        return new NewsLoader(this, TOP_HEADLINES_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        Log.i(TAG, "onLoadFinished: CALLED");
        loadingIndicator.setVisibility(View.GONE);
        avi.hide();


//        animatedCircleLoadingView.setVisibility(View.GONE);
//        animatedCircleLoadingView.resetLoading();

        if (newsList == null) {
            ((TextView) errorContainer.findViewById(R.id.tvErrorDesc)).setText("Couldn't reach servers at the moment");
            errorContainer.setVisibility(View.VISIBLE);
        } else {
            errorContainer.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            bPrev.setVisibility(View.VISIBLE);
            bNext.setVisibility(View.VISIBLE);
            llPagerDots.setVisibility(View.VISIBLE);
        }

        newsAdapter = new NewsAdapter(getSupportFragmentManager(), newsList);
        viewPager.setAdapter(newsAdapter);
        viewPager.setCurrentItem(savedInstanceIndex);


        setupPagerIndidcatorDots();
        ivArrayDotsPager[savedInstanceIndex].setImageResource(R.drawable.selected_dot);

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
        viewPager.setPageTransformer(false, new DrawFromBackTransformer());


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

        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(spinner))
                .singleShot(100)
                .blockAllTouches()
                .setContentTitle("Country Specific Headlines")
                .setStyle(R.style.CustomShowcaseTheme2)
                .setContentText("Now see the top headlines of the country you select in their own native language")
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        new ShowcaseView.Builder(HomeActivity.this)
                                .setTarget(new ViewTarget((TextView) findViewById(R.id.dummyReadMore)))
                                .setContentTitle("Read More")
                                .setStyle(R.style.CustomShowcaseTheme2)
                                .singleShot(130)
                                .blockAllTouches()
                                .setContentText("Click to read the full article from the source directly")
                                .setShowcaseEventListener(new OnShowcaseEventListener() {
                                    @Override
                                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                        new ShowcaseView.Builder(HomeActivity.this)
                                                .setTarget(new ViewTarget(llPagerDots))
                                                .setContentTitle("Swipe Through")
                                                .singleShot(160)
                                                .blockAllTouches()
                                                .setStyle(R.style.CustomShowcaseTheme2)
                                                .setContentText("Or use the navigation bar at the bottom to move accross the successive news")
                                                .build();
                                    }

                                    @Override
                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                    }

                                    @Override
                                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                    }

                                    @Override
                                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                                    }
                                })
                                .build();
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                    }
                })
                .build();

    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        savedInstanceIndex = viewPager.getCurrentItem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lastMinTime = newsHere.getLastMinTime();
        calendar = Calendar.getInstance();
        currentTime = calendar.getTimeInMillis();
        Log.e(TAG, "onResume: shouldExit = " + newsHere.isShouldExit() +
                " : isExiting = " + newsHere.isExiting());
        if (!newsHere.isShouldExit() && !newsHere.isExiting()) {
            if (interstitialAd.isLoaded() && (currentTime - lastMinTime > 1000 * 30)) {
                interstitialAd.show();
                lastMinTime = currentTime;
                newsHere.setLastMinTime(lastMinTime);
            }
        } else {
            newsHere.setExiting(false);
        }
    }

    private void setupWindowAnimations() {
        Explode explode = new Explode();
        explode.setDuration(1000);
        explode.setInterpolator(new OvershootInterpolator());
        getWindow().setEnterTransition(explode);
        getWindow().setExitTransition(explode);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: shouldExit = " + newsHere.isShouldExit());

        if (newsHere.isShouldExit()) {
            int instances = newsHere.getInstances() - 1;
            newsHere.setInstances(instances);
            if (instances == 0) {
                newsHere.setShouldExit(false);
                Log.e(TAG, "onStart: EXITING");
                newsHere.setExiting(true);
            }
            finish();
        } else {
            int instances = newsHere.getInstances() + 1;
            newsHere.setInstances(instances);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: DESTROYED");
    }
}