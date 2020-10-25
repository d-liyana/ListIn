package com.dinu.listin;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dinu.listin.Common.Common;
import com.dinu.listin.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private ProgressBar loadingProgressBar;
    Button loginButton;

    private FirebaseAuth mAuth;
    DatabaseReference userRef;
    private FirebaseAuth.AuthStateListener listener;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.login);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loadingProgressBar = findViewById(R.id.loading);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.i("currentUser" + mAuth.getCurrentUser(), "second" + currentUser);
        init();
    }


    private void init() {
        Log.i("Amin", "init");
        userRef=FirebaseDatabase.getInstance().getReference(Common.USER_REF);

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                Log.i("user", "userCurrent " + user);
                if (user != null) {
                    Log.i("user" + user, "done");
                    checkUserFromFireBase(user.getUid());
                } else {
                    LoginExit();
                   // signIn();
                }
            }
        };
    }



    private void LoginExit(){

        Log.i("inLoginExit","LoginExit");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                Log.i("inLoginExitun "+email,"LoginExitpw "+password);

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                loadingProgressBar.setVisibility(View.VISIBLE);

                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                loadingProgressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    Log.i("TASK","COMPLETE");

                                    userRef.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Log.i("Exit", "User " + mAuth.getUid());
                                            Toast.makeText(MainActivity.this, "You have already registered", Toast.LENGTH_SHORT).show();
                                            User user = dataSnapshot.getValue(User.class);
                                            goToHomeActivity(user);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }else {
                                    Toast.makeText(MainActivity.this, "User does not exits", Toast.LENGTH_LONG).show();
                                    Common.email=usernameEditText.toString().trim();
                                    Log.i("em "+Common.email,"user "+email);
                                    Intent homeIntent = new Intent(MainActivity.this, Register.class);
                                    startActivity(homeIntent);
                                }
                                finish();
                            }
                        });
            }
        });
    }


    private void checkUserFromFireBase(String uid) {
        Log.i("FirebaseUser", "FirebaseUserId " + uid);
        Log.i("FirebaseUser", "FirebaseUserId " + userRef.child(uid));


        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {



            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("FirebaseUser", "FirebaseUserId " + dataSnapshot.exists());

                if (dataSnapshot.exists()) {
                    Log.i("Exit", "User " + uid);
                    Toast.makeText(MainActivity.this, "You have already registered", Toast.LENGTH_SHORT).show();
                    User user = dataSnapshot.getValue(User.class);
                    goToHomeActivity(user);
                }

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void goToHomeActivity(User user) {
        Log.i("in", "goToHomeActivityInMainActivity");
        Common.currentUser = user;
        startActivity(new Intent(MainActivity.this, Home.class));
        finish();

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i("in", "onstart");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.i("in", "onstart " + currentUser);

        mAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if (listener != null) {
            mAuth.removeAuthStateListener(listener);
            compositeDisposable.clear();
            super.onStop();
        }
    }
}