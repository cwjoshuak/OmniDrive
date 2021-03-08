package com.example.omnidrive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.omnidrive.R;
import com.example.omnidrive.ui.login.LoginActivity;
import com.example.omnidrive.ui.signup.SignupActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgotPassword extends AppCompatActivity {

    static final String TAG = MainActivity.class.getSimpleName();
    static final String BASE_URL = "http://18.220.4.123:8080/api/v1.0/";
    static Retrofit retrofit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        final Button resetPassword = findViewById(R.id.resetBtn);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView inputEmail = (TextView) findViewById(R.id.editTextTextPersonName);
                if (inputEmail.getText().toString().isEmpty()) {
                    Toast.makeText(ForgotPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
                UserApiService userApiService = retrofit.create(UserApiService.class);
                Call<User> call = userApiService.getUser(inputEmail.getText().toString());
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.body() == null) {
                            Toast.makeText(ForgotPassword.this, "That email does not exist", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String[] values = {
                                response.body().getEmail(),
                                response.body().getFirstname(),
                                response.body().getLastname(),
                                response.body().getPassword()
                        };

                            User user = response.body();
                            Bundle extras = new Bundle();
                            extras.putString("email", user.getEmail());
                            extras.putString("firstname", user.getFirstname());
                            extras.putString("lastname", user.getLastname());
                            extras.putString("password", user.getPassword());
                            Intent intent = new Intent(getApplicationContext(), ChangePassword.class).putExtras(extras);
                            startActivity(intent);

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable throwable) {
                        Log.e(TAG, throwable.toString());
                    }
                });
            }
        });
    }
}