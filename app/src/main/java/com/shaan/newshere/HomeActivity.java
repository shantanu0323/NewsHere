package com.shaan.newshere;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private static final String API_KEY = "d98a0731ab834d3cb605d8ac24dd7072";
    private static String COUNTRY_CODE = "in";
    private static final String TOP_HEADLINES_URL = "https://newsapi.org/v2/top-headlines?apiKey=d98a0731ab834d3cb605d8ac24dd7072&country=" + COUNTRY_CODE;
    private static final String SEARCH_URL = "https://newsapi.org/v2/everything?apiKey=d98a0731ab834d3cb605d8ac24dd7072&q=";

    private static Typeface ROBOTO_THIN = null;
    private static Typeface ROBOTO_LIGHT = null;

    String[] countryNames = {"India", "Australia", "Canada", "China", "France", "Germany", "Italy", "Japan"};
    String[] countryCodes = {"in", "au", "ca", "cn", "fr", "de", "it", "jp"};
    int flags[] = {R.drawable.india_in, R.drawable.australia_au, R.drawable.canada_ca, R.drawable.china_cn, R.drawable.france_fr,
            R.drawable.germany_de, R.drawable.italy_it, R.drawable.japan_jp};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        Utils.overrideFonts(this, findViewById(android.R.id.content));

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

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
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

        TextView tvName = (TextView) findViewById(R.id.name);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto_light.ttf");
//        tvName.setTypeface(font);

        TextView tvName2 = (TextView) findViewById(R.id.name2);
        Typeface font2 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/roboto_thin.ttf");
//        tvName2.setTypeface(font2);
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
}
