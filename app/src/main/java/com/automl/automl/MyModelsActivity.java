package com.automl.automl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MyModelsActivity extends AppCompatActivity {

    public static final String TAG = MyModelsActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private ActionBar actionBar;

    private MenuManager menuManager;
    private AccountManager accountManager;
    private DatabaseManager databaseManager;
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
        menuManager.switchActivity();

        accountManager = new AccountManager(MyModelsActivity.this);

        databaseManager = new DatabaseManager();
        lvMyModels = findViewById(R.id.lvMyModels);

        FirebaseUser user = databaseManager.getUser();

        if (user == null) // If the user is null, there is no data to display.
            Toast.makeText(MyModelsActivity.this, "You must be logged in to view your ML Models. Please sign in using the button at the top of the screen.", Toast.LENGTH_LONG).show();
        else { // Display the data.
            ArrayList<MLModel> models = databaseManager.getMLModels(user.getEmail());

            MyModelsListViewAdapter adapter = new MyModelsListViewAdapter(MyModelsActivity.this, models);
            lvMyModels.setAdapter(adapter);
        }
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