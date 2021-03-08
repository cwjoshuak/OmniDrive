package com.example.omnidrive.ui.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omnidrive.MainActivity;
import com.example.omnidrive.R;
import com.example.omnidrive.User;
import com.example.omnidrive.UserApiService;
import com.example.omnidrive.UserRequest;
import com.example.omnidrive.ui.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends Activity {

    static final String TAG = MainActivity.class.getSimpleName();
    static final String BASE_URL = "http://18.220.4.123:8080/api/v1.0/";
    static Retrofit retrofit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final Button signup = findViewById(R.id.signupBtn);
        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView inputFirst = (TextView) findViewById(R.id.etFirstname);
                TextView inputLast = (TextView) findViewById(R.id.etLastname);
                TextView inputEmail = (TextView) findViewById(R.id.etUsername);
                TextView inputPassword = (TextView) findViewById(R.id.etPassword);
                TextView inputConfirm = (TextView) findViewById(R.id.etConfirmPassword);
                if (inputFirst.getText().toString().isEmpty() || inputLast.getText().toString().isEmpty() || inputEmail.getText().toString().isEmpty() || inputPassword.getText().toString().isEmpty()) {
                    Toast.makeText(SignupActivity.this, "You must enter all fields", Toast.LENGTH_SHORT).show();
                }
                else if (!inputPassword.getText().toString().equals(inputConfirm.getText().toString())) {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (retrofit == null) {
                        retrofit = new Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                    }
                    UserApiService userApiService = retrofit.create(UserApiService.class);

                    try {
                        UserRequest u = new UserRequest(inputEmail.getText().toString(), inputFirst.getText().toString(), inputLast.getText().toString(), inputPassword.getText().toString());
                        Call<User> userCall = userApiService.newUser(u);
                        userCall.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.body() != null) {
                                    Toast.makeText(SignupActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(SignupActivity.this, "Could not make account", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable throwable) {
                                Toast.makeText(SignupActivity.this, "Fail", Toast.LENGTH_SHORT).show();
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
