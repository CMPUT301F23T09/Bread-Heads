package com.example.breadheadsinventorymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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

import java.io.Serializable;
import java.util.ArrayList;
import android.app.AlertDialog;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

/**
 * Activity to view and edit the details and attached information of an individual item.
 */
public class ItemDetailsActivity extends AppCompatActivity {
    private Item selectedItem;
    private FirestoreInteract database;
    private ImageView itemImage;
    private int currentImageIndex = 0;

    private TagList globalTagList;


    /**
     * Loads item details, display related images (if any) and sets button functionality
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        selectedItem = (Item) getIntent().getSerializableExtra("item");
        // Retrieve the serialized object from the intent
        Serializable serializableObject = getIntent().getSerializableExtra("tagList");

        // Cast to TagList
        ArrayList<Tag> globalTagArray = (ArrayList<Tag>) serializableObject;
        if (globalTagList != null) {
            globalTagList.addAll(globalTagArray);
        } else {
            globalTagList = new TagList();
            globalTagList.addAll(globalTagArray);
        }

        database = new FirestoreInteract();

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

        itemImage = findViewById(R.id.itemImage);

        if (selectedItem.getImagePaths() != null) {
            if (!selectedItem.getImagePaths().isEmpty()) { // If the item's list of image paths is initialized and non-empty
                String firstImagePath = selectedItem.getImagePaths().get(currentImageIndex);
                updateImage(firstImagePath, itemImage);
            }
            else { // Set itemImage to a default image if no images
                itemImage.setImageResource(R.drawable.default_image); //fixme:do this
            }
        }

        ImageButton prevBtn = findViewById(R.id.previousButton), nextBtn = findViewById(R.id.nextButton);
        prevBtn.setOnClickListener(new View.OnClickListener() { // LEFT
            @Override
            public void onClick(View v) {
                if (selectedItem.getImagePaths() != null) {
                    if (!selectedItem.getImagePaths().isEmpty()) {
                        String imagePath = findShuffledImagePath(selectedItem.getImagePaths(), -1);
                        updateImage(imagePath, itemImage);
                    }
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() { // RIGHT
            @Override
            public void onClick(View v) {
                if (selectedItem.getImagePaths() != null) {
                    if (!selectedItem.getImagePaths().isEmpty()) {
                        String imagePath = findShuffledImagePath(selectedItem.getImagePaths(), 1);
                        updateImage(imagePath, itemImage);
                    }
                }
            }
        });
        // Get the list of tags associated with the current item
        List<String> itemTagStrings = selectedItem.getTags().getTagStrings();

        // Find the RecyclerView in your layout
        RecyclerView tagsRecyclerView = findViewById(R.id.tagsRecyclerView);

        // Create an adapter for the RecyclerView
        TagsAdapter tagsAdapter = new TagsAdapter(itemTagStrings);

        // Set the layout manager and adapter for the RecyclerView
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tagsRecyclerView.setAdapter(tagsAdapter);
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
     * @param imagePath: reference path to the image located on firebase storage
     * @param itemImage: the imageView to display the new image
     */
    private void updateImage(String imagePath, ImageView itemImage) {
        StorageReference imageRef = database.getStorageReference().child(imagePath);

        final long TWELVE_MEGABYTE = 1024 * 1024 * 12;
        imageRef.getBytes(TWELVE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                itemImage.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(findViewById(android.R.id.content).getRootView().getContext(), R.string.firebase_firestore_image_not_loaded_message, Toast.LENGTH_LONG).show();
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
            showEditItemFragment();
            return true;
        } else if (id == R.id.delete_item) {
            // Handle the Delete button click
            showDeleteConfirmationDialog();
            return true;
        } else if (id == android.R.id.home) {
            // Handle the Up button (back button) click
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditItemFragment() {
        Log.d("ItemDetailsActivity", "showEditItemFragment called");
        Log.d("ItemDetailsActivity", "selectedItem is " + (selectedItem != null ? "not null" : "null"));
        Log.d("Item has these Tags:",selectedItem.getTags().toString());

        if (selectedItem != null) {
            EditItemFragment editItemFragment = EditItemFragment.newInstance(selectedItem);
            editItemFragment.listener = new EditItemFragment.OnFragmentInteractionListener() {
                @Override
                public void onItemUpdated(Item item) {
                    Log.d("ItemDetailsActivity", "onItemUpdated called");
                    Log.d("ItemDetailsActivity", "onItemUpdated called with item: " + item.getDescription());
                    // Only update the Firestore if the item has changed
                    if (!selectedItem.equals(item)) {
                        database.putItem(item).addOnSuccessListener(aVoid -> {
                            Log.d("ItemDetailsActivity", "Firestore update successful");
                            // Refresh UI with the updated item
                            updateUI(item);
                        }).addOnFailureListener(e -> {
                            Log.e("ItemDetailsActivity", "Error updating item in Firestore", e);
                        });
                    } else {
                        // Item hasn't changed, just refresh UI
                        updateUI(item);
                    }
                }
            };

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            editItemFragment.show(transaction, "EDIT_ITEM");
        }
    }

    // Method to update UI with the new item details
    private void updateUI(Item updatedItem) {
        Log.d("ItemDetailsActivity", "updateUI called");
        TextView dateText = findViewById(R.id.dateText);
        dateText.setText(updatedItem.getDate());

        TextView modeText = findViewById(R.id.modelText);
        modeText.setText(updatedItem.getModel());

        TextView itemDescription = findViewById(R.id.itemDescription);
        itemDescription.setText(updatedItem.getDescription());

        TextView makeText = findViewById(R.id.makeText);
        makeText.setText(updatedItem.getMake());

        TextView commentText = findViewById(R.id.commentText);
        commentText.setText(updatedItem.getComment());

        TextView valueText = findViewById(R.id.valueText);
        valueText.setText(updatedItem.getValueDollarString());

        updateTags(updatedItem.getTags());

        if (!(updatedItem.getImagePaths().isEmpty() || updatedItem.getImagePaths() == null)) { // Guaranteed at least 1 image if not empty/null
            currentImageIndex = 0;
            String firstImagePath = updatedItem.getImagePaths().get(currentImageIndex);
            updateImage(firstImagePath, itemImage);
        }
        else { // No images left
            // set itemImage to default //fixme:do this
            itemImage.setImageResource(R.drawable.default_image);
        }

    }

    // Helper method to update tags in UI
    private void updateTags(TagList tags) {
        // Get the list of tags associated with the current item
        List<String> itemTagStrings = tags.getTagStrings();

        // Find the RecyclerView in your layout
        RecyclerView tagsRecyclerView = findViewById(R.id.tagsRecyclerView);

        // Create an adapter for the RecyclerView
        TagsAdapter tagsAdapter = new TagsAdapter(itemTagStrings);

        // Set the layout manager and adapter for the RecyclerView
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tagsRecyclerView.setAdapter(tagsAdapter);
    }

    private void deleteSelectedItem() {
        if (selectedItem != null) {
            database.deleteImages(selectedItem.getImagePaths());
            database.deleteItem(selectedItem).addOnSuccessListener(aVoid -> {
                Log.d("ItemDetailsActivity", "Firestore delete successful");
                finish();
            }).addOnFailureListener(e -> {
                Log.e("ItemDetailsActivity", "Error deleting item from Firestore", e);
            });
        }
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteSelectedItem();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public TagList getGlobalTagList() {
        // Implement this method to return the global tag list
        // For example, if globalTagList is a field in MainActivity:
        // return globalTagList;
        return globalTagList;
    }

}