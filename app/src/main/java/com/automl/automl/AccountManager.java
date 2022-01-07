package com.automl.automl;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

/**
 * This class manages the account.
 * This class will open an account management dialog where the user will be able to select several actions:
 * <pre>
 * 1. Create a new account.
 * 2. Sign in.
 * 3. Sign out.
 * 4. Open account settings activity.
 * </pre>
 */
public class AccountManager {

    private final Context context;
    private final DatabaseManager manager;
    private FirebaseUser user;

    public AccountManager(Context context) {
        this.context = context;
        this.manager = new DatabaseManager();
        this.user = manager.getUser();
    }

    /**
     * This function opens a dialog where the user will select account-related actions.
     */
    public void openAccountManagerDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.account_manager_dialog);
        dialog.setCancelable(true);

        Button btnAccountSettings = dialog.findViewById(R.id.btnAccountSettings);
        Button btnSignInSignOut = dialog.findViewById(R.id.btnSignInSignOut);
        Button btnSignup = dialog.findViewById(R.id.btnSignup);

         this.user = manager.getUser();

        if (user != null) { // If there is a user that is logged into the app we would like to enable the account settings activity.
            btnAccountSettings.setVisibility(View.VISIBLE);
            btnSignInSignOut.setText(R.string.sign_out);
        }
        else // If there isn't a user logged in the app we would like to enable users to log in.
            btnSignInSignOut.setText(R.string.sign_in);

        btnAccountSettings.setOnClickListener(view -> {
            Intent intent = new Intent(context, MyAccountActivity.class);
            intent.putExtra("context", context.getClass().getSimpleName()); // Send the activity name to MyAccountActivity so it will return the user to the original activity.
            context.startActivity(intent);
        });

        btnSignInSignOut.setOnClickListener(view -> {
            if (btnSignInSignOut.getText().toString().equals(context.getString(R.string.sign_in))) {
                createSignInDialog(btnSignInSignOut, btnAccountSettings);
                dialog.dismiss();
            }
            else { // If the user has logged out of the app.
                this.manager.signOut();
                btnSignInSignOut.setText(R.string.sign_in);
                this.user = this.manager.getUser();
                btnAccountSettings.setVisibility(View.INVISIBLE);
                dialog.dismiss();
            }
        });

        btnSignup.setOnClickListener(view -> {
            createSignUpDialog();
            dialog.dismiss();
            btnAccountSettings.setVisibility(View.VISIBLE);
        });

        dialog.show();
    }

    /**
     * This function creates the sign up dialog.
     */
    private void createSignUpDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.sign_up_dialog);
        dialog.setCancelable(true);

        EditText etEmail = dialog.findViewById(R.id.etSignUpEmail);
        EditText etPhone = dialog.findViewById(R.id.etPhone);
        EditText etPassword = dialog.findViewById(R.id.etSignUpPassword);
        EditText etRetypePassword = dialog.findViewById(R.id.etSignUpRetypePassword);
        Button btnSignUp = dialog.findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(view -> {
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String password = etPassword.getText().toString();
            String retypePassword = etRetypePassword.getText().toString();

            boolean hasSuccessfullySignedUp = this.manager.signUp(email, phone, password, retypePassword);

            if (hasSuccessfullySignedUp) { // If a new user was created.
                Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
            else
                Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
        });
        dialog.show();
    }

    /**
     * This function creates the sign in dialog
     * @see R.layout#sign_in_dialog
     */
    private void createSignInDialog(Button btnSignInSignOut, Button btnAccountSettings) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.sign_in_dialog);
        dialog.setCancelable(true);

        EditText etEmail = dialog.findViewById(R.id.etSignInEmail);
        EditText etPassword = dialog.findViewById(R.id.etSignInPassword);
        Button btnSignIn = dialog.findViewById(R.id.btnSignIn);

        dialog.setOnCancelListener(dialogInterface -> this.manager.signOut()); // Prevent security breaches.

        btnSignIn.setOnClickListener(view -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            boolean hasSuccessfullySignedIn = this.manager.signIn(email, password);

            if (hasSuccessfullySignedIn)
                createSignInAuthDialog(dialog, btnSignInSignOut, btnAccountSettings, email, password);
            else
                Toast.makeText(context, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();

        });
        dialog.show();
    }

    /**
     * This function creates a dialog where the user will type their verification code sent via SMS.
     * The function will re-login the user once the verification process was completed.
     * @param signInDialog The signIn dialog.
     * @param btnSignInSignOut A button to change its text - sign in or sign out.
     * @param  btnAccountSettings A button to change its visibility.
     * @param email The email of the user.
     * @param password The password of the user.
     * @see DatabaseManager#sendVerificationCode(Context, String, String)
     */
    private void createSignInAuthDialog(Dialog signInDialog, Button btnSignInSignOut, Button btnAccountSettings, String email, String password) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.sign_in_verification_dialog);
        dialog.setCancelable(true);

        this.manager.signOut();

        String verificationCode = this.manager.generateVerificationCode();
        this.manager.sendVerificationCode(context, email, verificationCode);

        boolean[] b = {false}; // The result of this function.

        EditText etVerify = dialog.findViewById(R.id.etVerify);
        Button btnVerify = dialog.findViewById(R.id.btnVerify);

        btnVerify.setOnClickListener(view -> {
            String insertedCode = etVerify.getText().toString(); // The code that the user has inserted.

            if (insertedCode.equals(verificationCode)) { // If the code is correct then the user is signed in.
                this.manager.signIn(email, password);
                b[0] = true;

                btnSignInSignOut.setText(context.getString(R.string.sign_out));
                Toast.makeText(context, "Welcome Back!", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                signInDialog.dismiss();
            }
            else {

                this.manager.signOut(); // Prevent security breaches.
                btnAccountSettings.setVisibility(View.INVISIBLE);
                Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
                btnSignInSignOut.setText(context.getString(R.string.sign_in));

                dialog.dismiss();
                signInDialog.dismiss();
            }
        });

        dialog.setOnCancelListener(dialogInterface -> {
            this.manager.signOut();
            signInDialog.dismiss();
        }); // Prevent security breaches.

        dialog.show();
    }
}
