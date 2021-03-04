package com.example.omnidrive.ui.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.omnidrive.R;
import com.example.omnidrive.ui.login.LoginActivity;

public class SignupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final Button signup = findViewById(R.id.signupBtn);
        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: add user info to database
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
