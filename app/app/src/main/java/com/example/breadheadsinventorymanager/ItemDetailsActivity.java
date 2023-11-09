package com.example.breadheadsinventorymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Objects;

public class ItemDetailsActivity extends AppCompatActivity {
    private Item selectedItem;
    private FirestoreInteract database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        selectedItem = (Item) getIntent().getSerializableExtra("item");

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

        if (selectedItem != null) {
            EditItemFragment editItemFragment = EditItemFragment.newInstance(selectedItem);
            editItemFragment.listener = new EditItemFragment.OnFragmentInteractionListener() {
                @Override
                public void onItemUpdated(Item item) {
                    Log.d("ItemDetailsActivity", "onItemUpdated called");
                    Log.d("ItemDetailsActivity", "onItemUpdated called with item: " + item.getDescription());
                    database.putItem(item).addOnSuccessListener(aVoid -> {
                        Log.d("ItemDetailsActivity", "Firestore update successful");
                        // Refresh UI with the updated item
                        updateUI(item);
                    }).addOnFailureListener(e -> {
                        // Handle failure if needed
                        Log.e("ItemDetailsActivity", "Error updating item in Firestore", e);
                    });
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
    }
}