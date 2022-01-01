package com.automl.automl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

public class MyAccountActivity extends AppCompatActivity {

    private TextView tvEmail;
    private Button btnChangePassword;
    private Button btnDeleteData;
    private Button btnDeleteAccount;
    private Switch switchBlockNotifications;

    private DatabaseManager databaseManager;

    private Context originalActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        tvEmail = findViewById(R.id.tvEmail);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnDeleteData = findViewById(R.id.btnDeleteData);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        switchBlockNotifications = findViewById(R.id.switchBlockNotifications);

        databaseManager = new DatabaseManager();

        FirebaseUser user = databaseManager.getUser();

        tvEmail.setText(tvEmail.getText().toString() + user.getEmail());

        btnChangePassword.setOnClickListener(view -> {
            databaseManager.changePassword(user.getEmail());
        });

        btnDeleteAccount.setOnClickListener(view -> {
            databaseManager.deleteUser();
            Intent intent = getIntent();
            String activityName = intent.getStringExtra("context");

            if (activityName.equals(MainActivity.TAG))
                intent = new Intent(MyAccountActivity.this, MainActivity.class);
            else if (activityName.equals(CreateMLModelActivity.TAG))
                intent = new Intent(MyAccountActivity.this, CreateMLModelActivity.class);
            startActivity(intent); // Since the user was deleted, the user will be sent back to the original activity.
        });
    }
}