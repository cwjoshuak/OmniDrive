package com.example.omnidrive;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.omnidrive.ui.login.LoginActivity;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;

import org.json.JSONObject;


public class Settings extends AppCompatActivity {

    private ISingleAccountPublicClientApplication mSingleAccountApp;
    private IAccount mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Activity activity = this;


        PublicClientApplication.createSingleAccountPublicClientApplication(getApplicationContext(),
                R.raw.auth_config_single_account,
                new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        /**
                         * This test app assumes that the app is only going to support one account.
                         * This requires "account_mode" : "SINGLE" in the config json file.
                         **/
                        mSingleAccountApp = application;
                        loadAccount();

                        final Button signInWithMicrosoft = findViewById(R.id.btnMicrosoftSignIn);
                        final Button signOutMicrosoft = findViewById(R.id.btnMicrosoftSignOut);
                        final Button signOutAll = findViewById(R.id.btnSignOutAll);
                        signInWithMicrosoft.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSingleAccountApp.signIn(activity, null, new String[]{"Files.Read.All"}, getAuthInteractiveCallback());
                                signInWithMicrosoft.setVisibility(View.INVISIBLE);
                                signInWithMicrosoft.setEnabled(false);
                                signOutMicrosoft.setVisibility(View.VISIBLE);
                                signOutMicrosoft.setEnabled(true);
                            }
                        });
                        signOutMicrosoft.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mSingleAccountApp.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
                                    @Override
                                    public void onSignOut() {
                                        Toast.makeText(Settings.this, "Signed out of One Drive", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(@NonNull MsalException exception) {
                                        Toast.makeText(Settings.this, "Unable to sign out of One Drive", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                signOutMicrosoft.setVisibility(View.INVISIBLE);
                                signOutMicrosoft.setEnabled(false);
                                signInWithMicrosoft.setVisibility(View.VISIBLE);
                                signInWithMicrosoft.setEnabled(true);

                            }
                        });

                        signOutAll.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mSingleAccountApp.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
                                    @Override
                                    public void onSignOut() {
                                        Toast.makeText(Settings.this, "Signed out of all accounts", Toast.LENGTH_SHORT).show();
                                        signOutMicrosoft.setVisibility(View.INVISIBLE);
                                        signOutMicrosoft.setEnabled(false);
                                        signInWithMicrosoft.setVisibility(View.VISIBLE);
                                        signInWithMicrosoft.setEnabled(true);
                                    }

                                    @Override
                                    public void onError(@NonNull MsalException exception) {
                                        Toast.makeText(Settings.this, "Unable to sign out", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                    }

                    @Override
                    public void onError(MsalException exception) {
                        System.out.println(exception.getMessage());
                    }
                });



    }


    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                Log.d("", "Successfully authenticated");
                Log.d("TAG", "ID Token: " + authenticationResult.getAccount().getClaims().get("id_token"));

                /* Update account */
                mAccount = authenticationResult.getAccount();

                /* call graph */
                callGraphAPI(authenticationResult);
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d("TAG", "Authentication failed: " + exception.toString());


                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d("TAG", "User cancelled login.");
            }
        };
    }

    private void callGraphAPI(final IAuthenticationResult authenticationResult) {
        Activity activity = this;
        final String defaultGraphResourceUrl = MSGraphRequestWrapper.MS_GRAPH_ROOT_ENDPOINT + "v1.0/me";
        MSGraphRequestWrapper.callGraphAPIUsingVolley(
                activity,
                defaultGraphResourceUrl,
                authenticationResult.getAccessToken(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        /* Successfully called graph, process data and send to UI */
                        Log.d("TAG", "Response: " + response.toString());
//                        displayGraphResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "Error: " + error.toString());
//                        displayError(error);
                    }
                });
    }

    private void loadAccount() {
        final Button signInWithMicrosoft = findViewById(R.id.btnMicrosoftSignIn);
        final Button signOutMicrosoft = findViewById(R.id.btnMicrosoftSignOut);

        if (mSingleAccountApp == null) {
            signOutMicrosoft.setVisibility(View.INVISIBLE);
            signOutMicrosoft.setEnabled(false);
            signInWithMicrosoft.setVisibility(View.VISIBLE);
            signInWithMicrosoft.setEnabled(true);
            return;
        }

        mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
            @Override
            public void onAccountLoaded(@Nullable IAccount activeAccount) {
                // You can use the account data to update your UI or your app database.
                mAccount = activeAccount;
                System.out.println("I SINGLE ACCOUNT APP:" +mSingleAccountApp);

                System.out.println("I SINGLE ACCOUNT APP:" +activeAccount);

            }

            @Override
            public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {

            }

            @Override
            public void onError(@NonNull MsalException exception) {

            }
        });
    }
}