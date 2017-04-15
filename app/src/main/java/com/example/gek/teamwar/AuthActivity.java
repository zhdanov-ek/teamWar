package com.example.gek.teamwar;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gek.teamwar.Data.Group;
import com.example.gek.teamwar.Utils.Connection;
import com.example.gek.teamwar.Utils.FbHelper;
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

public class AuthActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static int RC_SIGN_IN_GOOGLE = 1;
    private static String TAG = "AUTH_ACTIVITY";
    private static String EXTRA_IS_PROGRESSBAR = "progress_bar";
    private GoogleApiClient mGoogleApiClient;

    private ScrollView scrollView;
    private ProgressBar progressBar;
    private Button btnGoogleSignIn, btnSignOut;
    private EditText etName;
    private Button btnCreateGroup, btnConnectGroup;
    private EditText etNameNewGroup, etDescriptionNewGroup, etPasswordNewGroup;
    private EditText etPasswordGroup;
    private RadioGroup rgGroups;
    private Spinner spinnerGroups;
    private RadioButton rbCrateGroup, rbChooseGroup;
    private Boolean isProgressBarShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        findAllView();
        chooseNewGroup(true);

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

    }

    private void findAllView() {
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnGoogleSignIn = (Button) findViewById(R.id.btnGoogleSignIn);
        btnSignOut = (Button) findViewById(R.id.btnSignOut);
        btnCreateGroup = (Button) findViewById(R.id.btnCreateGroup);
        btnConnectGroup = (Button) findViewById(R.id.btnConnectGroup);

        etName = (EditText) findViewById(R.id.etName);
        etNameNewGroup = (EditText) findViewById(R.id.etNameNewGroup);
        etDescriptionNewGroup = (EditText) findViewById(R.id.etDescriptionNewGroup);
        etPasswordNewGroup = (EditText) findViewById(R.id.etPasswordNewGroup);
        etPasswordGroup = (EditText) findViewById(R.id.etPasswordGroup);

        rgGroups = (RadioGroup) findViewById(R.id.rgGroups);
        rbCrateGroup = (RadioButton) findViewById(R.id.rbCrateGroup);
        rbChooseGroup = (RadioButton) findViewById(R.id.rbChooseGroup);
        spinnerGroups = (Spinner) findViewById(R.id.spinnerGroups);

        btnGoogleSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnConnectGroup.setOnClickListener(this);
        btnCreateGroup.setOnClickListener(this);
        rbCrateGroup.setOnClickListener(View -> chooseNewGroup(true));
        rbChooseGroup.setOnClickListener(View -> chooseNewGroup(false));

    }

    private void chooseNewGroup(Boolean isCreateGroup) {
        etNameNewGroup.setEnabled(isCreateGroup);
        etDescriptionNewGroup.setEnabled(isCreateGroup);
        etPasswordNewGroup.setEnabled(isCreateGroup);
        btnCreateGroup.setEnabled(isCreateGroup);
        spinnerGroups.setEnabled(!isCreateGroup);
        btnConnectGroup.setEnabled(!isCreateGroup);
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
                break;
            case R.id.btnCreateGroup:
                createNewGroup();
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
                            Connection.getInstance().setUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
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
            // TODO: 15.04.17 Stop service with location listener
            updateUi();
        }
    }

    private void createNewGroup(){
        Group group = new Group();
        if ((etNameNewGroup.getText().length() == 0) ||
                (etPasswordNewGroup.getText().length() == 0)){
            Toast.makeText(this, "Fill name of group and password!", Toast.LENGTH_LONG).show();
        } else {
            group.setName(etNameNewGroup.getText().toString());
            group.setPassword(etPasswordNewGroup.getText().toString());
            group.setDescription(etDescriptionNewGroup.getText().toString());
            group.setEmailOwner(Connection.getInstance().getUserEmail());
            FbHelper.createGroup(group);
        }
    }
}