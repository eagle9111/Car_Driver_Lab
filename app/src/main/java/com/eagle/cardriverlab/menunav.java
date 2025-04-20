package com.eagle.cardriverlab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class menunav extends AppCompatActivity {
    TextView menu1, menu2, menu3, menu4, menu5, menu6, menu7;
    ImageButton button;
    LinearLayout adminLayout;
    private FirebaseAuth mAuth;
    private static final String ADMIN_EMAIL = "alhassan.khalilnew@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menunav);

        // get firebase instance for auth (admin pannel)
        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        checkAdminStatus();
        setupClickListeners();
    }

    private void initializeViews() {
        menu1 = findViewById(R.id.menuOption1);
        menu2 = findViewById(R.id.menuOption2);
        menu3 = findViewById(R.id.menuOption3);
        menu4 = findViewById(R.id.menuOption4);
        menu5 = findViewById(R.id.menuOption5);
        menu6 = findViewById(R.id.menuOption6);
        menu7 = findViewById(R.id.menuOption7);
        adminLayout = findViewById(R.id.admin);
    }

    private void checkAdminStatus() {
        //check if current user is an admin or not
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            if (currentUser.getEmail().equals(ADMIN_EMAIL)) {
                adminLayout.setVisibility(View.VISIBLE);
                menu7.setVisibility(View.VISIBLE);  
            } else {
                adminLayout.setVisibility(View.GONE);
                menu7.setVisibility(View.GONE);
            }
        } else {
            adminLayout.setVisibility(View.GONE);
            menu7.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        menu1.setOnClickListener(v -> {
            Intent i = new Intent(menunav.this, MainActivity.class);
            startActivity(i);
        });

        menu2.setOnClickListener(v -> {
            Intent i = new Intent(menunav.this, CarModels.class);
            startActivity(i);
        });

        menu3.setOnClickListener(v -> {
            Intent i = new Intent(menunav.this, CreateCar.class);
            startActivity(i);
        });

        menu4.setOnClickListener(v -> {
            Intent i = new Intent(menunav.this, Settings.class);
            startActivity(i);
        });

        menu5.setOnClickListener(v -> {
            Intent i = new Intent(menunav.this, profile_auth.class);
            startActivity(i);
        });

        menu6.setOnClickListener(v -> {
            Intent i = new Intent(menunav.this, foryou.class);
            startActivity(i);
        });

        menu7.setOnClickListener(v -> {
            // Double check admin status before allowing access
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null && currentUser.getEmail() != null &&
                    currentUser.getEmail().equals(ADMIN_EMAIL)) {
                Intent i = new Intent(menunav.this, AdminPannel.class);
                startActivity(i);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check admin status again when returning to the menu
        checkAdminStatus();
    }
}