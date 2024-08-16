package com.example.androidgg;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    Button btnGoogle;
    ActivityResultLauncher<Intent> activityResultLauncher=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleSignInResult(task);
                    } else {
                        Log.w("TAG", "Google sign-in failed.");
                        // Bạn có thể thêm thông báo lỗi cho người dùng tại đây
                    }
                }
            });
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGoogle=findViewById(R.id.button);

//        oneTapClient = Identity.getSignInClient(this);
//        signUpRequest = BeginSignInRequest.builder()
//                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
//                        .setSupported(true)
//                        .build())
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        .setServerClientId(getString(R.string.web_client_id))
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                // Automatically sign in when exactly one credential is retrieved.
//                .setAutoSelectEnabled(true)
//                .build();

//        ActivityResultLauncher<IntentSenderRequest>  activityResultLauncher=
//                registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        try {
//                            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
//                            String idToken = credential.getGoogleIdToken();
//
//                            if (idToken !=  null) {
//                                // Got an ID token from Google. Use it to authenticate
//                                // with your backend.
//                                String email = credential.getId();
//                                String username = credential.getId();
//                                String password = credential.getPassword();
//                                Toast.makeText(getApplicationContext(), "Email : " + email + " Name : " + username, Toast.LENGTH_SHORT).show();
//                                Log.d("TAG", "Got ID token.");
//                            }
//                        } catch (ApiException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.web_client_id)) // Request ID token
                .requestServerAuthCode(getString(R.string.web_client_id)) // Request Auth code
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                oneTapClient.beginSignIn(signUpRequest)
//                        .addOnSuccessListener(MainActivity.this, new OnSuccessListener<BeginSignInResult>() {
//                            @Override
//                            public void onSuccess(BeginSignInResult result) {
//                                IntentSenderRequest intentSenderRequest= new IntentSenderRequest
//                                        .Builder(result.getPendingIntent().getIntentSender())
//                                        .build();
//                                activityResultLauncher.launch(intentSenderRequest);
//                            }
//                        })
//                        .addOnFailureListener(MainActivity.this, new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                // No saved credentials found. Launch the One Tap sign-up flow, or
//                                // do nothing and continue presenting the signed-out UI.
//                                Log.d("TAG", e.getLocalizedMessage());
//                            }
//                        });
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
        activityResultLauncher.launch(signInIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Get authorization code
            String authCode = account.getServerAuthCode();

            if (authCode != null) {
                Log.d("TAG", "Authorization code: " + authCode);
                Toast.makeText(getApplicationContext(), "Authorization Code: " + authCode, Toast.LENGTH_SHORT).show();

                // Send authorization code to your backend server for exchange with an access token
            }
        } catch (ApiException e) {
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
        }
    }

}