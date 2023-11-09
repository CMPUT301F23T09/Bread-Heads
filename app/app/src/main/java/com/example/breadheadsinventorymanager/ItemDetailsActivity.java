package com.example.breadheadsinventorymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class ItemDetailsActivity extends AppCompatActivity {

    private int currentImageIndex = 0; // is this passed by value into the shuffleImage function?
    private FirestoreInteract database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Item selectedItem = (Item) getIntent().getSerializableExtra("item");

        TextView dateText = findViewById(R.id.dateText);
        dateText.setText(selectedItem != null ? selectedItem.getDate() : null);

        TextView modeText = findViewById(R.id.modelText);
        modeText.setText(Objects.requireNonNull(selectedItem).getModel());

        TextView itemDescription = findViewById(R.id.itemDescription);
        itemDescription.setText(selectedItem.getDescription());

        TextView makeText = findViewById(R.id.makeText);
        makeText.setText(selectedItem.getMake());

        TextView commentText = findViewById(R.id.commentText);
        commentText.setText(selectedItem.getComment());

        TextView valueText = findViewById(R.id.valueText);
        valueText.setText(selectedItem.getValueDollarString());

        ImageView itemImage = findViewById(R.id.itemImage);
        database = new FirestoreInteract();

        if (!selectedItem.getImagePaths().isEmpty()) { // If the item's list of image paths is initialized and non-empty
            String firstImagePath = selectedItem.getImagePaths().get(currentImageIndex);
            updateImage(firstImagePath, itemImage);
        }

        ImageButton prevBtn = findViewById(R.id.previousButton), nextBtn = findViewById(R.id.nextButton);
        prevBtn.setOnClickListener(new View.OnClickListener() { // LEFT
            @Override
            public void onClick(View v) {
                if (!selectedItem.getImagePaths().isEmpty()) {
                    String imagePath = findShuffledImagePath(selectedItem.getImagePaths(), -1);
                    updateImage(imagePath, itemImage);
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() { // RIGHT
            @Override
            public void onClick(View v) {
                if (!selectedItem.getImagePaths().isEmpty()) {
                    String imagePath = findShuffledImagePath(selectedItem.getImagePaths(), 1);
                    updateImage(imagePath, itemImage);
                }
            }
        });
    }

    /**
     * Gets the new image reference path from an array of paths after shifting left or right by 1
     * @param imagePaths
     * @param shiftDirection
     * @return The Bitmap of the new image
     */
    private String findShuffledImagePath(ArrayList<String> imagePaths, int shiftDirection) {
        if (shiftDirection > 0) { // Shift right
            if (currentImageIndex == imagePaths.size() - 1) {
                currentImageIndex = 0;
            }
            else {
                currentImageIndex += 1;
            }
        }
        else if (shiftDirection < 0) { // Shift left
            if (currentImageIndex == 0) {
                currentImageIndex = imagePaths.size() - 1;
            }
            else {
                currentImageIndex -= 1;
            }
        }
        return imagePaths.get(currentImageIndex);
    }

    /**
     * Updates the item image view with shuffled image
     * @param imagePath
     */
    private void updateImage(String imagePath, ImageView itemImage) {
        StorageReference imageRef = database.getStorageReference().child(imagePath);

        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                itemImage.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(findViewById(android.R.id.content).getRootView().getContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_details_topbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_item) {
            // Handle the Edit button click
            return true;
        } else if (id == R.id.delete_item) {
            // Handle the Delete button click
            return true;
        } else if (id == android.R.id.home) {
            // Handle the Up button (back button) click
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}