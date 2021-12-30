package com.automl.automl;

import com.automl.automl.blocks.Block;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This class manages the database and all actions related to it.
 */
public class DatabaseManager {

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

        boolean[] b = {false};

        this.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                b[0] = true;
            if (task.isComplete()) // TODO - try to remove.
                b[0] = true;
        });
        return true;
    }

    /**
     * This function creates a new user.
     * @param email The email of the user.
     * @param password The password of the user.
     * @param retypePassword The password of the user.
     * @return <code>true</code> if the user was successfully created <code>false</code> otherwise.
     */
    public boolean signUp(String email, String password, String retypePassword) {
        if (!password.equals(retypePassword))
            return false;

        if (email.length() == 0 || password.length() == 0)
            return false;

        this.auth.createUserWithEmailAndPassword(email, password);
        return true;
    }

    public void signOut() {
        this.auth.signOut();
    }

    /**
     * This function adds a ML Model to the database. THe model will be added to the user's model.
     * @param email The email of the user.
     * @param mlBlock The ML Model to add.
     */
    public void AddMLModel(String email, Block mlBlock) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference().child(email);

        ref.push().setValue(mlBlock);
    }

}
