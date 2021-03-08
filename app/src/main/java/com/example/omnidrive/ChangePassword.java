package com.example.omnidrive;

import android.content.Intent;
import android.os.Bundle;

import com.example.omnidrive.ui.login.LoginActivity;
import com.example.omnidrive.ui.signup.SignupActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePassword extends AppCompatActivity {
    private Bundle extras;
    static final String TAG = MainActivity.class.getSimpleName();
    static final String BASE_URL = "http://18.220.4.123:8080/api/v1.0/";
    static Retrofit retrofit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Intent intent = getIntent();
        extras = intent.getExtras();
        String passEmail = extras.getString("email");
        String passFirst = intent.getStringExtra("firstname");
        String passLast = intent.getStringExtra("lastname");

        final Button changePassword = (Button) findViewById(R.id.changePasswordBtn);
        changePassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView inputPassword = (TextView) findViewById(R.id.etPassword);
                TextView inputConfirm = (TextView) findViewById(R.id.etConfirmPassword);
                if (inputPassword.getText().toString().isEmpty() || inputConfirm.getText().toString().isEmpty()) {
                    Toast.makeText(ChangePassword.this, "You must enter all fields", Toast.LENGTH_SHORT).show();
                } else if (!inputPassword.getText().toString().equals(inputConfirm.getText().toString())) {
                    Toast.makeText(ChangePassword.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {

                    if (retrofit == null) {
                        retrofit = new Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                    }
                    UserApiService userApiService = retrofit.create(UserApiService.class);

                    try {
                        UserRequest u = new UserRequest(passEmail, passFirst, passLast, inputPassword.getText().toString());
                        Call<User> userCall = userApiService.updateUser(passEmail, u);
                        userCall.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.body() != null) {
                                    Toast.makeText(ChangePassword.this, "Password changed", Toast.LENGTH_SHORT).show();
                                    extras.remove("password");
                                    extras.putString("password", inputPassword.getText().toString());
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class).putExtras(extras);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(ChangePassword.this, "Could not save changes", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable throwable) {
                                Toast.makeText(ChangePassword.this, "Could not save changes", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }
}