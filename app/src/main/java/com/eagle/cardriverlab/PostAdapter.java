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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<PostModel> postList;
    private Context context;

    public PostAdapter(Context context, List<PostModel> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostModel post = postList.get(position);
        holder.textCarName.setText(post.getCarName());
        holder.textAcceleration.setText("Acceleration: " + post.getAcceleration() + " m/s²");
        holder.textDeceleration.setText("Deceleration: " + post.getDeceleration() + " m/s²");
        holder.textMaxSpeed.setText("Max Speed: " + post.getMaxSpeed() + " km/h");

        if (post.getImageBitmap() != null) {
            holder.imageViewCar.setImageBitmap(post.getImageBitmap());
        }

        // Set up the delete button to delete the post
        holder.btnDeletePost.setOnClickListener(v -> {
            // Call delete method with the current post and its position
            deletePost(post, position);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private void deletePost(PostModel post, int position) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postsRef = database.getReference("users")
                .child(post.getUserId())
                .child("vehicles")
                .child(post.getCarName());

        // Delete the post from Firebase
        postsRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    postList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
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

