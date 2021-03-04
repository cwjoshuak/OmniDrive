package com.example.omnidrive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.omnidrive.R;
import com.example.omnidrive.ui.signup.SignupActivity;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        final Button resetPassword = findViewById(R.id.resetBtn);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO : remember which email to change the password for? check if email exists too
                Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(intent);
            }
        });
    }
}