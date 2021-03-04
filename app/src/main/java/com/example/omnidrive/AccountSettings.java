package com.example.omnidrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.omnidrive.ui.signup.SignupActivity;

public class AccountSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // TODO: populate user info in text fields



        final Button changePassword = findViewById(R.id.changePasswordBtn);
        changePassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: remember email to change the password for
                Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(intent);
            }
        });

        final Button saveChanges = findViewById(R.id.save_changes);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: change first name, last name, email in database, add pop up to confirm changes
            }
        });
    }
}