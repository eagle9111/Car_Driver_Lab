package com.eagle.cardriverlab;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarList extends AppCompatActivity {
    private ListView listView;
    TextView name;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);

        listView = findViewById(R.id.ListView);
        btnBack = findViewById(R.id.btnBack);
        name = findViewById(R.id.tvcarsname);




        btnBack.setOnClickListener(v -> {
           finish();
        });

        String brand = getIntent().getStringExtra("BRAND");

        name.setText(brand);

        Map<String, List<Car>> carData = getCarData();
        List<Car> selectedCars = carData.getOrDefault(brand, new ArrayList<>());

        CarListAdapter adapter = new CarListAdapter(selectedCars);
        listView.setAdapter(adapter);

    }

    private Map<String, List<Car>> getCarData() {
        Map<String, List<Car>> carData = new HashMap<>();

        carData.put("Mercedes", Arrays.asList(
                new Car("A-Class A250", 250, 6, 4, 2023, 35000, "Petrol",  R.drawable.car1  ),
                new Car("C-Class C300", 250, 7, 3, 2022, 45000, "Petrol",  R.drawable.car1),
                new Car("E-Class E350", 250, 7, 4, 2022, 60000, "Diesel",  R.drawable.car1))
        );

        carData.put("BMW", Arrays.asList(
                new Car("X5 xDrive40i", 250, 6, 5, 2023, 65000, "Petrol",  R.drawable.car1),
                new Car("X7 xDrive40i", 250, 6, 3, 2023, 75000, "Diesel", R.drawable.car1),
                new Car("Z4 M40i", 250, 5, 3, 2022, 50000, "Petrol",  R.drawable.car1))
        );

        carData.put("Ferrari", Arrays.asList(
                new Car("488 GTB", 330, 8, 5, 2021, 250000, "Petrol",   R.drawable.car1),
                new Car("F8 Tributo", 340, 9, 4, 2021, 280000, "Petrol",  R.drawable.car1))
        );

        carData.put("Lamborghini", Arrays.asList(
                new Car("Aventador S", 350, 8, 4, 2021, 400000, "Petrol",  R.drawable.car1),
                new Car("Urus", 305, 6, 3, 2022, 220000, "Petrol", R.drawable.car1))
        );

        carData.put("Audi", Arrays.asList(
                new Car("A4 45 TFSI", 240, 6, 3, 2023, 45000, "Petrol", R.drawable.car1),
                new Car("A6 55 TDI", 250, 7, 3, 2023, 60000, "Diesel", R.drawable.car1))
        );

        return carData;
    }


    private static class Car {
        private final String name;
        private final int maxSpeed;
        private final int accelerationPerSecond;
        private final int decelerationPerSecond;
        private final int year;
        private final int carPrice;
        private final String engineType;

        private final int carImage;

        public Car(String name, int maxSpeed, int accelerationPerSecond, int decelerationPerSecond, int year, int carPrice, String engineType, int carImage ) {
            this.name = name;
            this.maxSpeed = maxSpeed;
            this.accelerationPerSecond = accelerationPerSecond;
            this.decelerationPerSecond = decelerationPerSecond;
            this.year = year;
            this.carPrice = carPrice;
            this.engineType = engineType;

            this.carImage = carImage;
        }

        // Getters
        public String getName() { return name; }
        public int getMaxSpeed() { return maxSpeed; }
        public int getAccelerationPerSecond() { return accelerationPerSecond; }
        public int getDecelerationPerSecond() { return decelerationPerSecond; }
        public int getYear() { return year; }
        public int getCarPrice() { return carPrice; }
        public String getEngineType() { return engineType; }

        public int getCarImage() { return carImage; }
    }


    private class CarListAdapter extends android.widget.BaseAdapter {
        private final List<Car> cars;

        public CarListAdapter(List<Car> cars) {
            this.cars = cars;
        }

        @Override
        public int getCount() {
            return cars.size();
        }

        @Override
        public Object getItem(int position) {
            return cars.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
            if (convertView == null) {
                convertView = android.view.LayoutInflater.from(CarList.this).inflate(R.layout.carslistlayout, parent, false);
            }

            Car car = cars.get(position);

            TextView carName = convertView.findViewById(R.id.carName);
            TextView carSpeed = convertView.findViewById(R.id.MaxSpeed);
            ImageButton btnSelectCar = convertView.findViewById(R.id.btnSelectCar);
            ImageButton btnView = convertView.findViewById(R.id.btnView);

            carName.setText(car.getName());
            carSpeed.setText(car.getMaxSpeed() + " km/h");
            btnView.setOnClickListener(v -> {
                Intent intent = new Intent(CarList.this, CarDetails.class);
                intent.putExtra("CARNAME", car.getName());
                intent.putExtra("MAXSPEED", car.getMaxSpeed());
                intent.putExtra("ACCELERATION", car.getAccelerationPerSecond());
                intent.putExtra("DECELERATION", car.getDecelerationPerSecond());
                intent.putExtra("YEAR", car.getYear());
                intent.putExtra("PRICE", car.getCarPrice());
                intent.putExtra("ENGINETYPE", car.getEngineType());
                intent.putExtra("IMAGE", car.getCarImage());
                startActivity(intent);
            });
            btnSelectCar.setOnClickListener(v -> {
                Intent intent = new Intent(CarList.this, MainActivity.class);
                intent.putExtra("CARNAME", car.getName());
                intent.putExtra("MAXSPEED", car.getMaxSpeed()); // Pass as integer
                intent.putExtra("ACCELERATION", car.getAccelerationPerSecond()); // Pass as integer
                intent.putExtra("DECELERATION", car.getDecelerationPerSecond()); // Pass as integer
                startActivity(intent);
            });






            return convertView;
        }
    }
}
