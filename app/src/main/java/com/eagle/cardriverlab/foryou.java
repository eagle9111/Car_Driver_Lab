package com.eagle.cardriverlab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class foryou extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ForYouAdapter postAdapter;
    private List<ForYouModel> postList;
    private ProgressBar progressBar;
    ImageButton menu;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foryou);
        FirebaseApp.initializeApp(this);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI elements
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        menu = findViewById(R.id.ibtnHamMenu);

        // Initialize GridLayoutManager with 1 column
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Initialize post list
        postList = new ArrayList<>();
        postAdapter = new ForYouAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);

        // Load posts for the "For You" page
        loadUserPosts();

        menu.setOnClickListener(v -> {
            Intent intent = new Intent(foryou.this, menunav.class);
            startActivity(intent);
        });
    }

    private void loadUserPosts() {
        progressBar.setVisibility(View.VISIBLE);

        // Fetch data from the root "users" node
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                postList.clear(); // Clear the existing list

                // Iterate through all users
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    // Check if the user has a "vehicles" node
                    if (userSnapshot.hasChild("vehicles")) {
                        DataSnapshot vehiclesSnapshot = userSnapshot.child("vehicles");

                        // Iterate through all vehicles of the current user
                        for (DataSnapshot vehicleSnapshot : vehiclesSnapshot.getChildren()) {
                            String carName = vehicleSnapshot.child("carName").getValue(String.class);
                            String encodedImage = vehicleSnapshot.child("photoBase64").getValue(String.class);
                            String acceleration = vehicleSnapshot.child("acceleration").getValue(String.class);
                            String deceleration = vehicleSnapshot.child("deceleration").getValue(String.class);
                            String maxSpeed = vehicleSnapshot.child("maxSpeed").getValue(String.class);

                            // Handle null values
                            if (carName == null) carName = "Unknown";
                            if (acceleration == null) acceleration = "0";
                            if (deceleration == null) deceleration = "0";
                            if (maxSpeed == null) maxSpeed = "0";

                            // Convert base64 to bitmap
                            Bitmap imageBitmap = null;
                            if (encodedImage != null) {
                                try {
                                    byte[] decodedBytes = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
                                    imageBitmap = android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            // Create ForYouModel and add to the list
                            ForYouModel post = new ForYouModel(carName, imageBitmap, acceleration, deceleration, maxSpeed);
                            postList.add(post);
                        }
                    }
                }

                // Notify the adapter that data has changed
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(foryou.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
            }
        });
    }
}