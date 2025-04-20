package com.eagle.cardriverlab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class profile_main extends AppCompatActivity {
    private static final String TAG = "ProfileMainActivity";

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<PostModel> postList;
    private ProgressBar progressBar;

    ImageButton add ,home ,setting , menu;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);


        add = findViewById(R.id.btn_add);
        home = findViewById(R.id.btn_home);
        setting = findViewById(R.id.btn_settings);
        menu = findViewById(R.id.ibtnHamMenu);
        menu.setOnClickListener(v->{
            Intent intent = new Intent(profile_main.this,menunav.class);
            startActivity(intent);
        });

        add.setOnClickListener(v->{
            Intent intent = new Intent(profile_main.this,profile_add.class);
            startActivity(intent);
        });
        home.setOnClickListener(v->{
            Intent intent = new Intent(profile_main.this,profile_main.class);
            startActivity(intent);
        });
        setting.setOnClickListener(v->{
            Intent intent = new Intent(profile_main.this,profile_auth.class);
            startActivity(intent);

                });


        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI elements
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize post list
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);

        // Load user posts
        loadUserPosts();
    }

    private void loadUserPosts() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference postsRef = databaseReference.child(user.getUid()).child("vehicles");

        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String carName = postSnapshot.child("carName").getValue(String.class);
                    String encodedImage = postSnapshot.child("photoBase64").getValue(String.class);
                    String acceleration = postSnapshot.child("acceleration").getValue(String.class);
                    String deceleration = postSnapshot.child("deceleration").getValue(String.class);
                    String maxSpeed = postSnapshot.child("maxSpeed").getValue(String.class);

                    // Decode image if it's available
                    Bitmap imageBitmap = null;
                    if (encodedImage != null) {
                        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
                        imageBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    }

                    // Create a PostModel with the userId
                    PostModel post = new PostModel(user.getUid(), carName, imageBitmap, acceleration, deceleration, maxSpeed);
                    postList.add(post);
                }

                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(profile_main.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading posts", error.toException());
            }
        });
    }
}
