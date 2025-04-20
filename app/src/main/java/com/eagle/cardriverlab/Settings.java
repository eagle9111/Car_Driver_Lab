package com.eagle.cardriverlab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {
    ImageButton menu;
    Button contactEmail, contactWhatsApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        menu = findViewById(R.id.ibtnHamMenu);
        contactEmail = findViewById(R.id.contactEmail);
        contactWhatsApp = findViewById(R.id.contactWhatsApp);

        menu.setOnClickListener(v -> {
            Intent i = new Intent(Settings.this, menunav.class);
            startActivity(i);
        });

        contactEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"alhassan.khalilnew@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry");
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        });

        // Set click listener for "WhatsApp Us"
        contactWhatsApp.setOnClickListener(v -> {
            String phoneNumber = "+96181796557";
            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + phoneNumber));
            startActivity(whatsappIntent);
        });
    }
}
