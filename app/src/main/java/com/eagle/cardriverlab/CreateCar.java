package com.eagle.cardriverlab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateCar extends AppCompatActivity {

    private EditText carNameEditText, maxSpeedEditText, accelerationEditText, brakingEditText;
    private DataBaseHelper dbHelper;

    ImageButton menu ;

    Button mycars  , save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_car);

        // Initialize views
        carNameEditText = findViewById(R.id.inputCarName);
        maxSpeedEditText = findViewById(R.id.inputMaxSpeed);
        accelerationEditText = findViewById(R.id.inputAccelerate);
        brakingEditText = findViewById(R.id.inputDecelerate);
        mycars = findViewById(R.id.btnMyCars);
        menu = findViewById(R.id.ibtnHamMenu);
        save = findViewById(R.id.btnSaveCar);

        dbHelper = new DataBaseHelper(this);
        save.setOnClickListener(v->{ saveCarDetails(); });
        menu.setOnClickListener(v ->{
            Intent i = new Intent(CreateCar.this , menunav.class);
            startActivity(i);
        });
        mycars.setOnClickListener(v->{
            Intent i = new Intent(CreateCar.this , mycarlist.class);
            startActivity(i);
                }

        );

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Method to save car details
    public void saveCarDetails() {
        String carName = carNameEditText.getText().toString();
        int maxSpeed = Integer.parseInt(maxSpeedEditText.getText().toString());
        int acceleration = Integer.parseInt(accelerationEditText.getText().toString());
        int braking = Integer.parseInt(brakingEditText.getText().toString());
        if (carName.isEmpty() || maxSpeed <= 0 || acceleration <= 0 || braking <= 0 ) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }
        dbHelper.insertCar(carName, maxSpeed, acceleration, braking);
        Toast.makeText(this, "Car details saved successfully", Toast.LENGTH_SHORT).show();
        carNameEditText.setText("");
        maxSpeedEditText.setText("");
        accelerationEditText.setText("");
        brakingEditText.setText("");
    }

}
