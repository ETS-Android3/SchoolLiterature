package fitisov123.schoolliterature;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static FragmentManager fragmentManager;
    private static FragmentTransaction fragmentTransaction;

    private static String name, surname, grade, user_id;
    private static View header;
    private static Context context;
    public static Toolbar toolbar;
    private static NavigationView navigationView;
    public static InterstitialAd interstitialAd;
    private static DrawerLayout drawer;

    private static HashMap<Integer, Integer> menuItemsMap = new HashMap<>();
    private static Integer currentMenuItemCheckedId = R.id.nav_regular_books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //toolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_nav_menu));

        context = this;

        header = navigationView.getHeaderView(0);

        fragmentManager = getSupportFragmentManager();

        MainActivity.toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.Darker));

        MainActivity.toolbar.setClickable(true);

        updateHeader();

        MobileAds.initialize(context, getString(R.string.admob_app_id));
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"/*getString(R.string.admob_unit_id)*/);
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.startLayout, new BookListFragment());
        fragmentTransaction.addToBackStack(String.valueOf(R.id.nav_regular_books));
        fragmentTransaction.commit();

        menuItemsMap.put(R.id.nav_settings, 0);
        menuItemsMap.put(R.id.nav_regular_books, 1);
        menuItemsMap.put(R.id.nav_info, 2);

        navigationView.getMenu().getItem(menuItemsMap.get(R.id.nav_regular_books)).setChecked(true);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float windowHeight = metrics.heightPixels;
        float windowWidth = metrics.widthPixels;
        float density  = getResources().getDisplayMetrics().density;
        DataStorage.setDensity(density);
        DataStorage.setWindowHeightDP(windowHeight / density);
        DataStorage.setWindowWidthDP(windowWidth / density);
    }

    public static void updateAppBar(String textName, int color) {
        MainActivity.toolbar.setTitle(textName);
        MainActivity.toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.Darker));
        ((AppCompatActivity)context).setSupportActionBar(MainActivity.toolbar);

        MainActivity.toolbar.setBackgroundColor(ContextCompat.getColor(context, color));

        DrawerLayout drawer = (DrawerLayout) ((AppCompatActivity)context).findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        ((AppCompatActivity)context),
                        drawer,
                        MainActivity.toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close
                    );
        toggle.syncState();
    }

    public static void updateAppBar(int color) {
        ((AppCompatActivity)context).setSupportActionBar(MainActivity.toolbar);
        MainActivity.toolbar.setBackgroundColor(ContextCompat.getColor(context, color));

        DrawerLayout drawer = (DrawerLayout) ((AppCompatActivity)context).findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        ((AppCompatActivity)context),
                        drawer,
                        MainActivity.toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close
                    );
        toggle.syncState();
    }

    public static void updateHeader() {
        getProfileData();
        setHeader();
    }

    public static void getProfileData() {
        grade = DataStorage.getGrade();
        name = DataStorage.getName();
        surname = DataStorage.getSurname();
        user_id = DataStorage.getUserId();
    }

    public static void setHeader() {
        TextView grade_tv = (TextView) header.findViewById(R.id.gradeView);
        TextView name_tv = (TextView) header.findViewById(R.id.nameView);

        grade_tv.setText(grade + " класс");
        name_tv.setText(name + " " + surname);
    }

    @Override
    public void onBackPressed() {
        getSupportActionBar().show();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(fragmentManager.getBackStackEntryCount() == 1) {
                return;
            }
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-2);
            String tag = backStackEntry.getName();
            currentMenuItemCheckedId = Integer.valueOf(tag);
            navigationView.getMenu().getItem(menuItemsMap.get(currentMenuItemCheckedId)).setChecked(true);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.text_settings_menu, menu);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == currentMenuItemCheckedId) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            return true;
        }

        currentMenuItemCheckedId = id;
        item.setChecked(true);
        switch (id) {
            case R.id.nav_settings:
                inflateFragment(new SettingsFragment(), R.id.nav_settings);
                break;

            case R.id.nav_regular_books:
                inflateFragment(new BookListFragment(), R.id.nav_regular_books);
                break;

            case R.id.nav_info:
                inflateFragment(new InfoFragment(), R.id.nav_info);
                break;

            case R.id.nav_exit:
                exitProfile();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void exitProfile() {
        CacheManager.clearUserData(context);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("from", "MainActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static void inflateFragment(Fragment fragment, Integer tag) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.startLayout, fragment);
        fragmentTransaction.addToBackStack(String.valueOf(tag));
        fragmentTransaction.commit();
    }

    public static void disallowOpeningMenu() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public static void allowOpeningMenu() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }
}
