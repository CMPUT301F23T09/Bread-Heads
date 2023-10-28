package com.example.breadheadsinventorymanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Main activity
 *
 * @version 0
 */
public class MainActivity extends AppCompatActivity {

    // INITIALIZE LIST DELETE BEFORE MERGING
    private ItemList itemList;
    private ArrayAdapter<Item> itemArrayAdapter;
    private ListView itemListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.user_icon);

        // adapter stuff
        itemListView = findViewById(R.id.items_main_list);

        itemList = new ItemList();
        itemArrayAdapter = new CustomItemListAdapter(this, itemList);
        itemListView.setAdapter(itemArrayAdapter);

        // test cases for sample data
        Item item1 = new Item("22/01/2000", "this is a test case", "make", "model", "123456789", 12);
        Item item2 = new Item("22/01/2000", "this is a test case", "make", "model", "123456789", 12);
        Item item3 = new Item("22/01/2000", "this is a test case", "make", "model", "123456789", 12);
        Item item4 = new Item("22/01/2000", "this is a test case", "make", "model", "123456789", 12);
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
        itemList.add(item4);
        itemArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Do stuff
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}