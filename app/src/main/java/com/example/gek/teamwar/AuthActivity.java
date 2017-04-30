package com.example.gek.teamwar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.gek.teamwar.Data.Const;
import com.example.gek.teamwar.Utils.Connection;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static int RC_SIGN_IN_GOOGLE = 1;
    private static String TAG = "AUTH_ACTIVITY";
    private static String EXTRA_IS_PROGRESSBAR = "progress_bar";
    private GoogleApiClient mGoogleApiClient;

    private Toolbar myToolbar;
    private ScrollView scrollView;
    private ProgressBar progressBar;
    private Button btnGoogleSignIn, btnSignOut, btnStopService;
    private Button  btnConnectGroup;
    private EditText etName;
    private EditText etPasswordGroup;
    private Boolean isProgressBarShow = false;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        findAllView();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addOnConnectionFailedListener(this)
                .build();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        etName.setText(mSharedPreferences.getString(Const.SETTINGS_NAME, ""));
        etPasswordGroup.setText(mSharedPreferences.getString(Const.SETTINGS_PASS, ""));
    }

    private void findAllView() {
        myToolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(myToolbar);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnGoogleSignIn = (Button) findViewById(R.id.btnGoogleSignIn);
        btnSignOut = (Button) findViewById(R.id.btnSignOut);
        btnConnectGroup = (Button) findViewById(R.id.btnConnectGroup);
        btnStopService = (Button) findViewById(R.id.btnStopService);

        etName = (EditText) findViewById(R.id.etName);
        etPasswordGroup = (EditText) findViewById(R.id.etPasswordGroup);

        btnGoogleSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnConnectGroup.setOnClickListener(this);
        btnStopService.setOnClickListener(this);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed.");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGoogleSignIn:
                progressBar.setVisibility(View.VISIBLE);
                isProgressBarShow = true;
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
                break;
            case R.id.btnSignOut:
                makeSignOut();
                break;
            case R.id.btnConnectGroup:
                connectToGroup();
                break;
            case R.id.btnStopService:
                stopLocationService();
                break;
        }
    }

    // auth result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseSignIn(credential);
            } else {
                Toast.makeText(getApplicationContext(), "Error auth google", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Google Login Failed " + result.getStatus().toString());
                progressBar.setVisibility(View.GONE);
                isProgressBarShow = false;
            }
        }
    }


    private void firebaseSignIn(AuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "FireBaseSignIn successful " + task.isSuccessful());
                            Toast.makeText(getBaseContext(), "Sign in successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
                        }
                        isProgressBarShow = false;
                        updateUi();
                    }
                }
            );
    }



    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }

    private void updateUi(){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            btnGoogleSignIn.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            if (Connection.getInstance(this).getServiceRunning()){
                btnStopService.setVisibility(View.VISIBLE);
                btnConnectGroup.setText(getString(R.string.action_to_map));
            } else {
                btnStopService.setVisibility(View.GONE);
                btnConnectGroup.setText(getString(R.string.action_connect));
            }
        } else {
            btnGoogleSignIn.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }
        if (isProgressBarShow){
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EXTRA_IS_PROGRESSBAR, isProgressBarShow);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(EXTRA_IS_PROGRESSBAR))){
            isProgressBarShow = savedInstanceState.getBoolean(EXTRA_IS_PROGRESSBAR);
        }
    }

    private void makeSignOut() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            Connection.getInstance(this).close();

            // TODO: 15.04.17 Stop service with location listener

            updateUi();
        }
    }

    private void connectToGroup(){
        if ((etName.getText().length() > 2) && (etPasswordGroup.getText().length() > 2)){
            Connection.getInstance(this).setUserName(etName.getText().toString());
            Connection.getInstance(this).setGroupPassword(etPasswordGroup.getText().toString());
            Connection.getInstance(this).setUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            mSharedPreferences.edit().putString(Const.SETTINGS_NAME, etName.getText().toString()).apply();
            mSharedPreferences.edit().putString(Const.SETTINGS_PASS, etPasswordGroup.getText().toString()).apply();
            mSharedPreferences.edit().putString(Const.SETTINGS_EMAIL, Connection.getInstance(this).getUserEmail()).apply();
            startActivity(new Intent(this, MapActivity.class));
        }
    }

    private void stopLocationService(){
        stopService(new Intent(this, LocationService.class));
        Connection.getInstance(this).setServiceRunning(false);
        Log.d(TAG, "stopLocationService: setServiceRunning - false");
        updateUi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ab_settings:
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                break;
            case R.id.ab_about:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
