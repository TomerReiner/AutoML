package com.automl.automl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class CreateMLModelActivity extends AppCompatActivity {

    public static final String TAG = "CreateMLModel";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private ActionBar actionBar;

    // Animation resources to make the opening of the floating action buttons nicer.
    private Animation fromButtonAnim;
    private Animation rotateCloseAnim;
    private Animation rotateOpenAnim;
    private Animation toButtonAnim;

    private ExtendedFloatingActionButton fabAddBlock;
    private ExtendedFloatingActionButton fabAddMLModelBlock;
    private ExtendedFloatingActionButton fabAddDataAnalysisBlock;

    private SelectMLModelDialog selectMLModelDialog; // This class manages the ML Model selection.

    private boolean clickedFabAddItem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ml_model);

        drawerLayout = findViewById(R.id.dlCreateMlModel);
        navigationView = findViewById(R.id.nvCreateMLModel);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        actionBar = getSupportActionBar();

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        actionBar.setDisplayHomeAsUpEnabled(true); // Display the navigation view.

        fromButtonAnim = AnimationUtils.loadAnimation(CreateMLModelActivity.this, R.anim.from_bottom_anim);
        rotateCloseAnim = AnimationUtils.loadAnimation(CreateMLModelActivity.this, R.anim.rotate_close_anim);
        rotateOpenAnim = AnimationUtils.loadAnimation(CreateMLModelActivity.this, R.anim.rotate_open_anim);
        toButtonAnim = AnimationUtils.loadAnimation(CreateMLModelActivity.this, R.anim.to_bottom_anim);

        selectMLModelDialog = new SelectMLModelDialog(CreateMLModelActivity.this);

        fabAddBlock = findViewById(R.id.fabAddBlock);
        fabAddMLModelBlock = findViewById(R.id.fabAddMLModelBlock);
        fabAddDataAnalysisBlock = findViewById(R.id.fabAddDataAnalysisBlock);

        fabAddBlock.setOnClickListener(view -> {
            onAddItemClicked();
        });

        fabAddMLModelBlock.setOnClickListener(view ->
                selectMLModelDialog.createSelectMlModelDialog()
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_menu_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    /**
     * This function handles the opening of <code>fabAddMLModelBlock</code> and <code>fabAddDataAnalysisBlock</code>.
     * The function will open these buttons with a nice animation.
     */
    private void onAddItemClicked() {
        if (!clickedFabAddItem) {
            fabAddMLModelBlock.setVisibility(View.VISIBLE);
            fabAddDataAnalysisBlock.setVisibility(View.VISIBLE);

            fabAddDataAnalysisBlock.startAnimation(fromButtonAnim);
            fabAddDataAnalysisBlock.startAnimation(rotateOpenAnim);

            fabAddMLModelBlock.startAnimation(fromButtonAnim);
            fabAddMLModelBlock.startAnimation(rotateOpenAnim);
        }
        else {
            fabAddMLModelBlock.setVisibility(View.INVISIBLE);
            fabAddDataAnalysisBlock.setVisibility(View.INVISIBLE);

            fabAddMLModelBlock.startAnimation(toButtonAnim);
            fabAddMLModelBlock.startAnimation(rotateCloseAnim);

            fabAddDataAnalysisBlock.startAnimation(toButtonAnim);
            fabAddDataAnalysisBlock.startAnimation(rotateCloseAnim);
        }

        clickedFabAddItem = !clickedFabAddItem;
    }
}