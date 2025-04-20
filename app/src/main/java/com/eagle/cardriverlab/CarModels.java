package com.eagle.cardriverlab;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class CarModels extends AppCompatActivity {

    private GridView gridView;
    private ImageButton menuButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_models);

        gridView = findViewById(R.id.gridView);
        menuButton = findViewById(R.id.ibtnHamMenu);


            menuButton.setOnClickListener(v ->{
                Intent i = new Intent(CarModels.this , menunav.class);
                startActivity(i);
            });


        // Add Categories
        List<String> brands = Arrays.asList("Mercedes", "BMW", "Ferrari", "Lamborghini","Audi");
        int[] getCarModel = {R.drawable.mercedes, R.drawable.bmw, R.drawable.ferari, R.drawable.lambo,R.drawable.audi};

        CustomAdapter adapter = new CustomAdapter(this, getCarModel, brands);
        gridView.setAdapter(adapter);
    }

    public class CustomAdapter extends BaseAdapter {
        private Context context;
        private int[] images;
        private List<String> brands;

        public CustomAdapter(Context context, int[] images, List<String> brands) {
            this.context = context;
            this.images = images;
            this.brands = brands;
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return brands.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.grid_layout_car_model, parent, false);
            }

            ImageButton imageButton = convertView.findViewById(R.id.imageview);
            imageButton.setImageResource(images[position]);

            imageButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, CarList.class);
                intent.putExtra("BRAND", brands.get(position));
                context.startActivity(intent);
            });

            return convertView;
        }
    }

}
