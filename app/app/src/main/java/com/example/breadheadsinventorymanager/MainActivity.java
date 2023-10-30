package com.example.breadheadsinventorymanager;

import static android.view.View.GONE;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;

import java.util.ArrayList;

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
    private SearchView searchBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.user_icon);


        // ADAPTER SETUP DELETE BEFORE MERGING!
        // test using recyclerview instead of listview
        itemList = new ItemList();
        itemListView = findViewById(R.id.items_main_list);
        itemArrayAdapter = new CustomItemListAdapter(this, itemList);
        itemListView.setAdapter(itemArrayAdapter);

        // searchView id and adapter
        searchBox = findViewById(R.id.search_view);
        //makeSearchAdapter = new SearchableAdapter(this, R.layout.main_menu_list_content, itemList);

        // test cases for sample data
        Item item1 = new Item("22/01/2000", "this is test case 1", "make1", "model", "123456789", 12);
        Item item2 = new Item("22/01/2000", "this is test case 2", "make1", "model", "123456789", 12);
        Item item3 = new Item("22/01/2000", "this is test case 3", "make2", "model", "123456789", 12);
        Item item4 = new Item("22/01/2000", "this is test case 4", "make3", "model", "123456789", 12);
        Item item5 = new Item("22/01/2000", "this is test case 4", "make4", "model", "123456789", 12);

        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
        itemList.add(item4);
        itemList.add(item5);

        itemArrayAdapter.notifyDataSetChanged();
        // END OF ADAPTER SETUP DELETE BEFORE MERGING!
    }

    /**
     * Handles inflating the top bar and getting relevant button ID's
     *
     * @param menu The options menu in which you place your items.
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar, menu);
        return true;
    }

    /**
     * Handles buttons clicked in the top bar
     *
     * @param item The menu item that was selected.
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
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
     *
     * @param item the menu item that was clicked
     * @return true if an item is clicked, false otherwise
     */
    private boolean onFilterMenuClick(MenuItem item) {
        int itemClick = item.getItemId();
        // Switch cases do not work with android ID's idk why
        if (itemClick == R.id.date) {
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
            // set searchView text to nothing and reset adapter and make search box disappear
            searchBox.setVisibility(GONE);
            searchBox.setQuery(getIntent().getDataString(), false);
            itemListView.setAdapter(itemArrayAdapter);
            itemArrayAdapter.notifyDataSetChanged();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Handles filtering itemList for make, creates a SearchView to search for a make
     */
    private void showMakeSubMenu() {
        PopupMenu popup = new PopupMenu(this, this.findViewById(R.id.filter_popup));

        // create our text listener to search for search entry
        searchBox.setVisibility(View.VISIBLE);
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            // creates a list to then set a new adapter to
            public boolean onQueryTextChange(String newText) {
                ItemList results = new ItemList();

                for (int i = 0; i < itemList.size(); i++) {
                    if (itemList.get(i).getMake().contains(newText)) {
                        results.add(itemList.get(i));
                    }
                }
                CustomItemListAdapter tempAdapter = new CustomItemListAdapter(getApplicationContext(), results);
                itemListView.setAdapter(tempAdapter);
                ((CustomItemListAdapter) itemListView.getAdapter()).update(results);

                return false;
            }
        });
    }
}