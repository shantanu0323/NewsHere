package com.shaan.newshere;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.transition.Explode;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DatePickerFragment.OnDatePickedListener, LoaderManager.LoaderCallbacks<List<News>> {

    private static final String TAG = "SearchActivity";
    private static final String URL_KEY = "queryUrl";
    private static Typeface ROBOTO_THIN = null;
    private static Typeface ROBOTO_LIGHT = null;
    private FrameLayout searchContainer, sortContainer;
    private ImageButton bSearchDone, bSearchCancel, bSortDone, bSortCancel;
    private TextView tvTitle;
    private TextInputLayout etQuery;
    private LinearLayout bSort;
    private RadioGroup sortRadioContainer;
    private RadioButton rbRelevancy, rbPopularity, rbPublishedAt;
    private TextView tvSort;
    private ImageButton bFilterDate;
    public String fromDate;
    public String toDate;
    private boolean dateFetched = false;
    private TextView tvPages, tvDateRange;
    private ProgressBar loadingIndicator;

    private static final int NEWS_LOADER_ID = 1;

    int cnt = 0;
    private String dateRange = "";
    private LinearLayout llPagerDots;
    private ImageView[] ivArrayDotsPager;
    ViewPager viewPager;
    private Button bPrev, bNext;
    private static final int MAXIMUM_PAGE = 20;
    private boolean loaderInitiated = false;

    private final String BASE_URL = "https://newsapi.org/v2/everything?apiKey=d98a0731ab834d3cb605d8ac24dd7072&language=en";
    private String queryText = null;
    private int currentPageNo = 1;
    private String sortPreference = "relevancy";
    private Calendar minCal = null;
    private NewsAdapter newsAdapter;
    private LinearLayout llAction;
    private ImageButton bPrevPage, bNextPage;
    private int savedInstanceIndex = 0;

    private ConstraintLayout errorContainer;
    private ImageButton bRefresh;
    private int LAST_LOADER_ID;
    private boolean forceLoad = false;

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
    private ActivityOptionsCompat compat;
    private NewsHere newsHere;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        findViews();
        initFonts();

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

        setInitialDates();

        bSearchDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                String query = etQuery.getEditText().getText().toString().trim().toLowerCase();
                if (query.length() > 0) {
//                    if (isAlpha(queryText)) {
                    etQuery.setErrorEnabled(false);
                    tvTitle.setText(query);
                    queryText = query.toLowerCase();
                    searchContainer.setVisibility(View.GONE);
                    initiateLoader();
                } else {
                    etQuery.setErrorEnabled(true);
                    etQuery.setError("Query cannot be empty");
                }
            }
        });

        bSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                searchContainer.setVisibility(View.GONE);
            }
        });

        bSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sortValue = tvSort.getText().toString();
                if (sortValue.equals("Relevancy")) {
                    rbRelevancy.setChecked(true);
                } else if (sortValue.equals("Popularity")) {
                    rbPopularity.setChecked(true);
                } else {
                    rbPublishedAt.setChecked(true);
                }
                searchContainer.setVisibility(View.GONE);
                sortContainer.setVisibility(View.VISIBLE);
            }
        });

        bSortDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sortValue = ((RadioButton) findViewById(sortRadioContainer.getCheckedRadioButtonId()))
                        .getText().toString();
                tvSort.setText(sortValue);
                if (sortValue.equals("Relevancy")) {
                    sortPreference = "relevancy";
                } else if (sortValue.equals("Popularity")) {
                    sortPreference = "popularity";
                } else {
                    sortPreference = "publishedAt";
                }
                initiateLoader();
                sortContainer.setVisibility(View.GONE);
            }
        });

        bSortCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortContainer.setVisibility(View.GONE);
            }
        });

        searchContainer.setClickable(true);
        sortContainer.setClickable(true);

        bFilterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dFragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", "From");
                bundle.putInt("minYear", minCal.get(Calendar.YEAR));
                bundle.putInt("minMonth", minCal.get(Calendar.MONTH));
                bundle.putInt("minDay", minCal.get(Calendar.DAY_OF_MONTH));
                dFragment.setArguments(bundle);
                dFragment.setCancelable(false);
                dFragment.show(getFragmentManager(), "Date Picker");
            }
        });

        if (currentPageNo == 1) {
            bPrevPage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor
                    (getApplicationContext(), R.color.imageTintColor)));
            bNextPage.setImageTintMode(PorterDuff.Mode.MULTIPLY);
            bPrevPage.setEnabled(false);
        } else {
            bPrevPage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor
                    (getApplicationContext(), android.R.color.white)));
            bNextPage.setImageTintMode(PorterDuff.Mode.MULTIPLY);
            bPrevPage.setEnabled(true);
        }

        bPrevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPageNo > 1) {
                    currentPageNo--;
                    bNextPage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor
                            (getApplicationContext(), android.R.color.white)));
                    bNextPage.setImageTintMode(PorterDuff.Mode.MULTIPLY);
                    initiateLoader();
                    bNextPage.setEnabled(true);
                }
                if (currentPageNo == 1) {
                    bPrevPage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor
                            (getApplicationContext(), R.color.imageTintColor)));
                    bNextPage.setImageTintMode(PorterDuff.Mode.MULTIPLY);
                    bPrevPage.setEnabled(false);
                } else {
                    bPrevPage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor
                            (getApplicationContext(), android.R.color.white)));
                    bNextPage.setImageTintMode(PorterDuff.Mode.MULTIPLY);
                    bPrevPage.setEnabled(true);
                }
            }
        });

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
            loadingIndicator.setVisibility(View.VISIBLE);
            errorContainer.setVisibility(View.GONE);
            LoaderManager loaderManager = getSupportLoaderManager();
            String url = BASE_URL + "&from=" + fromDate;
            url += "&to=" + toDate;
            url += "&page=" + currentPageNo;
            url += "&sortBy=" + sortPreference;
            if (queryText != null) {
                url += "&q=" + queryText;
            }

            Bundle args = new Bundle();
            args.putString(URL_KEY, url);
            if (forceLoad) {
                loaderManager.restartLoader(LAST_LOADER_ID, args, this);
                forceLoad = false;
            } else {
                LAST_LOADER_ID = NEWS_LOADER_ID;
            }
            if (!loaderInitiated) {
                loaderManager.initLoader(NEWS_LOADER_ID, args, this);
                loaderInitiated = true;
            } else {
                loaderManager.restartLoader(NEWS_LOADER_ID, args, this);
            }
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            ((TextView) errorContainer.findViewById(R.id.tvErrorDesc)).setText("There seems to be an issue with you internet connectivity");
            errorContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        String url = args.getString(URL_KEY);
        Log.i(TAG, "onCreateLoader: QUERY_URL : " + url);
        return new NewsLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        Log.i(TAG, "onLoadFinished: CALLED");
        loadingIndicator.setVisibility(View.GONE);

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

        int totalResults = newsList.get(0).getTotalResults();

        final int maxPage = totalResults / MAXIMUM_PAGE + 1;
        String pageText = "Page " + currentPageNo + " of " + maxPage;
        tvPages.setText(pageText);
        llAction.setVisibility(View.VISIBLE);


        bNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPageNo < maxPage) {
                    currentPageNo++;
                    bPrevPage.setEnabled(true);
                    bPrevPage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor
                            (getApplicationContext(), android.R.color.white)));
                    bNextPage.setImageTintMode(PorterDuff.Mode.MULTIPLY);
                    initiateLoader();
                }
                if (currentPageNo == maxPage) {
                    bNextPage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor
                            (getApplicationContext(), R.color.imageTintColor)));
                    bNextPage.setImageTintMode(PorterDuff.Mode.MULTIPLY);
                    bNextPage.setEnabled(false);
                } else {
                    bNextPage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor
                            (getApplicationContext(), android.R.color.white)));
                    bNextPage.setImageTintMode(PorterDuff.Mode.MULTIPLY);
                    bNextPage.setEnabled(true);
                }
            }
        });

    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

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
        lastMinTime = newsHere.getLastMinTime();
        if (interstitialAd.isLoaded() && (index % 7 == 0) && index != 0 && (currentTime - lastMinTime > 1000 * 30)) {
            interstitialAd.show();
            lastMinTime = currentTime;
            newsHere.setLastMinTime(lastMinTime);
        }
        return index;
    }


    private void setInitialDates() {
        Calendar currentCalendar = Calendar.getInstance();
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
        toDate = "" + year + "-" + (month + 1) + "-" + day;
        try {
            Date currentDate = new SimpleDateFormat("yyyy-MM-d").parse(toDate);
            SimpleDateFormat displayFormatter = new SimpleDateFormat("d MMM yyyy");
            dateRange = displayFormatter.format(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long weekBeforeMillis = currentCalendar.getTimeInMillis() - 1000 * 60 * 60 * 24 * 7;
        Date weekBeforeDate = new Date(weekBeforeMillis);
        SimpleDateFormat displayFormatter = new SimpleDateFormat("d MMM yyyy");
        dateRange = displayFormatter.format(weekBeforeDate) + "  -  " + dateRange;
        tvDateRange.setText(dateRange);
        tvDateRange.setVisibility(View.VISIBLE);
        minCal = Calendar.getInstance();
        minCal.setTime(weekBeforeDate);
        fromDate = "" + minCal.get(Calendar.YEAR) + "-" + (minCal.get(Calendar.MONTH) + 1) +
                "-" + minCal.get(Calendar.DAY_OF_MONTH);

        long ninetyDays = 1000 * 3600 * 24 * 90L;
        long minDateMillis = currentCalendar.getTimeInMillis() - ninetyDays;
        Date minDate = new Date(minDateMillis);
        minCal.setTime(minDate);


    }

    private void findViews() {
        newsHere = (NewsHere) getApplication();
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        searchContainer = (FrameLayout) findViewById(R.id.searchContainer);
        searchContainer.setVisibility(View.VISIBLE);
        bSearchDone = (ImageButton) findViewById(R.id.bSearchDone);
        bSearchCancel = (ImageButton) findViewById(R.id.bSearchCancel);
        etQuery = (TextInputLayout) findViewById(R.id.etQuery);

        sortContainer = (FrameLayout) findViewById(R.id.sortContainer);
        sortContainer.setVisibility(View.GONE);
        bSortDone = (ImageButton) findViewById(R.id.bSortDone);
        bSortCancel = (ImageButton) findViewById(R.id.bSortCancel);
        bSort = (LinearLayout) findViewById(R.id.bSort);
        sortRadioContainer = (RadioGroup) findViewById(R.id.sortRadioContainer);
        tvSort = (TextView) findViewById(R.id.tvSort);
        rbRelevancy = (RadioButton) findViewById(R.id.rbRelevancy);
        rbPopularity = (RadioButton) findViewById(R.id.rbPopularity);
        rbPublishedAt = (RadioButton) findViewById(R.id.rbPublishedAt);

        bFilterDate = (ImageButton) findViewById(R.id.bFilterDate);

        tvPages = (TextView) findViewById(R.id.tvPages);
        tvDateRange = (TextView) findViewById(R.id.tvDateRange);
        tvDateRange.setVisibility(View.GONE);

        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        llPagerDots = (LinearLayout) findViewById(R.id.pager_dots);
        bPrev = (Button) findViewById(R.id.bPrev);
        bNext = (Button) findViewById(R.id.bNext);
        llAction = (LinearLayout) findViewById(R.id.ll_action);
        llAction.setVisibility(View.GONE);
        bPrevPage = (ImageButton) findViewById(R.id.bPrevPage);
        bNextPage = (ImageButton) findViewById(R.id.bNextPage);

        errorContainer = (ConstraintLayout) findViewById(R.id.errorContainer);
        bRefresh = (ImageButton) findViewById(R.id.bRefresh);
        adView = (AdView) findViewById(R.id.adView);
        compat = ActivityOptionsCompat.makeSceneTransitionAnimation(SearchActivity.this, null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (sortContainer.getVisibility() == View.VISIBLE) {
            sortContainer.setVisibility(View.GONE);
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
//                            SearchActivity.super.onBackPressed();
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
            startActivity(intent, compat.toBundle());

//
//            if (newsHere.getSourceActivity().equals("category")) {
//                finish();
//                Log.e(TAG, "onNavigationItemSelected: SEARCH finish()");
//            } else {
//                Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent, compat.toBundle());
//                Log.e(TAG, "onNavigationItemSelected: CATEGORY started()");
//            }
            newsHere.setSourceActivity("search");
            newsHere.setTargetActivity("category");

        } else if (id == R.id.nav_top_headlines) {
            newsHere.setSourceActivity("search");
            newsHere.setTargetActivity("home");
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent, compat.toBundle());
//            finish();
//            Log.e(TAG, "onNavigationItemSelected: SEARCH finish()");
        } else if (id == R.id.nav_search) {
            // Do nothing
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            sortContainer.setVisibility(View.GONE);
            searchContainer.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy");
        if (!dateFetched) {
            fromDate = year + "-" + (month + 1) + "-" + day;
            try {
                Date date = new SimpleDateFormat("yyyy-MM-d").parse(fromDate);
                dateRange = formatter.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateFetched = true;
            DialogFragment dFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", "To");
            bundle.putInt("minYear", year);
            bundle.putInt("minMonth", month);
            bundle.putInt("minDay", day);
            dFragment.setArguments(bundle);
            dFragment.setCancelable(false);
            dFragment.show(getFragmentManager(), "Date Picker");
        } else {
            toDate = "" + year + "-" + (month + 1) + "-" + day;
            try {
                Date date = new SimpleDateFormat("yyyy-MM-d").parse(toDate);
                dateRange += "  -  " + formatter.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvDateRange.setText(dateRange);
            dateRange = "";
            dateFetched = false;
            initiateLoader();
        }

    }

    private void initFonts() {
        ROBOTO_LIGHT = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto_light.ttf");
        ROBOTO_THIN = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto_thin.ttf");
    }

    @Override
    protected void onPause() {
        super.onPause();
        savedInstanceIndex = viewPager.getCurrentItem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: shouldExit = " + newsHere.isShouldExit());
        if (newsHere.isShouldExit()) {
            finish();
        }
        calendar = Calendar.getInstance();
        lastMinTime = newsHere.getLastMinTime();
        currentTime = calendar.getTimeInMillis();
        if (!newsHere.isExiting()) {
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
            finish();
        }
//        if (newsHere.getTargetActivity().equals("home")) {
//            finish();
//            Log.e(TAG, "onStart: SEARCH finish" );
//        }
    }


}
