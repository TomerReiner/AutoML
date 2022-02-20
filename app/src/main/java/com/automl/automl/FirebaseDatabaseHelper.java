package com.automl.automl;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class manages the database and all actions related to it.
 */
public class FirebaseDatabaseHelper {

    public static final String LOGGED_IN_USERS = "logged_in_users";
    public static final String CURRENTLY_LOGGED_IN = "currently_logged_in";
    public static final String DEVICE_ID = "device_id";
    public static final String ML_MODELS = "ml_models";
    public static final String USERS = "users";
    public static final String USERNAME = "username";
    public static final String PHONE_NUM = "phoneNum";
    public static final String PASSWORD = "password";

    private User[] user = {null}; // The current user that is logged in to the app.
    private String deviceId;
    private Map map;

    public FirebaseDatabaseHelper(Context context) {
        this.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        getCurrentlyLoggedInUser();
    }

    public User getUser() {
        return this.user[0];
    }

    public void getData() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference();

        ref.child(USERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                map = (Map) snapshot.getValue();
                Log.e("DATA", "DATA" + snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCurrentlyLoggedInUser() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference().child(LOGGED_IN_USERS);

        ref.orderByKey().equalTo(this.deviceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                if (map != null && map.get(deviceId) instanceof HashMap) {
                    HashMap<String, String> userData = (HashMap<String, String>) map.get(deviceId);
                    user[0] = new User(userData.get(USERNAME), userData.get(PHONE_NUM), userData.get(PASSWORD));
                }
                else
                    user[0] = null;
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

        ref.child(LOGGED_IN_USERS).child(this.deviceId).setValue(this.user[0]); // Chane the logged in user in this device to the new user.
        ref.child(USERS).child(username).setValue(this.user[0]); // Save the user.
        ref.child(USERS).child(username).child(ML_MODELS).setValue(""); // Create place for the ML Models.
        ref.push();

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
            System.out.println(userData);
            String realPassword = userData.get(PASSWORD); // Extract the user's password from the dataset.
            String insertedPassword = "" + password.hashCode();

            if (insertedPassword.equals(realPassword)) { // The login was successful.
                user[0] = new User(userData.get(USERNAME), userData.get(PHONE_NUM), userData.get(PASSWORD));
                System.out.println(user[0]);
                ref.child(LOGGED_IN_USERS).child(deviceId).setValue(userData); // Chane the logged in user in this device to the new user.
                ref.push();
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    /**
     * This function signs the user out of the app. Signing out is done by setting the currently logged in user in the device id to an empty string.
     */
    public void signOut() { // TODO - make signout work
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference().child(LOGGED_IN_USERS).child(this.deviceId);
        ref.setValue("");
        this.user[0] = null;
    }

    /**
     * This function adds a ML Model to the database. THe model will be added to the user's model.
     * @param username The username of the user.
     * @param model The ML Model to add.
     */
    public void addMLModel(String username, MLModel model) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference().child("" + username.hashCode());
        ref.child(ML_MODELS).setValue(model); // Add the model to the database.
        ref.push();
    }

    /**
     * This function sends a change-password email to <code>email</code>.
     * @param email The email of the user. A change password email will be sent to this address.
     */
    public void changePassword(String email) {
        // TODO - complete
    }

    /**
     * This function deletes the current user.
     */
    public void deleteUser() {
        User currentUser = this.getUser();

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference().child("users").child("" + currentUser.getUsername());
        ref.removeValue();

        //currentUser.delete();
    }


    /**
     * This function sends a verification SMS to the user.
     * @param username The username of the user from which we can get the username.
     * The user's phone number is stored in the database.
     * @param verificationCode 6 Digits long verification code.
     * @see #signUp(String, String, String, String)
     */
    public void sendVerificationCode(Context context, String username, String verificationCode) { // TODO - fix
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference().child(USERS);

        ref.orderByKey().equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map map = (Map) snapshot.getValue();

                HashMap<String, String> m = (HashMap<String, String>) map.get(username);
                String phone = m.get(PHONE_NUM);

                try {
                    SmsManager manager = SmsManager.getDefault();
                    PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), PendingIntent.FLAG_IMMUTABLE);
                    manager.sendTextMessage(phone, null, verificationCode + " is your verification code.", sentIntent, null);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
     * This function retrieves all the ML Models of a user in the database.
     * @param username The username of the user.
     * @return An array list with all the user's models.
     */
    public ArrayList<MLModel> getMLModels(String username) {

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference().child("users").child(username).child("ML Models");

        ArrayList<MLModel> models = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    MLModel mlModel = postSnapshot.getValue(MLModel.class);
                    models.add(mlModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return models;
    }

    /**
     * This function check if a user already exists or not.
     * @param username The username.
     * @return <code>true</code> if the user already exists, <code>false</code> otherwise.
     */
    private boolean isUserExists(String username) {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference().child(USERNAME);

        boolean[] b = {false};

        ref.orderByKey().equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                b[0] = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return b[0];
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
