package com.shaan.newshere;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FrameLayout searchContainer, sortContainer;
    private ImageButton bSearchDone, bSearchCancel, bSortDone, bSortCancel;
    private TextView tvTitle;
    private TextInputLayout etQuery;
    private LinearLayout bSort;
    private RadioGroup sortRadioContainer;
    private RadioButton rbRelevancy, rbPopularity, rbPublishedAt;
    private TextView tvSort;

    int cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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

        findViews();

        bSearchDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = etQuery.getEditText().getText().toString().trim().toLowerCase();
                if (query.length() > 0) {
                    if (isAlpha(query)) {
                        etQuery.setErrorEnabled(false);
                        tvTitle.setText(query);
                        searchContainer.setVisibility(View.GONE);
                    } else {
                        etQuery.setErrorEnabled(true);
                        etQuery.setError("Please enter a valid query");
                    }
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

    public boolean isAlpha(String name) {
        return name.matches("[a-z]+");
    }
}
