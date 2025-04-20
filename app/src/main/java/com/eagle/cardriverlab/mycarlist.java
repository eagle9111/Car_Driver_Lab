package com.eagle.cardriverlab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.HashMap;

public class mycarlist extends AppCompatActivity {
    private ListView listView;
    private ImageButton btnBack;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mycarlist);

        listView = findViewById(R.id.ListView);
        btnBack = findViewById(R.id.btnBack);
        dbHelper = new DataBaseHelper(this);

        // Set up the back button
        btnBack.setOnClickListener(v -> finish());

        // Fetch the list of cars from the database
        List<HashMap<String, String>> carList = dbHelper.getAllCars();

        // Set the adapter to display cars
        CarListAdapter adapter = new CarListAdapter(this, carList);
        listView.setAdapter(adapter);

        // Handle window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Custom Adapter to bind car data to the ListView
    public class CarListAdapter extends BaseAdapter {
        private Context context;
        private List<HashMap<String, String>> carList;

        public CarListAdapter(Context context, List<HashMap<String, String>> carList) {
            this.context = context;
            this.carList = carList;
        }

        @Override
        public int getCount() {
            return carList.size();
        }

        @Override
        public Object getItem(int position) {
            return carList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.mycarlist, parent, false);
            }

            HashMap<String, String> car = carList.get(position);

            TextView carName = convertView.findViewById(R.id.carName);
            TextView maxSpeed = convertView.findViewById(R.id.MaxSpeed);
            ImageButton btnSelectCar = convertView.findViewById(R.id.btnSelectCar);
            ImageButton btndeltetCar = convertView.findViewById(R.id.btndeltetCar);

            carName.setText(car.get("carName"));
            maxSpeed.setText(car.get("MaxSpeed"));

            btnSelectCar.setOnClickListener(v -> {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("CARNAME", car.get("carName"));
                intent.putExtra("MAXSPEED", car.get("MaxSpeed")); // Pass as string
                intent.putExtra("ACCELERATION", car.get("Acc")); // Pass as string
                intent.putExtra("DECELERATION", car.get("dec")); // Pass as string
                startActivity(intent);
            });
            btndeltetCar.setOnClickListener(v -> {
                // Delete the car from the database
                dbHelper.deleteCar(car.get("carName"));

                // Remove the car from the list
                carList.remove(position);

                // Notify the adapter that the data has changed
                notifyDataSetChanged();

                // Optional: Show a toast message to confirm deletion
                Toast.makeText(context, "Car deleted successfully", Toast.LENGTH_SHORT).show();
            });


            return convertView;
        }
    }
}
