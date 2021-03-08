package com.example.omnidrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omnidrive.ui.login.LoginActivity;
import com.example.omnidrive.ui.signup.SignupActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountSettings extends AppCompatActivity {

    private Bundle extras;
    static final String TAG = MainActivity.class.getSimpleName();
    static final String BASE_URL = "http://18.220.4.123:8080/api/v1.0/";
    static Retrofit retrofit = null;

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
        String passFirst = extras.getString("firstname");
        String passLast = extras.getString("lastname");
        String passPassword = extras.getString("password");

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
                if (email.getText().toString().isEmpty() || first.getText().toString().isEmpty() || last.getText().toString().isEmpty()) {
                    Toast.makeText(AccountSettings.this, "Fields cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    if (retrofit == null) {
                        retrofit = new Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                    }
                    UserApiService userApiService = retrofit.create(UserApiService.class);
                    if (!email.getText().toString().equals(passEmail)) {
                        UserRequest u = new UserRequest(email.getText().toString(), first.getText().toString(), last.getText().toString(), passPassword);
                        Call<User> userCall = userApiService.newUser(u);
                        userCall.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.body() != null) {
                                    Toast.makeText(AccountSettings.this, "Changes saved", Toast.LENGTH_SHORT).show();
                                    extras.remove("email");
                                    extras.putString("email", email.getText().toString());
                                    extras.remove("firstname");
                                    extras.putString("firstname", first.getText().toString());
                                    extras.remove("lastname");
                                    extras.putString("lastname", last.getText().toString());
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class).putExtras(extras);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(AccountSettings.this, "Could not save changes", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable throwable) {
                                Toast.makeText(AccountSettings.this, "Could not save changes", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    UserRequest u = new UserRequest(email.getText().toString(), first.getText().toString(), last.getText().toString(), passPassword);
                    Call<User> userCall = userApiService.updateUser(email.getText().toString(), u);
                    userCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.body() != null) {
                                Toast.makeText(AccountSettings.this, "Changes saved", Toast.LENGTH_SHORT).show();
                                extras.remove("email");
                                extras.putString("email", email.getText().toString());
                                extras.remove("firstname");
                                extras.putString("firstname", first.getText().toString());
                                extras.remove("lastname");
                                extras.putString("lastname", last.getText().toString());
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class).putExtras(extras);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(AccountSettings.this, "Could not save changes", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable throwable) {
                            Toast.makeText(AccountSettings.this, "Could not save changes", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}