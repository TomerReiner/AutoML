package com.automl.automl;
// https://raw.githubusercontent.com/codebasics/py/master/pandas/11_melt/weather.csv https://raw.githubusercontent.com/codebasics/py/master/pandas/2_dataframe_basics/weather_data.csv
// https://raw.githubusercontent.com/codebasics/py/master/ML/9_decision_tree/salaries.csv
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.security.Permission;
import java.security.Permissions;

/**
 * TODO - tasks
 * 1) Make signup work - DONE
 * 2) Account Settings - change password, delete data, delete user, block notifications. finish change password and delete user during the weekend - DONE, finish the rest right after finishing task #8
 * 3) DA blocks - DONE
 * 4) Download file and read it. finish until 15.1 DONE
 * 5) ML Models configuration dialogs - finish by 20.1.
 * 6) Add blocks animation. finish until 23.1
 * 7) My models activity. finish by 30.1
 * 8) Add MyModels for each user - partially done. finish by 10.2
 * 9) Add python connection. finish until 10.3
 */

@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private ActionBar actionBar;

    private MenuManager menuManager;
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.dlMainActivity);
        navigationView = findViewById(R.id.nvMainActivity);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        actionBar = getSupportActionBar();

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        actionBar.setDisplayHomeAsUpEnabled(true); // Display the navigation view.

        menuManager = new MenuManager(MainActivity.this, TAG, navigationView);
        menuManager.switchActivity();

        accountManager = new AccountManager(MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_menu_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemMyAccount)
            accountManager.openAccountManagerDialog();

        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}