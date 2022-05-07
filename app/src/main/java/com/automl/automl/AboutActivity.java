package com.automl.automl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class AboutActivity extends AppCompatActivity {

    public static final String TAG = AboutActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private ActionBar actionBar;

    private MenuManager menuManager;
    private AccountManager accountManager;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        drawerLayout = findViewById(R.id.dlAboutActivity);
        navigationView = findViewById(R.id.nvAboutActivity);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        actionBar = getSupportActionBar();

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        actionBar.setDisplayHomeAsUpEnabled(true); // Display the navigation view.

        menuManager = new MenuManager(AboutActivity.this, TAG, navigationView);
        accountManager = new AccountManager(AboutActivity.this);
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(AboutActivity.this);

        menuManager.switchActivity(firebaseDatabaseHelper);
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