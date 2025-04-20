package com.eagle.cardriverlab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class profile_auth extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int RC_SIGN_IN = 9001;

    private TextView nameTextView;
    private TextView emailTextView;

    ImageButton home , add , setting ;
    private Button googleLoginButton;
    private Button signOutButton;

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_auth);

        nameTextView = findViewById(R.id.name);
        emailTextView = findViewById(R.id.email);
        googleLoginButton = findViewById(R.id.googlelogin);
        signOutButton = findViewById(R.id.signoutbutton);
        home = findViewById(R.id.btn_home);
        add = findViewById(R.id.btn_add);
        setting = findViewById(R.id.btn_settings);

        home.setOnClickListener(v->{
            Intent i = new Intent(profile_auth.this , profile_main.class);
            startActivity(i);
        });
        add.setOnClickListener(v->{
            Intent i = new Intent(profile_auth.this , profile_add.class);
            startActivity(i);
        });
        setting.setOnClickListener(v->{
            Intent i = new Intent(profile_auth.this , profile_auth.class);
            startActivity(i);
        });

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set click listeners
        googleLoginButton.setOnClickListener(v -> signIn());
        signOutButton.setOnClickListener(v -> signOut());

        // Check if user is already signed in
        updateUI(firebaseAuth.getCurrentUser());
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Sign out from Firebase
        firebaseAuth.signOut();

        // Sign out from Google
        googleSignInClient.signOut().addOnCompleteListener(this,
                task -> {
                    updateUI(null);
                    Toast.makeText(profile_auth.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e(TAG, "Google sign in failed code: " + e.getStatusCode());
                Log.e(TAG, "Error message: " + e.getMessage());
                Log.e(TAG, "Stack trace: ", e);
                Toast.makeText(this, "Google Sign In failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(profile_auth.this, "Authentication failed",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            nameTextView.setText(user.getDisplayName());
            emailTextView.setText(user.getEmail());
            googleLoginButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
        } else {
            nameTextView.setText("Welcome");
            emailTextView.setText("");
            googleLoginButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }
}