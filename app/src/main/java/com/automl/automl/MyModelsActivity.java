package com.automl.automl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MyModelsActivity extends AppCompatActivity {

    public static final String TAG = MyModelsActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private ActionBar actionBar;

    private MenuManager menuManager;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private SQLiteDatabaseHelper sqLiteDatabaseHelper;

    private TextView tvLoginAlert;
    private ListView lvMyModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_models);

        drawerLayout = findViewById(R.id.dlMyModelsActivity);
        navigationView = findViewById(R.id.nvMyModelsActivity);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        actionBar = getSupportActionBar();

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        actionBar.setDisplayHomeAsUpEnabled(true); // Display the navigation view.

        menuManager = new MenuManager(MyModelsActivity.this, TAG, navigationView);

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(MyModelsActivity.this);
        menuManager.switchActivity(firebaseDatabaseHelper);
        ArrayList<MLModelDisplay> models = (ArrayList<MLModelDisplay>) getIntent().getSerializableExtra("models");

        sqLiteDatabaseHelper = new SQLiteDatabaseHelper(MyModelsActivity.this);

        tvLoginAlert = findViewById(R.id.tvLoginAlert);
        lvMyModels = findViewById(R.id.lvMyModels);

        User user = sqLiteDatabaseHelper.getUser();

        if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) // Request Permission to send SMS.
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);

        if (user == null) { // If the user is null, there is no data to display.
            tvLoginAlert.setVisibility(View.VISIBLE);
            lvMyModels.setVisibility(View.GONE);
        }
        else { // Display the data.
            MyModelsListViewAdapter adapter = new MyModelsListViewAdapter(MyModelsActivity.this, models);
            lvMyModels.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}