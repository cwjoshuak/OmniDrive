package com.example.omnidrive.ui.main;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.omnidrive.MSGraphRequestWrapper;
import com.example.omnidrive.MainActivity;
import com.example.omnidrive.OneDriveFile;
import com.example.omnidrive.OneDriveFileList;
import com.example.omnidrive.R;
import com.example.omnidrive.ui.login.LoginActivity;
import com.google.gson.Gson;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OneDriveFragment extends Fragment {
    private ISingleAccountPublicClientApplication mSingleAccountApp;
    private IAccount mAccount;
    private RecyclerView documentView;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
    private String defaultGraphResourceUrl = MSGraphRequestWrapper.MS_GRAPH_ROOT_ENDPOINT + "v1.0/me/drive/root/children";
    private LinearLayoutManager llm;
    private Stack<OneDriveFile> lastFilesClicked = new Stack<>();
    public interface OnItemClickListener {
        void onItemClick(OneDriveFile item);
    }

    OnItemClickListener listener = new OnItemClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onItemClick(OneDriveFile item) {

            if (item.isBackDirectory) {
                OneDriveFile lastFileClicked = lastFilesClicked.pop();
                if(lastFileClicked.parentReference.path.endsWith("root:")){
                    defaultGraphResourceUrl = MSGraphRequestWrapper.MS_GRAPH_ROOT_ENDPOINT + "v1.0/me/drive/root/children";
                }
                else {
                    defaultGraphResourceUrl = MSGraphRequestWrapper.MS_GRAPH_ROOT_ENDPOINT + "v1.0/me" +lastFileClicked.parentReference.path+":/children";
                    item.isBackDirectory = true;
//                    lastFilesClicked.push(item);
                }

//                System.out.println(item.getFullPath());
//                String[] parentPaths = lastFileClicked.parentReference.path.split("/");
//                String newPath = "";
//                for(int i = 0; i < parentPaths.length; i++) {
//                    if (parentPaths[i].endsWith(":") && i == parentPaths.length-1)
//                        break;
//                    else if (!parentPaths[i].isEmpty())
//                        newPath = newPath.join("/", parentPaths[i]);
//                }
//                if (newPath.endsWith("drive")) {
//                    newPath+="/root/children";
//                    lastFileClicked = null;
//                }
//                defaultGraphResourceUrl = MSGraphRequestWrapper.MS_GRAPH_ROOT_ENDPOINT + "v1.0/me/" +newPath;

            }

            else {
                defaultGraphResourceUrl = MSGraphRequestWrapper.MS_GRAPH_ROOT_ENDPOINT + "v1.0/me" +item.getFullPath()+":/children";
                item.isBackDirectory = true;
                lastFilesClicked.push(item);
            }


            System.out.println(defaultGraphResourceUrl);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("RUNNING");
                    mSingleAccountApp.acquireTokenSilentAsync(getScopes(), mAccount.getAuthority(), getAuthSilentCallback());
                }
            });
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.one_drive_fragment, container, false);
        documentView = view.findViewById(R.id.onedriveRecyclerView);
        llm = new LinearLayoutManager(getActivity());
        documentView.setLayoutManager(llm);
        documentView.setAdapter(new DocumentsAdapter(new ArrayList<>(), listener));
        documentView.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();

        System.out.println("DOCUMENT VIEW: "+documentView);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                PublicClientApplication.createSingleAccountPublicClientApplication(activity,
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

                            }
                            @Override
                            public void onError(MsalException exception) {
                                System.out.println(exception.getMessage());
                            }
                        });
            }
        });
    }

    /**
     * Callback used in for silent acquireToken calls.
     */
    private SilentAuthenticationCallback getAuthSilentCallback() {
        return new SilentAuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                Log.d("TAG", "Successfully authenticated");

                /* Successfully got a token, use it to call a protected resource - MSGraph */
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
                } else if (exception instanceof MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                }
            }
        };
    }

    private void loadAccount() {
        if (mSingleAccountApp == null) {
            return;
        }

        mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
            @Override
            public void onAccountLoaded(@Nullable IAccount activeAccount) {
                // You can use the account data to update your UI or your app database.
                mAccount = activeAccount;
                Activity activity = getActivity();
                TextView tv = activity.findViewById(R.id.onedriveTextView);
                ImageView iv = activity.findViewById(R.id.onedriveImageView);
                System.out.println("MACCOUNT:" + activeAccount);
                if (activeAccount != null) {
                    System.out.println("I SINGLE ACCOUNT APP:" +mSingleAccountApp);
                    System.out.println("I SINGLE ACCOUNT APP:" +activeAccount);
                    documentView.setVisibility(View.VISIBLE);
                    tv.setVisibility(View.GONE);
                    iv.setVisibility(View.GONE);
                    mSingleAccountApp.acquireTokenSilentAsync(getScopes(), mAccount.getAuthority(), getAuthSilentCallback());
                } else {
                    documentView.setVisibility(View.GONE);
                    tv.setVisibility(View.VISIBLE);
                    iv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {

            }

            @Override
            public void onError(@NonNull MsalException exception) {
                exception.printStackTrace();
            }
        });
    }

    private String[] getScopes() {
        return new String[]{"Files.Read.All"};
    }

    private void callGraphAPI(final IAuthenticationResult authenticationResult) {
        Activity activity = getActivity();
        System.out.println("GRAPH API CALLED"+ activity);

        MSGraphRequestWrapper.callGraphAPIUsingVolley(
                activity,
                defaultGraphResourceUrl,
                authenticationResult.getAccessToken(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        /* Successfully called graph, process data and send to UI */
                        Log.d("TAG", "Response: " + response.toString());
                        updateFileList(response);
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

    private void updateFileList(JSONObject response) {
        Activity activity = getActivity();
        Gson gson = new Gson();

        OneDriveFileList fileList = gson.fromJson(response.toString(), OneDriveFileList.class);
        System.out.println(fileList.toString());
        System.out.println(documentView);

        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {

                if (!lastFilesClicked.empty()) {
                    if (fileList.fileList != null){
                        fileList.fileList.add(0, lastFilesClicked.peek());
                        ((DocumentsAdapter) documentView.getAdapter()).setmData(fileList.fileList);
                    }
                    else {
                        OneDriveFileList fl = new OneDriveFileList();
                        fl.fileList.add(0, lastFilesClicked.peek());
                        ((DocumentsAdapter) documentView.getAdapter()).setmData(fl.fileList);
                    }
                } else {
                    if (fileList.fileList != null){
                        ((DocumentsAdapter) documentView.getAdapter()).setmData(fileList.fileList);
                    }
                }
                documentView.getAdapter().notifyDataSetChanged();
            }
        });

    }
}
