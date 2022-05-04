package com.automl.automl;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * This class manages the database and all actions related to it.
 */
public class FirebaseDatabaseHelper {

    public static final String LOGGED_IN_USERS = "logged_in_users";
    public static final String ML_MODELS = "ml_models";
    public static final String USERS = "users";
    public static final String USERNAME = "username";
    public static final String PHONE_NUM = "phoneNum";
    public static final String PASSWORD = "password";

    private final User[] user = {null}; // The current user that is logged in to the app.
    private final String deviceId;
    private Map<Object, Object> map;
    private final SQLiteDatabaseHelper sqLiteDatabaseHelper;
    private ArrayList<MLModelDisplay> models = new ArrayList<>();

    public FirebaseDatabaseHelper(Context context) {
        this.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        sqLiteDatabaseHelper = new SQLiteDatabaseHelper(context);
        this.user[0] = this.sqLiteDatabaseHelper.getUser();
        if (this.user[0] != null)
            retrieveMLModels(user[0].getUsername());
    }

    public User getUser() {
        return this.user[0];
    }

    public void getUsersData() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference();

        ref.child(USERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                map = (Map<Object, Object>) snapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * This function creates a new user.
     * This function also inserts the phone number of the user into the database under a new username child.
     * The phone number will be used to authenticate during sign in.
     * For example:
     * @param username The username of the user.
     * @param phone The phone number of the user.
     * @param password The password of the user.
     * @param retypePassword The password of the user.
     * @return <code>true</code> if the user was successfully created <code>false</code> otherwise.
     */
    public boolean signUp(String username, String phone, String password, String retypePassword) {
        if (!password.equals(retypePassword))
            return false;

        if (username.length() == 0 || password.length() == 0)
            return false;

        if (!phone.matches("\\d+") || phone.length() != 10)
            return false;

        if (isUserExists(username)) // A new user cannot be created with an existing username.
            return false;

        if (!isUsernameValid(username)) // If the username is invalid/
            return false;

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference();

        this.user[0] = new User(username, phone, "" + password.hashCode());
        this.sqLiteDatabaseHelper.fastLogin(this.user[0]);

        ref.child(LOGGED_IN_USERS).child(this.deviceId).setValue(this.user[0]); // Chane the logged in user in this device to the new user.
        ref.child(USERS).child(username).setValue(this.user[0]); // Save the user.
        ref.child(ML_MODELS).child(username).setValue(""); // Create place for the ML Models.
        ref.push();

        getUsersData(); // Get the new users' data.

        return true;
    }

    /**
     * This function signs a user to the app.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return <code>true</code> if the user was successfully signed in <code>false</code> otherwise.
     */
    public boolean signIn(String username, String password) {
        if (username.length() == 0 || password.length() == 0)
            return false;

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference();

        try {
            HashMap<String, String> userData = (HashMap<String, String>) map.get(username); // Trying to get the user's data.
            String realPassword = userData.get(PASSWORD); // Extract the user's password from the dataset.
            String insertedPassword = "" + password.hashCode();

            if (insertedPassword.equals(realPassword)) { // The login was successful.
                user[0] = new User(userData.get(USERNAME), userData.get(PHONE_NUM), userData.get(PASSWORD));
                ref.child(LOGGED_IN_USERS).child(deviceId).setValue(userData); // Chane the logged in user in this device to the new user.
                ref.push();
                sqLiteDatabaseHelper.fastLogin(user[0]); // To login the user fast when re-opening the app.
                return true;
            }
            else // The password is incorrect.
                return false;

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This function signs the user out of the app. Signing out is done by setting the currently logged in user in the device id to an empty string.
     */
    public void signOut() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference().child(LOGGED_IN_USERS).child(this.deviceId);
        ref.setValue("");
        ref.push();
        this.models = new ArrayList<>();
        this.sqLiteDatabaseHelper.signOut();
        this.user[0] = null;
    }

    /**
     * This function sends a change-password username to <code>username</code>.
     * @param user The user with the updated password.
     */
    public void changePassword(User user) {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference();

        this.user[0] = user;

        ref.child(USERS).child(user.getUsername()).setValue("");
        ref.push();
        ref.child(LOGGED_IN_USERS).child(this.deviceId).setValue("");
        ref.push();
        ref.child(USERS).child(user.getUsername()).setValue(this.user[0]);
        ref.push();
        ref.child(LOGGED_IN_USERS).child(this.deviceId).setValue(this.user[0]);
        ref.push(); // Update the date in firebase.

        this.sqLiteDatabaseHelper.fastLogin(this.user[0]);
    }

    /**
     * This function deletes all the user's data.
     * @param username The username of the user that we want to delete their data.
     */
    public void deleteData(String username) {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference();
        ref.child(ML_MODELS).child(username).setValue("");
        ref.push();
    }

    /**
     * This function deletes the current user.
     * @param username The username of the user that we want to delete.
     */
    public void deleteUser(String username) {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference();
        ref.child(USERS).child(username).removeValue();
        ref.child(ML_MODELS).child(username).removeValue();
        ref.child(LOGGED_IN_USERS).child(this.deviceId).setValue("");
        ref.push(); // Delete the user from the registered devices.

        this.sqLiteDatabaseHelper.signOut();
        this.user[0] = null;
        this.getUsersData();
    }


    /**
     * This function sends a verification SMS to the user.
     * @param username The username of the user from which we can get the username.
     * The user's phone number is stored in the database.
     * @param verificationCode 6 Digits long verification code.
     * @see #signUp(String, String, String, String)
     */
    public void sendVerificationCode(Context context, String username, String verificationCode) {
        HashMap<String, String> userData = (HashMap<String, String>) map.get(username);
        assert userData != null;

        String phone = userData.get(PHONE_NUM);

        try { // Trying to send an sms to the user
            SmsManager manager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), PendingIntent.FLAG_IMMUTABLE);
            manager.sendTextMessage(phone, null, verificationCode + " is your verification code.", sentIntent, null);
        } catch (Exception e) {
            Toast.makeText(context, "Failed delivering message. Please give the app permission to send SMS.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * This function generates a 6 digits long verification code.
     * @return The verification code.
     */
    public String generateVerificationCode() {
        int x = (int) (Math.random() * (999999 - 100000 + 1)) + 100000;
        System.out.println(x);
        return "" + x;
    }

    /**
     * This function adds a ML Model to the database. THe model will be added to the user's model.
     * @param username The username of the user.
     * @param model The ML Model to add.
     */
    public void addMLModel(String username, MLModelDisplay model) {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference();
        this.models.add(model);

        ref.child(ML_MODELS).child(username).setValue(""); // Add the model to the database.
        ref.push();
        ref.child(ML_MODELS).child(username).setValue(this.models); // Add the model to the database.
        ref.push();
        ref.child(ML_MODELS).child(username).child("" + (this.models.size() - 1)).child("attributes").setValue(model.getAttributes());
        ref.push();
    }

    public ArrayList<MLModelDisplay> getModels() {
        return this.models;
    }

    /**
     * This function is used since retrieving the ML Models in {@link MLPipelineService} doesn't work.
     * Setting the ML Models manually for the {@link FirebaseDatabaseHelper} instance created in
     * {@link MLPipelineService} works properly.
     * @see MLPipelineService
     * @see CreateMLModelActivity#onCreate(Bundle)
     */
    public void setModels(ArrayList<MLModelDisplay> models) {
        this.models = models;
    }

    /**
     * This function retrieves all the ML Models of a user in the database.
     * @param username The username of the user.
     */
    private void retrieveMLModels(String username) {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference().child(ML_MODELS).child(username);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() instanceof String) // If the value is an instance of string then the user has no ML Models.
                    models = new ArrayList<>();
                else {
                    ArrayList<HashMap<String, Object>> modelsData = (ArrayList<HashMap<String, Object>>) snapshot.getValue();
                    for (int i = 0; i < Objects.requireNonNull(modelsData).size(); i++) {
                        MLModelDisplay model = MLModelDisplay.fromMap(modelsData.get(i));
                        models.add(model);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        System.out.println(models.size());
    }

    /**
     * This function check if a user already exists or not.
     * @param username The username.
     * @return <code>true</code> if the user already exists, <code>false</code> otherwise.
     */
    private boolean isUserExists(String username) {
        if (map == null) // If there is no user logged in the app.
        {
            return false;
        } else {
            map.keySet();
        }
        String[] users = this.map.keySet().toArray(new String[0]);

        for (String user : users) { // Iterate through all the users to check if there is already a user with this username.
            if (user.equals(username)) // If there is a user with this username.
                return true;
        }
        return false;
    }

    /**
     * This function checks if a username is valid(i.e., contains only letters and digits.)
     * @param username The username.
     * @return <code>true</code> if the username is valid, <code>false</code> otherwise.
     */
    private boolean isUsernameValid(String username) {
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < username.length(); i++) {
            if (!validChars.contains("" + username.charAt(i))) // If the username contains invalid characters.
                return false;
        }
        return true; // All the characters in the username are valid.
    }
}
