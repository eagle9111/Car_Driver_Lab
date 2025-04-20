package com.eagle.cardriverlab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ForYouAdapter extends RecyclerView.Adapter<ForYouAdapter.ForYouViewHolder> {
    private Context context; // Context for starting activities and accessing resources
    private List<ForYouModel> postList; // List of posts to display
    private DataBaseHelper dbHelper; // Database helper for saving car data

    // Constructor
    public ForYouAdapter(Context context, List<ForYouModel> postList) {
        this.context = context;
        this.postList = postList != null ? postList : new ArrayList<>(); // Ensure postList is not null
        this.dbHelper = new DataBaseHelper(context); // Initialize the database helper
    }

    @Override
    public ForYouViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.foryou_items, parent, false);
        return new ForYouViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForYouViewHolder holder, int position) {
        // Get the current post
        ForYouModel post = postList.get(position);

        // Safely bind data to views
        holder.textCarName.setText(post.getCarName() != null ? post.getCarName() : "Unknown");
        holder.textAcceleration.setText("Acceleration: " + (post.getAcceleration() != null ? post.getAcceleration() : "0") + " m/s²");
        holder.textDeceleration.setText("Deceleration: " + (post.getDeceleration() != null ? post.getDeceleration() : "0") + " m/s²");
        holder.textMaxSpeed.setText("Max Speed: " + (post.getMaxSpeed() != null ? post.getMaxSpeed() : "0") + " km/h");

        // Set the car image if available
        if (post.getImageBitmap() != null) {
            holder.imageViewCar.setImageBitmap(post.getImageBitmap());
        }

        holder.button1.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("CARNAME", post.getCarName());
            intent.putExtra("ACCELERATION", post.getAcceleration());
            intent.putExtra("DECELERATION", post.getDeceleration());
            intent.putExtra("MAXSPEED", post.getMaxSpeed());
            context.startActivity(intent);
        });

        // Handle button 2 click (Save car data to database)
        holder.button2.setOnClickListener(v -> {
            String carName = post.getCarName();
            String maxSpeedStr = post.getMaxSpeed() != null ? post.getMaxSpeed() : "0";
            String accelerationStr = post.getAcceleration() != null ? post.getAcceleration() : "0";
            String decelerationStr = post.getDeceleration() != null ? post.getDeceleration() : "0";

            // Convert strings to integers
            int maxSpeed = Integer.parseInt(maxSpeedStr);
            int acceleration = Integer.parseInt(accelerationStr);
            int deceleration = Integer.parseInt(decelerationStr);

            // Validate input
            if (carName.isEmpty() || maxSpeed <= 0 || acceleration <= 0 || deceleration <= 0) {
                Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save car data to the database
            dbHelper.insertCar(carName, maxSpeed, acceleration, deceleration);
            Toast.makeText(context, "Car data saved successfully", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return postList.size(); // Return the number of items in the list
    }

    // ViewHolder class to hold references to views
    public static class ForYouViewHolder extends RecyclerView.ViewHolder {
        TextView textCarName, textAcceleration, textDeceleration, textMaxSpeed;
        ImageView imageViewCar;
        Button button1, button2;

        public ForYouViewHolder(View itemView) {
            super(itemView);
            // Initialize views
            textCarName = itemView.findViewById(R.id.textCarName);
            textAcceleration = itemView.findViewById(R.id.textAcceleration);
            textDeceleration = itemView.findViewById(R.id.textDeceleration);
            textMaxSpeed = itemView.findViewById(R.id.textMaxSpeed);
            imageViewCar = itemView.findViewById(R.id.imageViewCar);
            button1 = itemView.findViewById(R.id.button1);
            button2 = itemView.findViewById(R.id.button2);
        }
    }
}