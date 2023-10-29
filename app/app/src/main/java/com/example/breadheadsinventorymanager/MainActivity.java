package com.example.breadheadsinventorymanager;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Main activity
 *
 * @version 0
 */
public class MainActivity extends AppCompatActivity {

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
        Item item1 = new Item("22/01/2000", "this is test case 1", "make1", "model", "123456789", 12);
        Item item2 = new Item("22/01/2000", "this is test case 2", "make1", "model", "123456789", 12);
        Item item3 = new Item("22/01/2000", "this is test case 3", "make2", "model", "123456789", 12);
        Item item4 = new Item("22/01/2000", "this is test case 4", "make3", "model", "123456789", 12);
        itemList.add(item1);
        itemList.add(item2);
        //itemList.add(item3);
        //itemList.add(item4);
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
        // Buttons for handling filter menu
        MenuItem filterButton = menu.findItem(R.id.filter_popup);
        MenuItem makeButton = menu.findItem(R.id.make_menu);
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

            return true;
        } else if (itemClick == R.id.description) {
            //TODO make stuff happen when this is clicked

            return true;
        } else if (itemClick == R.id.make_menu) {
            // create "make" submenu
            showMakeSubMenu();
            return true;
        } else if (itemClick == R.id.remove_filter) {
            removeFilter();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes the filter set on items
     */
    private void removeFilter() {
        for(int i = 0; i < itemList.size(); i++) {
            itemListView.getChildAt(i).setLayoutParams(new ListView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        }
    }

    /**
     * Handles submenu creation for make
     */
    private void showMakeSubMenu() {
        PopupMenu popup = new PopupMenu(this, this.findViewById(R.id.filter_popup));

        // populate test list
        // DELETE BEFORE MERGING!!!

        makeList = new ArrayList<String>();
        String makeString;
        // I do not like the performance issues this might have
        for(int i = 0; i < itemList.size(); i++) {
             makeString = itemList.get(i).getMake();
             Log.d("ITEM LIST MAKE", makeString);
             //for(int j = 0; j < makeList.size(); j++) {
                // if(makeString.equals(makeList.get(j))) {
                     //break;
                // }
             popup.getMenu().add(makeString);
            //}
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //TODO update list to only display objects with that make
                for(int i = 0; i < itemList.size(); i++) {
                    // if the two makes are different, toggle visibility
                   if(!(item.getTitle() == itemList.get(i).getMake())) {
                       itemListView.getChildAt(i).setLayoutParams(new ListView.LayoutParams(1, 1));
                   }
                }
                return false;
            }
        });
        popup.getMenuInflater().inflate(R.menu.filter_make_submenu, popup.getMenu());
        popup.show();
    }
}