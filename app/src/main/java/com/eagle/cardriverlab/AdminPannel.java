package com.eagle.cardriverlab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminPannel extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AdminAdapter adminAdapter;
    private List<ForYouModel> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_pannel);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        // Initialize postList
        postList = new ArrayList<>();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adminAdapter = new AdminAdapter(this, postList);
        recyclerView.setAdapter(adminAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.ibtnHamMenu).setOnClickListener(v -> {
            Intent intent = new Intent(AdminPannel.this, menunav.class);
            startActivity(intent);
        });

        loadData();
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        postList.clear(); // Clear existing data

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

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
                adminAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminPannel.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}