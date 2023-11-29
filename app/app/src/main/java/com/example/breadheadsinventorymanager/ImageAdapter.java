package com.example.breadheadsinventorymanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {
    private ArrayList<String> imagePaths;
    private Context context;
    private ItemClickListener itemClickListener;
    private FirestoreInteract database;
    private boolean showCheckboxes = false;

    public ImageAdapter(Context context, ArrayList<String> imagePaths, ItemClickListener itemClickListener) {
        this.context = context;
        this.imagePaths = imagePaths;
        this.itemClickListener = itemClickListener;
        this.database = new FirestoreInteract();
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_preview, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        StorageReference imageRef = database.fetchImageReferenceFromStorage(imagePaths.get(position));
        final long TWELVE_MEGABYTE = 1024 * 1024 * 12;

        imageRef.getBytes(TWELVE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
//                Toast.makeText(findViewById(android.R.id.content).getRootView().getContext(), R.string.firebase_firestore_image_not_loaded_message, Toast.LENGTH_LONG).show();
            }
        });

        holder.updateCheckboxVisibility();
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        CheckBox imageCheckBox;
        public ImageHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imagePreview);
            imageCheckBox = view.findViewById(R.id.imageCheckBox);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (showCheckboxes) {
                        imageCheckBox.setChecked(!imageCheckBox.isChecked());
                    }
                    itemClickListener.onItemClick(imagePaths.get(position), position);
                }
            }
        }

        void updateCheckboxVisibility() {
            if (showCheckboxes) {
                imageCheckBox.setVisibility(View.VISIBLE);
            }
            else {
                imageCheckBox.setChecked(false);
                imageCheckBox.setVisibility(View.GONE);
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(String imagePath, int position);
    }

    public void changeCheckboxVisibility(boolean show) {
        this.showCheckboxes = show;
        notifyDataSetChanged();
    }
}
