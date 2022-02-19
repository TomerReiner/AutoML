package com.automl.automl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

@SuppressWarnings("FieldCanBeLocal")
public class MyAccountActivity extends AppCompatActivity {

    private TextView tvUsername;
    private Button btnChangePassword;
    private Button btnDeleteData;
    private Button btnDeleteAccount;
    private Switch switchBlockNotifications;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    private Context originalActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        tvUsername = findViewById(R.id.tvUsername);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnDeleteData = findViewById(R.id.btnDeleteData);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        switchBlockNotifications = findViewById(R.id.switchBlockNotifications);

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(MyAccountActivity.this);

        Intent[] intent = {getIntent()};

        User user = new User(intent[0].getStringExtra(FirebaseDatabaseHelper.USERNAME), intent[0].getStringExtra(FirebaseDatabaseHelper.PHONE_NUM), intent[0].getStringExtra(FirebaseDatabaseHelper.PASSWORD));

        tvUsername.setText(tvUsername.getText().toString() + user.getUsername());

        btnChangePassword.setOnClickListener(view -> {
            firebaseDatabaseHelper.changePassword(user.getUsername());
        });

        btnDeleteAccount.setOnClickListener(view -> {
            firebaseDatabaseHelper.deleteUser();
            String activityName = intent[0].getStringExtra("context");

            if (activityName.equals(MainActivity.TAG))
                intent[0] = new Intent(MyAccountActivity.this, MainActivity.class);
            else if (activityName.equals(CreateMLModelActivity.TAG))
                intent[0] = new Intent(MyAccountActivity.this, CreateMLModelActivity.class);
            startActivity(intent[0]); // Since the user was deleted, the user will be sent back to the original activity.
        });
    }
}