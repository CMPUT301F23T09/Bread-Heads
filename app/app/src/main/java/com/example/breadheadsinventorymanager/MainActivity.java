package com.example.breadheadsinventorymanager;

import static android.view.View.GONE;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;

import java.util.ArrayList;

/**
 * Main activity
 *
 * @version 0
 */
public class MainActivity extends AppCompatActivity implements AddItemFragment.OnFragmentInteractionListener {

    // make list is used to store all the makes,
    private SearchView searchBox;

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

        searchBox = findViewById(R.id.search_view);

        // ADAPTER SETUP DELETE BEFORE MERGING!
        // test using recyclerview instead of listview
        itemList = new ItemList();
        itemListView = findViewById(R.id.items_main_list);
        itemArrayAdapter = new CustomItemListAdapter(this, itemList);
        itemListView.setAdapter(itemArrayAdapter);

        // searchView id and adapter

        //makeSearchAdapter = new SearchableAdapter(this, R.layout.main_menu_list_content, itemList);

        // test cases for sample data

        // END OF ADAPTER SETUP DELETE BEFORE MERGING!
    }

    // TOPBAR MENU HANDLING AND FUNCTIONALITY

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
        } else if (id == R.id.add_item) {
            // show dialog for adding an item
            showAddItem();
            return true;
        } else if (id == R.id.delete_item) {
            // enter select mode to be able to delete one or more items
//            Intent selectIntent = new Intent(MainActivity.this, SelectModeActivity.class);
//            startActivity(selectIntent);
            selectMode();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // ADD ITEM DIALOG HANDLING

    /**
     * Handles when the delete button is pressed. Allows user to select one or more items and
     * delete them at once.
     */
    private void selectMode() {
        Button confirm_button = (Button)findViewById(R.id.select_mode_confirm);
        Button cancel_button = (Button)findViewById(R.id.select_mode_cancel);
        ArrayList<Integer> selectedItems = new ArrayList<Integer>();
//        int[] selectedItems = {};

        // bring ups popup with text to let the user know to select items now
        PopupMenu select_text_popup = new PopupMenu(this, this.findViewById(R.id.delete_item));
        select_text_popup.getMenuInflater().inflate(R.menu.select_item_text, select_text_popup.getMenu());
        select_text_popup.show();

        // make the confirm and cancel button visible and clickable
        confirm_button.setVisibility(View.VISIBLE);
        confirm_button.setClickable(true);
        cancel_button.setVisibility(View.VISIBLE);
        cancel_button.setClickable(true);

        // make it so you can click on items to select them
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // add it to array of item position indexes
                selectedItems.add(position);

            }
        });
        // when the confirm button is pressed
        confirm_button.setOnClickListener(v -> {
            // delete all selected items
            // selectedItems is an array of indexes of selected items from itemListView
            for (int i = 0; i < selectedItems.size(); i++) {
                itemList.remove(selectedItems.get(i));
            }
            itemArrayAdapter.notifyDataSetChanged();
            // hide the buttons and make them not clickable so they aren not accidentally pressed
            confirm_button.setVisibility(View.INVISIBLE);
            confirm_button.setClickable(false);
            cancel_button.setVisibility(View.INVISIBLE);
            cancel_button.setClickable(false);
        });

        // when the cancel button is pressed
        cancel_button.setOnClickListener(v -> {
            // unselect any selected items
//            itemListView.SelectItem
            // hide the buttons and make them not clickable so they aren not accidentally pressed
            confirm_button.setVisibility(View.INVISIBLE);
            confirm_button.setClickable(false);
            cancel_button.setVisibility(View.INVISIBLE);
            cancel_button.setClickable(false);
        });
    }
    /**
     * handles creating the dialog and switching to associated fragment
     */
    private void showAddItem() {
        new AddItemFragment().show(getSupportFragmentManager(), "ADD_CITY");
    }

    @Override
    public void onOKPressed(Item item) {
        itemList.add(item);
        itemArrayAdapter.notifyDataSetChanged();
    }

    // TOPBAR MENU HANDLING

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
            // show description search field
            showDescriptionSearch();
            return true;
        } else if (itemClick == R.id.make_menu) {
            // create "make" submenu
            showMakeSubMenu();
            return true;
        } else if (itemClick == R.id.remove_filter) {
            // set searchView text to nothing and reset adapter so no filters are present
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
     * Shows and populates submenu for filtering by make
     */
    private void showMakeSubMenu() {
        // show submenu of all available makes
        ArrayList<String> makeList = new ArrayList<String>();
        makeList = itemList.getMakeList();
        PopupMenu popup = new PopupMenu(this, this.findViewById(R.id.filter_popup));

        // populate the submenu with makeList strings
        for(int i = 0; i < makeList.size(); i++) {
            popup.getMenu().add(makeList.get(i));
        }
        popup.setOnMenuItemClickListener(this::onMakeClick);
        popup.getMenuInflater().inflate(R.menu.filter_make_submenu, popup.getMenu());
        popup.show();
    }

    /**
     * handles click events for make submenu
     * @param menuItem the item clicked
     * @return true to avoid unintended calls to other functions
     */
    private boolean onMakeClick(MenuItem menuItem) {
        int menuItemClick = menuItem.getItemId();
        ItemList results = new ItemList();

        // compare the make selected to the makes of itemList
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getMake().equals(menuItem.toString())) {
                    results.add(itemList.get(i));
            }
        }
        // update adapter to show filtered results
        CustomItemListAdapter tempAdapter = new CustomItemListAdapter(getApplicationContext(), results);
        itemListView.setAdapter(tempAdapter);
        ((CustomItemListAdapter) itemListView.getAdapter()).update(results);
        return true;
    }

    /**
     * Handles filtering itemList for make, creates a SearchView to search for a make
     */
    private void showDescriptionSearch() {

        // create our text listener to search for search entry
        searchBox.setVisibility(View.VISIBLE);
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            // creates a list to then set a new adapter to
            // modified code from this video https://www.youtube.com/watch?v=7Sw98YZW-ik
            public boolean onQueryTextChange(String newText) {
                ItemList results = new ItemList();

                for (int i = 0; i < itemList.size(); i++) {
                    if (itemList.get(i).getDescription().contains(newText)) {
                        results.add(itemList.get(i));
                    }
                }
                // update adapter to show the filtered results
                CustomItemListAdapter tempAdapter = new CustomItemListAdapter(getApplicationContext(), results);
                itemListView.setAdapter(tempAdapter);
                ((CustomItemListAdapter) itemListView.getAdapter()).update(results);

                return false;
            }
        });
    }
}