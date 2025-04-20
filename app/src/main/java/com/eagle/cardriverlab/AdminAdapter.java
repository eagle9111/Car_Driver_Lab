package com.eagle.cardriverlab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.PostViewHolder> {
    private List<ForYouModel> postList;
    private Context context;

    public AdminAdapter(Context context, List<ForYouModel> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        ForYouModel post = postList.get(position);
        holder.textCarName.setText(post.getCarName());
        holder.textAcceleration.setText("Acceleration: " + post.getAcceleration() + " m/s");
        holder.textDeceleration.setText("Deceleration: " + post.getDeceleration() + " m/s");
        holder.textMaxSpeed.setText("Max Speed: " + post.getMaxSpeed() + " km/h");

        if (post.getImageBitmap() != null) {
            holder.imageViewCar.setImageBitmap(post.getImageBitmap());
        } else {
            holder.imageViewCar.setImageResource(R.drawable.car1);
        }

        holder.btnDeletePost.setOnClickListener(v -> deletePost(post, position));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private void deletePost(ForYouModel post, int position) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        // First, find the user that owns this vehicle
        usersRef.get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                if (userSnapshot.hasChild("vehicles")) {
                    DataSnapshot vehiclesSnapshot = userSnapshot.child("vehicles");
                    for (DataSnapshot vehicleSnapshot : vehiclesSnapshot.getChildren()) {
                        String carName = vehicleSnapshot.child("carName").getValue(String.class);
                        if (carName != null && carName.equals(post.getCarName())) {
                            // Found the matching vehicle, now delete it
                            vehicleSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        postList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, postList.size());
                                        Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to delete post: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                            return; // Exit after finding and deleting the vehicle
                        }
                    }
                }
            }
            Toast.makeText(context, "Vehicle not found", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Error searching for vehicle: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textCarName, textAcceleration, textDeceleration, textMaxSpeed;
        ImageView imageViewCar;
        Button btnDeletePost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textCarName = itemView.findViewById(R.id.textCarName);
            textAcceleration = itemView.findViewById(R.id.textAcceleration);
            textDeceleration = itemView.findViewById(R.id.textDeceleration);
            textMaxSpeed = itemView.findViewById(R.id.textMaxSpeed);
            imageViewCar = itemView.findViewById(R.id.imageViewCar);
            btnDeletePost = itemView.findViewById(R.id.btnDeletePost);
        }
    }
}