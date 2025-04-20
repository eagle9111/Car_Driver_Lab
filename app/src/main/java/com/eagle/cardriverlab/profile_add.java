package com.eagle.cardriverlab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class profile_add extends AppCompatActivity {
    private static final String TAG = "ProfileAddActivity";

    private ImageView imageViewCar;
    private EditText editTextCarName, editTextAcceleration, editTextDeceleration, editTextMaxSpeed;
    private Button btnSelectImage, btnSubmit;
    private Uri imageUri;

    private FirebaseAuth firebaseAuth;

    ImageButton home , setting , add ;
    private DatabaseReference databaseReference;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_add);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI elements
        imageViewCar = findViewById(R.id.imageViewCar);
        editTextCarName = findViewById(R.id.editTextCarName);
        editTextAcceleration = findViewById(R.id.editTextAcceleration);
        editTextDeceleration = findViewById(R.id.editTextDeceleration);
        editTextMaxSpeed = findViewById(R.id.editTextMaxSpeed);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        home = findViewById(R.id.btn_home);
        setting = findViewById(R.id.btn_settings);
        add = findViewById(R.id.btn_add);

        home.setOnClickListener(v->{
            Intent i = new Intent(profile_add.this , profile_main.class);
            startActivity(i);
        });

        add.setOnClickListener(v->{
            Intent i = new Intent(profile_add.this , profile_add.class);
            startActivity(i);
        });

        setting.setOnClickListener(v->{
            Intent i = new Intent(profile_add.this , profile_auth.class);
            startActivity(i);
        });

        // Image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            imageViewCar.setImageURI(imageUri);
                        }
                    }
                }
        );

        // Set click listeners
        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnSubmit.setOnClickListener(v -> submitVehicleData());

        // Load previously saved image
        loadVehicleData();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void submitVehicleData() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        String carName = editTextCarName.getText().toString().trim();
        String acceleration = editTextAcceleration.getText().toString().trim();
        String deceleration = editTextDeceleration.getText().toString().trim();
        String maxSpeed = editTextMaxSpeed.getText().toString().trim();

        if (carName.isEmpty() || acceleration.isEmpty() || deceleration.isEmpty() || maxSpeed.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            convertImageToBase64(currentUser, carName, acceleration, deceleration, maxSpeed);
        } else {
            saveVehicleData(currentUser, carName, null, acceleration, deceleration, maxSpeed);
        }
    }

    private void convertImageToBase64(FirebaseUser user, String carName, String acceleration,
                                      String deceleration, String maxSpeed) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

            saveVehicleData(user, carName, encodedImage, acceleration, deceleration, maxSpeed);
        } catch (IOException e) {
            Log.e(TAG, "Image conversion error", e);
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveVehicleData(FirebaseUser user, String carName, String imageBase64,
                                 String acceleration, String deceleration, String maxSpeed) {
        Map<String, Object> vehicleData = new HashMap<>();
        vehicleData.put("carName", carName);
        vehicleData.put("photoBase64", imageBase64);
        vehicleData.put("acceleration", acceleration);
        vehicleData.put("deceleration", deceleration);
        vehicleData.put("maxSpeed", maxSpeed);

        databaseReference.child(user.getUid()).child("vehicles").child(carName)
                .setValue(vehicleData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Vehicle data saved successfully", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving vehicle data", e);
                    Toast.makeText(this, "Failed to save vehicle data", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadVehicleData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return;

        DatabaseReference vehicleRef = databaseReference.child(user.getUid()).child("vehicles");
        vehicleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot vehicleSnapshot : snapshot.getChildren()) {
                    String encodedImage = vehicleSnapshot.child("photoBase64").getValue(String.class);
                    if (encodedImage != null) {
                        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        imageViewCar.setImageBitmap(bitmap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profile_add.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
