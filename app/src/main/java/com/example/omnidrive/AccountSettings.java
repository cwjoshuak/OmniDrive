package com.example.omnidrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omnidrive.ui.signup.SignupActivity;

public class AccountSettings extends AppCompatActivity {
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        TextView first = (TextView) findViewById(R.id.firstName);
        TextView last = (TextView) findViewById(R.id.lastName);
        TextView email = (TextView) findViewById(R.id.user_email);

        Intent intent = getIntent();
        extras = intent.getExtras();
        String passEmail = extras.getString("email");
        String passFirst = intent.getStringExtra("firstname");
        String passLast = intent.getStringExtra("lastname");

        email.setText(passEmail);
        first.setText(passFirst);
        last.setText(passLast);


        final Button changePassword = findViewById(R.id.changePasswordBtn);
        changePassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePassword.class).putExtras(extras);
                startActivity(intent);
            }
        });

        final Button saveChanges = findViewById(R.id.save_changes);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: change first name, last name, email in database, add pop up to confirm changes
                Toast.makeText(AccountSettings.this, "Changes saved", Toast.LENGTH_SHORT).show();
            }
        });
    }
}