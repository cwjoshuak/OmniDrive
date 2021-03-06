package com.example.omnidrive.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omnidrive.ForgotPassword;
import com.example.omnidrive.MainActivity;
import com.example.omnidrive.R;
import com.example.omnidrive.User;
import com.example.omnidrive.UserApiService;
import com.example.omnidrive.ui.signup.SignupActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    static final String TAG = MainActivity.class.getSimpleName();
    static final String BASE_URL = "http://18.220.4.123:8080/api/v1.0/";
    static Retrofit retrofit = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button loginBtn = findViewById(R.id.login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connect();
                User user = new User("test@gmail.com", "FirstName", "lastName", "PW");
                Bundle extras = new Bundle();
                extras.putString("email", user.getEmail());
                extras.putString("firstname", user.getFirstname());
                extras.putString("lastname", user.getLastname());
                extras.putString("password", user.getPassword());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class).putExtras(extras);
                startActivity(intent);
            }
        });

        final Button signupBtn = findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        final TextView forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
            }
        });


    }

    private void connect() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        UserApiService userApiService = retrofit.create(UserApiService.class);
        TextView inputEmail = (TextView) findViewById(R.id.etUsername);
        TextView inputPassword = (TextView) findViewById(R.id.etPassword);
        Call<User> call = userApiService.getUser(inputEmail.getText().toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.body() == null) {
                    Toast.makeText(LoginActivity.this, "Email/Password Incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] values = {
                        response.body().getEmail(),
                        response.body().getFirstname(),
                        response.body().getLastname(),
                        response.body().getPassword()
                };

                if (inputEmail.getText().toString().equals(values[0]) && inputPassword.getText().toString().equals(values[3])) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    User user = response.body();
                    Bundle extras = new Bundle();
                    extras.putString("email", user.getEmail());
                    extras.putString("firstname", user.getFirstname());
                    extras.putString("lastname", user.getLastname());
                    extras.putString("password", user.getPassword());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class).putExtras(extras);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Email/Password Incorrect", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

}