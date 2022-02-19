package com.automl.automl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.automl.automl.blocks.Block;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("FieldCanBeLocal")
public class CreateMLModelActivity extends AppCompatActivity {

    public static final String TAG = CreateMLModelActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private ActionBar actionBar;

    private MenuManager menuManager;

    private AccountManager accountManager;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    private SelectMLModelDialog selectMLModelDialog; // This object will manage the ML Model selection.

    private SelectDADialog selectDADialog; // This object will manage the selection of a DA action.

    // Animation resources to make the opening of the floating action buttons nicer.
    private Animation fromButtonAnim;
    private Animation rotateCloseAnim;
    private Animation rotateOpenAnim;
    private Animation toButtonAnim;

    private ExtendedFloatingActionButton fabAddBlock;
    private ExtendedFloatingActionButton fabAddMLModelBlock;
    private ExtendedFloatingActionButton fabAddDataAnalysisBlock;

    private ScrollView scrollView;
    private LinearLayout linearLayout;

    private EditText etFilename;
    private Button btnLoadFile;
    private Button btnUndo;
    private Button btnFinish;

    private boolean clickedFabAddItem = false;

    private FileManager fileManager;
    private HashMap<String, ArrayList<String>> dataset = new HashMap<>(); // This hashmap will store the dataset.

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

        menuManager = new MenuManager(CreateMLModelActivity.this, TAG, navigationView);
        menuManager.switchActivity();
        accountManager = new AccountManager(CreateMLModelActivity.this);
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(CreateMLModelActivity.this);

        fromButtonAnim = AnimationUtils.loadAnimation(CreateMLModelActivity.this, R.anim.from_bottom_anim);
        rotateCloseAnim = AnimationUtils.loadAnimation(CreateMLModelActivity.this, R.anim.rotate_close_anim);
        rotateOpenAnim = AnimationUtils.loadAnimation(CreateMLModelActivity.this, R.anim.rotate_open_anim);
        toButtonAnim = AnimationUtils.loadAnimation(CreateMLModelActivity.this, R.anim.to_bottom_anim);

        scrollView = findViewById(R.id.scrollView);
        linearLayout = findViewById(R.id.linearLayout);

        fabAddBlock = findViewById(R.id.fabAddBlock);
        fabAddMLModelBlock = findViewById(R.id.fabAddMLModelBlock);
        fabAddDataAnalysisBlock = findViewById(R.id.fabAddDataAnalysisBlock);

        etFilename = findViewById(R.id.etFilename);
        btnLoadFile = findViewById(R.id.btnLoadFile);
        btnUndo = findViewById(R.id.btnUndo);
        btnFinish = findViewById(R.id.btnFinish);
        // TODO - load the file from the documents folder or from a url.

        fileManager = new FileManager(dataset);

        selectMLModelDialog = new SelectMLModelDialog(CreateMLModelActivity.this, scrollView, linearLayout, fabAddDataAnalysisBlock);
        selectDADialog = new SelectDADialog(CreateMLModelActivity.this, scrollView, linearLayout, fileManager);

        if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        fabAddBlock.setOnClickListener(view -> {
            onAddItemClicked();
        });

        fabAddMLModelBlock.setOnClickListener(view -> {
            selectMLModelDialog.createSelectMlModelDialog(this.dataset.keySet());
        });

        fabAddDataAnalysisBlock.setOnClickListener(view -> {
            if (dataset.equals(new HashMap<>())) {
                Toast.makeText(CreateMLModelActivity.this, "Please make sure you have inserted a valid url.", Toast.LENGTH_SHORT).show();
                return;
            }
            selectDADialog.createSelectDADialog();
        });

        Log.e("HERE", isNetworkAvailable() + "");

        btnLoadFile.setOnClickListener(view -> {
            User user = firebaseDatabaseHelper.getUser();

            if (user == null) {
                Toast.makeText(CreateMLModelActivity.this, "You must be logged in to use the app.", Toast.LENGTH_SHORT).show();
                return;
            }
            String filename = etFilename.getText().toString();

            if (filename.length() == 0) { // If the user has not inserted a filename.
                Toast.makeText(CreateMLModelActivity.this, "Please enter a filename.", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean isURL = URLUtil.isValidUrl(filename);

            if (isURL) {
                fileManager.execute(filename);
                etFilename.setVisibility(View.GONE);
                btnLoadFile.setVisibility(View.GONE);
            }
        });

        btnUndo.setOnClickListener(v -> { // If the user wants to undo an action.
            if (selectMLModelDialog.getMlModel() != null) { // If a model was created, the user was in the final stage of preprocessing the data which is creating ML Model.
                selectMLModelDialog.undo();
                fabAddDataAnalysisBlock.setClickable(true);
            }
            else // If the user hasn't created ML Model yet then they are in the data analysis stage.
                selectDADialog.undo();
        });

        btnFinish.setOnClickListener(v -> { // When the user has finished the ML Model building process.
            ArrayList<Block> blocks = selectDADialog.getBlocks();
            MLModel mlModel = selectMLModelDialog.getMlModel();

            if (blocks.size() == 0 || mlModel == null) { // If the user has not completed all the required steps.
                Toast.makeText(CreateMLModelActivity.this, "You must first create a pipeline", Toast.LENGTH_LONG).show();
                return;
            }

            if (blocks.isEmpty() || mlModel == null) {
                Toast.makeText(CreateMLModelActivity.this, "Please Make Sure That You Have Completed the ML Model Pipeline", Toast.LENGTH_LONG).show();
                return;
            }

            if (!Python.isStarted())
                Python.start(new AndroidPlatform(CreateMLModelActivity.this));

            Python py = Python.getInstance();
            PyObject pyFile = py.getModule("main");
            PyObject result = pyFile.callAttr("pipeline", fileManager.getColumns(), fileManager.getData(), selectDADialog.getBlocks(),
                    selectMLModelDialog.getMlModel().getType(),
                    selectMLModelDialog.getMlModel().getAttributes(), selectMLModelDialog.getyColumn()); // TODO - check if this works.
            String score = result.toString();

            Toast.makeText(CreateMLModelActivity.this, "The score of the model is: " + score, Toast.LENGTH_LONG).show();

            firebaseDatabaseHelper.addMLModel(firebaseDatabaseHelper.getUser().getUsername(), selectMLModelDialog.getMlModel()); // TODO - make email more efficient.
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_menu_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemMyAccount)
            accountManager.openAccountManagerDialog(firebaseDatabaseHelper.getUser());

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

    /**
     * This function checks if there is an available network.
     * The app needs a network connection to download files from the internet.
     * @return <code>true</code> if there is an available network connection, <code>false</code> otherwise.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}