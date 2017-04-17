package com.example.gek.teamwar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.gek.teamwar.Utils.Connection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by gek on 17.04.17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected FirebaseAuth.AuthStateListener authListener;

    public abstract void changeAuth();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    Connection.getInstance().setUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                }
            }
        };
    }
}
