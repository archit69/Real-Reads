package com.vizy.newsapp.realread.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.vizy.newsapp.realread.R;
import com.vizy.newsapp.realread.model.UserSession;

import java.util.HashMap;

public class SignIn extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    Button signIn, noAccount;
    EditText mobileNo, password;
    GoogleApiClient mGoogleApiClient;
    ProgressDialog mProgressDialog;
    private static final int RC_SIGN_IN = 9001;
    String TAG="MainActivity-Google+SignIn";
    GoogleSignInAccount acct;

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final UserSession session=new UserSession(getApplicationContext());

        signIn=(Button)findViewById(R.id.sign_in);
        noAccount=(Button)findViewById(R.id.no_account);
        mobileNo=(EditText)findViewById(R.id.enter_mobile_no_login);
        password=(EditText)findViewById(R.id.enter_password_login);

        /*signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber=mobileNo.getText().toString();
                String pass=password.getText().toString();
                HashMap<String, String> user = session.getUserDetails();
                if(phoneNumber==user.get(UserSession.KEY_PHONE)&&pass==user.get(UserSession.KEY_PASSWORD)){
                    Intent toHome=new Intent(SignIn.this, MainActivity.class);
                    startActivity(toHome);
                    finish();
                }
            }
        });*/

        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toSignUp=new Intent(SignIn.this, SignUp.class);
                startActivity(toSignUp);
                finish();
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(this);


        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

      /*  mGoogleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .addScope(Scopes.PLUS_LOGIN).addScope(Scopes.PLUS_ME).build();*/

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && data!=null) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
            // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean b) {
        if(b){
            Log.e("Sucessfull","Login");

            String nam=acct.getDisplayName();
            String em=acct.getEmail();
            Uri image_url =acct.getPhotoUrl();

            UserSession session = new UserSession(this);
            session.createLoginSession(nam, em);

            startActivity(new Intent(SignIn.this,MainActivity.class));
            finish();
        }
        else {
            Log.e("Unsucessful","error");
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
