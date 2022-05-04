package com.automl.automl;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("FieldCanBeLocal")
public class MyAccountActivity extends AppCompatActivity {

    public static final String DELETE_ML_MODELS = "deleteMLModels";
    public static final String DELETE_ACCOUNT = "deleteAccount";

    private TextView tvUsername;
    private Button btnChangePassword;
    private Button btnDeleteData;
    private Button btnDeleteAccount;

    private User user;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private SQLiteDatabaseHelper sqLiteDatabaseHelper;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        tvUsername = findViewById(R.id.tvUsername);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnDeleteData = findViewById(R.id.btnDeleteData);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(MyAccountActivity.this);
        sqLiteDatabaseHelper = new SQLiteDatabaseHelper(MyAccountActivity.this);

        intent = getIntent();

        user = sqLiteDatabaseHelper.getUser();

        tvUsername.setText(tvUsername.getText().toString() + user.getUsername());

        btnChangePassword.setOnClickListener(view -> createChangePasswordDialog());

        btnDeleteData.setOnClickListener(v -> createDeleteDataOrUserDialog(DELETE_ML_MODELS));

        btnDeleteAccount.setOnClickListener(view -> createDeleteDataOrUserDialog(DELETE_ACCOUNT));
    }

    /**
     * This function creates a change password dialog.
     * @see FirebaseDatabaseHelper#changePassword(User)
     * @see R.layout#change_password_dialog
     */
    private void createChangePasswordDialog() {
        Dialog dialog = new Dialog(MyAccountActivity.this);
        dialog.setContentView(R.layout.change_password_dialog);
        dialog.setCancelable(true);

        EditText etCurrentPassword = dialog.findViewById(R.id.etCurrentPassword);
        EditText etNewPassword = dialog.findViewById(R.id.etNewPassword);
        EditText etNewPasswordRetype = dialog.findViewById(R.id.etNewPasswordRetype);
        Button btnChangePasswordConfirm = dialog.findViewById(R.id.btnChangePasswordConfirm);

        btnChangePasswordConfirm.setOnClickListener(v -> {
            String insertedCurrentPassword = "" + etCurrentPassword.getText().toString().hashCode();
            String newPassword = "" + etNewPassword.getText().toString().hashCode();
            String newPasswordRetype = "" + etNewPasswordRetype.getText().toString().hashCode();

            if (insertedCurrentPassword.equals(user.getPassword()) && newPassword.equals(newPasswordRetype) && newPassword.length() > 0) { // If all the information is correct.
                User updatedUser = new User(user.getUsername(), user.getPhoneNum(), newPassword);
                firebaseDatabaseHelper.changePassword(updatedUser);
                dialog.dismiss();
            }
            else
                Toast.makeText(this, "One of the fields has incorrect information.", Toast.LENGTH_SHORT).show();
        });
        dialog.show();
    }

    /**
     * This function creates a dialog to delete all the user's data (i.e., their ML Models) or their account.
     * This dialog is used for both deleting ML Models and Deleting the account since both actions require only password authentication.
     * @param action The action taken. either {@link #DELETE_ML_MODELS} or {@link #DELETE_ACCOUNT}.
     * @see FirebaseDatabaseHelper#deleteData(String)
     * @see R.layout#delete_data_or_user_dialog
     */
    private void createDeleteDataOrUserDialog(String action) {
        Dialog dialog = new Dialog(MyAccountActivity.this);
        dialog.setContentView(R.layout.delete_data_or_user_dialog);
        dialog.setCancelable(true);

        EditText etPasswordDelete = dialog.findViewById(R.id.etPasswordDelete);
        Button btnDelete = dialog.findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(v -> {
            String insertedPassword = "" + etPasswordDelete.getText().toString().hashCode();

            if (!user.getPassword().equals(insertedPassword)) { // If the inserted password is incorrect.
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (action.equals(DELETE_ML_MODELS)) // If the user wants to delete their ML Models
                firebaseDatabaseHelper.deleteData(user.getUsername());
            else { // If the user wants to delete their account.
                firebaseDatabaseHelper.deleteUser(user.getUsername());

                String activityName = intent.getStringExtra("context"); // Get the original activity to return the user there.

                if (activityName.equals(MainActivity.TAG))
                    intent = new Intent(MyAccountActivity.this, MainActivity.class);
                else if (activityName.equals(CreateMLModelActivity.TAG))
                    intent = new Intent(MyAccountActivity.this, CreateMLModelActivity.class);

                else if (activityName.equals(MyModelsActivity.TAG))
                    intent = new Intent(MyAccountActivity.this, MyModelsActivity.class);
                else
                    intent = new Intent(MyAccountActivity.this, AboutActivity.class);

                dialog.dismiss();
                startActivity(intent); // Since the user was deleted, the user will be sent back to the original activity.
            }
            dialog.dismiss();
        });
        dialog.show();
    }

}