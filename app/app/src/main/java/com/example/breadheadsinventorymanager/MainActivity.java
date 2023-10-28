package com.example.breadheadsinventorymanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;

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

    // INITIALIZE LIST OBJECTS DELETE BEFORE MERGING
    private ItemList itemList;
    private ArrayAdapter<Item> itemArrayAdapter;
    private ListView itemListView;

    private MenuItem filterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.user_icon);


        // adapter stuff DELETE BEFORE MERGING!

        itemListView = findViewById(R.id.items_main_list);

        itemList = new ItemList();
        itemArrayAdapter = new CustomItemListAdapter(this, itemList);
        itemListView.setAdapter(itemArrayAdapter);

        // test cases for sample data
        Item item1 = new Item("22/01/2000", "this is test case 1", "make", "model", "123456789", 12);
        Item item2 = new Item("22/01/2000", "this is test case 2", "make", "model", "123456789", 12);
        Item item3 = new Item("22/01/2000", "this is test case 3", "make", "model", "123456789", 12);
        Item item4 = new Item("22/01/2000", "this is test case 4", "make", "model", "123456789", 12);
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
        itemList.add(item4);
        itemArrayAdapter.notifyDataSetChanged();

        // END OF adapter stuff DELETE BEFORE MERGING!


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar, menu);

        // get the topbar button id
        filterButton = menu.findItem(R.id.filter_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home ) {
            // do stuff
            return true;
        } else if (id == R.id.filter_menu) {
            // create menu for filtering items
            filterButtonCLick();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /** handles the menu creation after the "filter button is tapped
     *
     */
    private void filterButtonCLick() {
            // shows the menu
            PopupMenu popup = new PopupMenu(this, this.findViewById(R.id.filter_menu));
            popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
            popup.show();
            // TODO make stuff happen when buttons are tapped
    }
}