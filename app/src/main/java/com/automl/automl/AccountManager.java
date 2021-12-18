package com.automl.automl;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
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

    private Context context;
    private DatabaseManager manager;
    private FirebaseUser user = null;

    public AccountManager(Context context) {
        this.context = context;
        this.manager = new DatabaseManager();
    }

    /**
     * This function opens a dialog where the user will select account-related actions.
     */
    public String openAccountManagerDialog() {
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

        btnSignInSignOut.setOnClickListener(view -> {
            if (btnSignInSignOut.getText().toString().equals(context.getString(R.string.sign_in))) {
                createSignInDialog();
                this.user = this.manager.getUser();
                if (this.user != null) // If the user is not null, then there is a user logged in. Therefore, the option of signing in will be blocked until the user logs out.
                    btnSignInSignOut.setText(context.getString(R.string.sign_out));
                else // If there isn't a user logged in the access to the account management activity is blocked.
                    btnAccountSettings.setVisibility(View.INVISIBLE);
            }
            else { // If the user has logged out of the app.
                this.manager.signOut();
                btnSignInSignOut.setText(R.string.sign_in);
                this.user = this.manager.getUser();
                btnAccountSettings.setVisibility(View.INVISIBLE);
            }
        });

        btnSignup.setOnClickListener(view -> {
            createSignUpDialog();
            dialog.dismiss();
            btnAccountSettings.setVisibility(View.VISIBLE);
        });

        dialog.show();
        return null;
    }

    /**
     * This function creates the sign in dialog
     * @see R.layout#sign_in_dialog
     */
    private void createSignInDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.sign_in_dialog);
        dialog.setCancelable(true);

        EditText etEmail = dialog.findViewById(R.id.etSignInEmail);
        EditText etPassword = dialog.findViewById(R.id.etSignInPassword);
        Button btnSignIn = dialog.findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(view -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            boolean hasSuccessfullySignedIn = this.manager.signIn(email, password);

            if (hasSuccessfullySignedIn) { // Greet the user.
                Toast.makeText(context, "Welcome Back!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
            else // Notify the user about an error.
                Toast.makeText(context, "Something went wrong. Please make sure the email and passwords are correct.", Toast.LENGTH_SHORT).show();
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
        EditText etPassword = dialog.findViewById(R.id.etSignUpPassword);
        EditText etRetypePassword = dialog.findViewById(R.id.etSignUpRetypePassword);
        Button btnSignUp = dialog.findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(view -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String retypePassword = etRetypePassword.getText().toString();

            boolean hasSuccessfullySignedUp = this.manager.signUp(email, password, retypePassword);

            if (hasSuccessfullySignedUp) { // If a new user was created.
                Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
            else
                Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
        });
        dialog.show();
    }
}
