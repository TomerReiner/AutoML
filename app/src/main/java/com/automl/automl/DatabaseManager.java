package com.automl.automl;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;

import com.automl.automl.blocks.Block;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages the database and all actions related to it.
 */
public class DatabaseManager {

    public static final String PHONE_NUM = "Phone Num";

    private FirebaseAuth auth;

    public DatabaseManager() {
        this.auth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getUser() {
        return this.auth.getCurrentUser();
    }

    /**
     * This function signs a user to the app.
     * @param email The email of the user.
     * @param password The password of the user.
     * @return <code>true</code> if the user was successfully signed in <code>false</code> otherwise.
     */
    public boolean signIn(String email, String password) {
        if (email.length() == 0 || password.length() == 0)
            return false;

        boolean[] b = {false}; // true - if the login was successful, false otherwise.

        this.auth.signInWithEmailAndPassword(email, password);
        return true;
    }

    /**
     * This function creates a new user.
     * This function also inserts the phone number of the user into the database under a new username child.
     * The phone number will be used to authenticate during sign in.
     * Each user will be automatically assigned with a new username which will be the hash code of their email.
     * For example:
     * <pre>
     * Email = sample.email@gmail.com
     * Username = sample.email
     * </pre>
     * @param email The email of the user.
     * @param phone The phone number of the user.
     * @param password The password of the user.
     * @param retypePassword The password of the user.
     * @return <code>true</code> if the user was successfully created <code>false</code> otherwise.
     */
    public boolean signUp(String email, String phone, String password, String retypePassword) {
        if (!password.equals(retypePassword))
            return false;

        if (email.length() == 0 || password.length() == 0)
            return false;

        if (!phone.matches("\\d+") || phone.length() != 10)
            return false;

        this.auth.createUserWithEmailAndPassword(email, password);
        signIn(email, password);

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference().child("users");

        Map<Object, Object> map = new HashMap<>();
        map.put(PHONE_NUM, phone);

        ref.child("" + email.hashCode()).setValue(map); // Save the phone number in the database for further usage.
        ref.child("ML Models").setValue(""); // Create place for the ML Models.
        return true;
    }

    public void signOut() {
        this.auth.signOut();
    }

    /**
     * This function adds a ML Model to the database. THe model will be added to the user's model.
     * @param email The email of the user.
     * @param model The ML Model to add.
     */
    public void addMLModel(String email, MLModel model) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference().child("" + email.hashCode());
        ref.child("ML Models").setValue(model); // Add the model to the database.
    }

    /**
     * This function sends a change-password email to <code>email</code>.
     * @param email The email of the user. A change password email will be sent to this address.
     */
    public void changePassword(String email) {
        this.auth.sendPasswordResetEmail(email);
    }

    /**
     * This function deletes the current user.
     */
    public void deleteUser() {
        FirebaseUser currentUser = this.getUser();

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference().child("users").child("" + currentUser.getEmail().hashCode());
        ref.removeValue();

        currentUser.delete();
    }


    /**
     * This function sends a verification SMS to the user.
     * @param email The email of the user from which we can get the username.
     * The user's phone number is stored in the database.
     * @param verificationCode 6 Digits long verification code.
     * @see #signUp(String, String, String, String)
     */
    public void sendVerificationCode(Context context, String email, String verificationCode) {
        String username = "" + email.hashCode();

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://myml-4f150-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = db.getReference().child("users");

        ref.orderByKey().equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map map = (Map) snapshot.getValue();

                HashMap<String, String> m = (HashMap<String, String>) map.get(username);
                String phone = m.get(PHONE_NUM);

                try {
                    SmsManager manager = SmsManager.getDefault();
                    PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0);
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
     * @param email The email of the user.
     * @return An array list with all the user's models.
     */
    public ArrayList<MLModel> getMLModels(String email) {
        String username = "" + email.hashCode();

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
}
