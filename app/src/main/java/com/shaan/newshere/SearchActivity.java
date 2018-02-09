package com.shaan.newshere;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Spannable;
import android.text.SpannableString;
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
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DatePickerFragment.OnDatePickedListener {

    private static final String TAG = "SearchActivity";
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

    int cnt = 0;
    private String dateRange = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                String query = etQuery.getEditText().getText().toString().trim().toLowerCase();
                if (query.length() > 0) {
//                    if (isAlpha(query)) {
                    etQuery.setErrorEnabled(false);
                    tvTitle.setText(query);
                    searchContainer.setVisibility(View.GONE);
//                    } else {
//                        etQuery.setErrorEnabled(true);
//                        etQuery.setError("Please enter a valid query");
//                    }
                } else {
                    etQuery.setErrorEnabled(true);
                    etQuery.setError("Query cannot be empty");
                }
            }
        });

        bSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                tvSort.setText(((RadioButton) findViewById(sortRadioContainer.getCheckedRadioButtonId()))
                        .getText().toString());
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
                dFragment.setArguments(bundle);
                dFragment.setCancelable(false);
                dFragment.show(getFragmentManager(), "Date Picker");
            }
        });
    }

    private void setInitialDates() {
        Calendar currentCalendar = Calendar.getInstance();
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
        fromDate = "" + year + "-" + (month + 1) + "-" + day;
        try {
            Date currentDate = new SimpleDateFormat("yyyy-MM-d").parse(fromDate);
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
    }

    private void findViews() {
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        searchContainer = (FrameLayout) findViewById(R.id.searchContainer);
        searchContainer.setVisibility(View.GONE);
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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchContainer.getVisibility() == View.VISIBLE ||
                sortContainer.getVisibility() == View.VISIBLE) {
            searchContainer.setVisibility(View.GONE);
            sortContainer.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_categories) {
            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_top_headlines) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
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

//    public boolean isAlpha(String name) {
//        return name.matches("[a-z]+");
//    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy");
        if (!dateFetched) {
            fromDate = year + "-" + (month + 1) + "-" + day;
            try {
                Date date = new SimpleDateFormat("yyyy-MM-d").parse(fromDate);
                dateRange = formatter.format(date);
                Toast.makeText(getApplicationContext(), fromDate, Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateFetched = true;
            DialogFragment dFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", "To");
            dFragment.setArguments(bundle);
            dFragment.setCancelable(false);
            dFragment.show(getFragmentManager(), "Date Picker");
        } else {
            toDate = "" + year + "-" + (month + 1) + "-" + day;
            try {
                Date date = new SimpleDateFormat("yyyy-MM-d").parse(toDate);
                dateRange += "  -  " + formatter.format(date);
                Toast.makeText(getApplicationContext(), toDate, Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvDateRange.setText(dateRange);
            dateRange = "";
            dateFetched = false;
        }

    }

    private void initFonts() {
        ROBOTO_LIGHT = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto_light.ttf");
        ROBOTO_THIN = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto_thin.ttf");
    }

}
