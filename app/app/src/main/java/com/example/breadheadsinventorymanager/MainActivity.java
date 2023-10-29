package com.example.breadheadsinventorymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;

/**
 * Main activity
 *
 * @version 0
 */
public class MainActivity extends AppCompatActivity {

    // Buttons for handling filter menu
    private MenuItem filterButton;
    private MenuItem makeButton;
    
    // make list is used to store all the makes,
    // TODO create a makeList class to check for repeat "makes" 
    private ArrayList<String> makeList;
    
    
    // INITIALIZE LIST OBJECTS DELETE BEFORE MERGING
    private ItemList itemList;
    private ArrayAdapter<Item> itemArrayAdapter;
    private ListView itemListView;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.user_icon);


        // ADAPTER SETUP DELETE BEFORE MERGING!

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

        // END OF ADAPTER SETUP DELETE BEFORE MERGING!

    }

    /**
     * Handles inflating the top bar and getting relevant button ID's
     * @param menu The options menu in which you place your items.
     *
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar, menu);

        // get the top bar filter button id
        filterButton = menu.findItem(R.id.filter_popup);
        makeButton = menu.findItem(R.id.make_menu);
        return true;
    }

    /**
     * Handles buttons clicked in the top bar
     * @param item The menu item that was selected.
     *
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home ) {
            // do profile selection
            return true;
        } else if (id == R.id.filter_popup) {
            // create menu for filtering items
            showFilterMenu();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handles the menu creation after the "filter button" is tapped
     */
    private void showFilterMenu() {
            // shows the menu of filterable objects
            PopupMenu popup = new PopupMenu(this, this.findViewById(R.id.filter_popup));
            popup.setOnMenuItemClickListener(this::onFilterMenuClick);
            popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
            popup.show();
    }

    /**
     * Handles clicking of menu items
     * @param item the menu item that was clicked
     * @return true if an item is clicked, false otherwise
     */
    private boolean onFilterMenuClick(MenuItem item) {
        int itemClick = item.getItemId();
        // Switch cases do not work with android ID's idk why
        if(itemClick  == R.id.date) {
            //TODO make stuff happen when this is clicked
            //Log.d("WE CLICKED DATE", "WE CLICKED DATE");

            return true;
        } else if (itemClick == R.id.description) {
            //TODO make stuff happen when this is clicked
            //Log.d("WE CLICKED DESCRIPTION", "WE CLICKED DESCRIPTION");

            return true;
        } else if (itemClick == R.id.make_menu) {
            //TODO make stuff happen when this is clicked
            //Log.d("WE CLICKED MAKE", "WE CLICKED MAKE");
            // Create a dialog that shows all makes, select one (or more idk) then update list accordingly
            showMakeSubMenu();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Handles submenu creation for make
     */
    private void showMakeSubMenu() {


        PopupMenu popup = new PopupMenu(this, this.findViewById(R.id.filter_popup));

        popup.setOnMenuItemClickListener(this::onMakeMenuClick);

        makeList = new ArrayList<String>();
        for(int i = 0; i < itemList.size(); i++) {
            String makeString = itemList.get(i).getMake();
            popup.getMenu().add(makeString);
        }

        popup.getMenuInflater().inflate(R.menu.filter_make_submenu, popup.getMenu());



        popup.show();
    }

    /**
     * Handles click events for clicking the make submenu
     * @param menuItem the item that was clicked
     * @return true if a menu item was clicked, false otherwise
     */
    private boolean onMakeMenuClick(MenuItem menuItem) {
        int itemClick = menuItem.getItemId();
        if(itemClick == menuItem.getItemId()) {
            //TODO make stuff happen when this is clicked
            //Log.d("WE CLICKED THE SUBMENU", "WE CLICKED THE SUBMENU");

            return true;
        } else {
            return false;
        }
    }
}