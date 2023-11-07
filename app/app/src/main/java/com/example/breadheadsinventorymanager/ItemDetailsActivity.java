package com.example.breadheadsinventorymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ItemDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Item selectedItem = (Item) getIntent().getSerializableExtra("item");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView dateText = findViewById(R.id.dateText);
        dateText.setText(selectedItem.getDate());

        TextView modeText = findViewById(R.id.modelText);
        modeText.setText(selectedItem.getModel());

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