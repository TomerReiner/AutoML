package com.automl.automl;

import android.content.Context;
import android.content.Intent;

import com.google.android.material.navigation.NavigationView;

/**
 * This class manages the movement between activities in the app.
 */
public class MenuManager {

    private final Context context;
    private final String tag; // The name of the activity.
    private final NavigationView navigationView;

    public MenuManager(Context context, String tag, NavigationView navigationView) {
        this.context = context;
        this.tag = tag;
        this.navigationView = navigationView;
    }

    /**
     * This function returns the activity name the tag is related to.
     * @param id The id of the tag.
     * @return The activity name.
     */
    private String getActivityNameForItemId(int id) {
        if (id == R.id.itemTutorial)
            return MainActivity.TAG;
        if (id == R.id.itemCreateMLModel)
            return CreateMLModelActivity.TAG;
        return "";
    }

    /**
     * This function switches between the different activities.
     * @param firebaseDatabaseHelper The database of the app. Used to retrieve the user's ML Models and display it in {@link MyModelsActivity}.
     */
    public void switchActivity(FirebaseDatabaseHelper firebaseDatabaseHelper) {
        Intent[] intents = {null};

        navigationView.setNavigationItemSelectedListener(item -> {
            if (this.tag.equals(this.getActivityNameForItemId(item.getItemId()))) // If the user wants to move to the same screen the app will do nothing.
                return true;

            if (item.getItemId() == R.id.itemTutorial) { // If the user wants to move to the home screen.
                intents[0] = new Intent(context, MainActivity.class);
                context.startActivity(intents[0]);
            }

            else if (item.getItemId() ==R.id.itemCreateMLModel) { // If the user wants to move the app's main screen (i.e., CreateMLModelActivity).
                intents[0] = new Intent(context, CreateMLModelActivity.class);
                context.startActivity(intents[0]);
                return true;
            }

            else if (item.getItemId() == R.id.itemMyModels) {
                intents[0] = new Intent(context, MyModelsActivity.class);
                intents[0].putExtra("models", firebaseDatabaseHelper.getModels());
                context.startActivity(intents[0]);
                return true;
            }
            else if (item.getItemId() == R.id.itemAbout) {
                intents[0] = new Intent(context, AboutActivity.class);
                intents[0].putExtra("models", firebaseDatabaseHelper.getModels());
                context.startActivity(intents[0]);
                return true;
            }
            return false;
        });
    }
}
