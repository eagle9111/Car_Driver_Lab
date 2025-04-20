package com.eagle.cardriverlab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CarDetails extends AppCompatActivity {
    ImageView carImageModel;
    ImageButton backButton;
    TextView carModelName,carAcceleration,carDeceleration, carMaxSpeed , carYear , carPrice , carEngineType;
    Button viewMoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_car_details);

        String carName = getIntent().getStringExtra("CARNAME");
        int maxSpeed = getIntent().getIntExtra("MAXSPEED", 0);
        int accelerationPerSecond = getIntent().getIntExtra("ACCELERATION", 0);
        int decelerationPerSecond = getIntent().getIntExtra("DECELERATION", 0);
        int year = getIntent().getIntExtra("YEAR", 0);
        int carprice = getIntent().getIntExtra("PRICE", 0);
        String enginetype = getIntent().getStringExtra("ENGINETYPE");
        int carImage = getIntent().getIntExtra("IMAGE", 0);



        carImageModel = findViewById(R.id.carImage);
        carModelName = findViewById(R.id.carName);
        carAcceleration = findViewById(R.id.carAcceleration);
        carDeceleration = findViewById(R.id.carDeceleration);
        carMaxSpeed = findViewById(R.id.carMaxSpeed);
        backButton = findViewById(R.id.btnBack);
        carYear = findViewById(R.id.caryear);
        carPrice = findViewById(R.id.carPrice);
        carEngineType = findViewById(R.id.carEngineType);
        viewMoreButton = findViewById(R.id.readmore);



        carImageModel.setImageResource(carImage);
        carModelName.setText(carName);
        carAcceleration.setText("" + accelerationPerSecond + " m/s");
        carDeceleration.setText("" + decelerationPerSecond + " m/s");
        carMaxSpeed.setText("" + maxSpeed + " km/h");
        carYear.setText("" + year);
        carPrice.setText("" + carprice + " $");
        carEngineType.setText("" + enginetype);


        backButton.setOnClickListener(v->{
            finish();
        });
        viewMoreButton.setOnClickListener(v -> {
            String query = carModelName.getText().toString();
            String url = "https://www.google.com/search?q=" + query;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}