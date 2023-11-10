package com.example.breadheadsinventorymanager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Main activity
 *
 * @version 1
 */
public class MainActivity extends AppCompatActivity implements AddItemFragment.OnFragmentInteractionListener {
    // id for search box to filter by description
    private SearchView searchBox;
    private EditText startDate;
    private EditText endDate;
    private TextView dateErrorMsg;
    private Button filterDateButton;
    private TextView totalValue;
    private ImageButton sortButton;
    private Button sortOrderButton;

    // obligatory id's for lists/adapter
    private ItemList itemList;
    private CustomItemListAdapter itemArrayAdapter;
    private ListView itemListView;
    private FirestoreInteract database;
    private TagList tagList;

    // stores information about how the list is currently sorted
    private String sortMode = "description"; // which field to sort by
    private boolean sortAscending = true; // whether to sort in ascending or descending order

    // stores the filters to apply to the adapter
    // first character in each filter determines the filter type
    // 'D': description. '1'/'2': lower/upper bound on date resp. 'M': make.
    // multiple makes can be applied at once! multiple filters are ANDed; different makes are ORed.
    private ArrayList<CharSequence> filters = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.user_icon);

        searchBox = findViewById(R.id.search_view);
        startDate = findViewById(R.id.filter_date_start);
        endDate = findViewById(R.id.filter_date_end);
        dateErrorMsg = findViewById(R.id.invalid_date_message);
        filterDateButton = findViewById(R.id.date_filter_button);
        totalValue = findViewById(R.id.total_value);
        sortButton = findViewById(R.id.sort_button);
        sortOrderButton = findViewById(R.id.sort_order_button);

        //ListView and adapter setup
        database = new FirestoreInteract();
        updateList().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                defaultItemClickListener();
            }
        });

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortMenu();
            }
        });
        sortOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean order = toggleSortOrder();
                if (order) {
                    sortOrderButton.setText(R.string.ascending);
                } else {
                    sortOrderButton.setText(R.string.descending);
                }
            }
        });
    }

    // ITEM LIST HANDLING

    /**
     * Sets itemListView's onItemClickListener to the default.
     */
    public void defaultItemClickListener() {
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem = itemArrayAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, ItemDetailsActivity.class);
                intent.putExtra("item", selectedItem);
                startActivity(intent);
            }
        });
    }

    /**
     * Updates the total value displayed at the bottom of the screen
     */
    private void updateTotalValue() {
        totalValue.setText(getString(R.string.totalValueTitle,
                itemArrayAdapter.getSumAsDollarString()));
        totalValue.setVisibility(View.VISIBLE);
    }

    /**
     * Updates the contents of itemList with the contents of the Firestore database
     * @return A Task tracking the update
     */
    private Task<QuerySnapshot> updateList() {
        itemList = new ItemList();
        return database.populateWithItems(itemList).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                itemList.sort(sortMode, sortAscending);
                itemListView = findViewById(R.id.items_main_list);
                itemArrayAdapter = new CustomItemListAdapter(getApplicationContext(), itemList);
                itemListView.setAdapter(itemArrayAdapter);
                activateFilters();
            }
        });
    }

    // TAG LIST HANDLING

    /**
     * Updates the contents of tagList with the contents of the Firestore database
     * @return A Task tracking the update
     */
    private Task<QuerySnapshot> updateTags() {
        tagList = new TagList();
        return database.populateWithTags(tagList);
    }

    // ADD ITEM DIALOG HANDLING

    /**
     * Uploads the item and related images to firebase and refreshes the app UI
     * @param item The item
     * @param imageMap The contained images
     */
    @Override
    public void onOKPressed(Item item, Map<String, Uri> imageMap) {
        database.putItem(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateList();
            }
        });

        // Upload images: fixme: eventually, should store image under a folder named after its
        // item's id. So grab the item's firestore id after uploading the item. Then, upload the
        // images
        Iterator<Map.Entry<String, Uri>> it = imageMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Uri> image = (Map.Entry<String, Uri>) it.next();
            assert(item.getImagePaths().contains(image.getKey()));
        }
        database.uploadImages(imageMap, findViewById(android.R.id.content).getRootView());
    }

    /**
     * handles creating the dialog and switching to associated fragment
     */
    private void showAddItem() {
        new AddItemFragment().show(getSupportFragmentManager(), "ADD_ITEM");
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
            selectMode();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // ADD ITEM DIALOG HANDLING

    /**
     * Handles when the delete button is pressed, which causes the app to enter "select mode". Meaning checkboxes appear for each
     * item in the list that allows the user to select multiple items at once to do various functions with those items. Currently
     * the only function is to delete multiple items. In the future, you will be able to add tags to all of the selected items
     */
    private void selectMode() {
        Button confirm_button = findViewById(R.id.select_mode_confirm);
        Button cancel_button = findViewById(R.id.select_mode_cancel);

        // bring ups popup with text to let the user know to select items now
        PopupMenu select_text_popup = new PopupMenu(this, this.findViewById(R.id.delete_item));
        select_text_popup.getMenuInflater().inflate(R.menu.select_item_text, select_text_popup.getMenu());
        select_text_popup.show();

        // make the confirm and cancel button visible and clickable
        confirm_button.setVisibility(View.VISIBLE);
        confirm_button.setClickable(true);
        cancel_button.setVisibility(View.VISIBLE);
        cancel_button.setClickable(true);

        // make the checkbox visible for each item
        for (int i = 0; i < itemList.size(); i++) {
            // get the item at position i
            Item current_item = itemList.get(i);
            CheckBox checkbox = current_item.getCheckBox();
            if (checkbox == null){

                checkbox = itemListView.findViewById(R.id.checkBox);
            }
            checkbox.setVisibility(View.VISIBLE);
        }

        // allow the user to click on the item to enable the checkbox to be checked
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item current_item = itemList.get(position);
                CheckBox checkBox = view.findViewById(R.id.checkBox);
                current_item.setCheckBox(checkBox);
                CheckBox checkbox = current_item.getCheckBox();
                checkbox.setVisibility(View.VISIBLE);
                checkbox.setChecked(!checkbox.isChecked());

            }
        });
        // when the confirm button is pressed
        confirm_button.setOnClickListener(v -> {
            defaultItemClickListener();

            // hide the buttons and make them not clickable so they aren not accidentally pressed
            confirm_button.setVisibility(View.INVISIBLE);
            confirm_button.setClickable(false);
            cancel_button.setVisibility(View.INVISIBLE);
            cancel_button.setClickable(false);

            for (int i = itemList.size()-1; i > -1; i--) {
                // get the item at position i
                Item current_item = itemList.get(i);
                CheckBox checkbox = current_item.getCheckBox();
                if (checkbox != null){
                    if (checkbox.isChecked()){
                        // Delete item in firebase database
                        itemList.remove(current_item);
                        database.deleteItem(current_item).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // void
                            }
                        });

                    }
                    // uncheck and hide the checkbox
                    checkbox.setChecked(false);
                    checkbox.setVisibility(View.INVISIBLE);
                }

            }

            updateList();
        });

        // when the cancel button is pressed
        cancel_button.setOnClickListener(v -> {
            defaultItemClickListener();

            // hide the buttons and make them not clickable so they aren not accidentally pressed
            confirm_button.setVisibility(View.INVISIBLE);
            confirm_button.setClickable(false);
            cancel_button.setVisibility(View.INVISIBLE);
            cancel_button.setClickable(false);
            for (int i = 0; i < itemList.size(); i++) {
                // get the item at position i
                Item current_item = itemList.get(i);
                CheckBox checkbox = current_item.getCheckBox();

                // uncheck and hide the checkbox
                if (checkbox != null){
                    checkbox.setChecked(false);
                    checkbox.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    // SORT MENU HANDLING

    /**
     * Handles sort menu creation
     */
    private void showSortMenu() {
        PopupMenu popup = new PopupMenu(this, this.findViewById(R.id.sort_button));
        popup.setOnMenuItemClickListener(this::onSortMenuClick);
        popup.getMenuInflater().inflate(R.menu.sort_menu, popup.getMenu());
        popup.show();
    }

    /**
     * Handles clicking of sort menu items
     * @param item the menu item that was clicked
     * @return true if an item is clicked, false otherwise
     */
    private boolean onSortMenuClick(MenuItem item) {
        int itemClick = item.getItemId();
        if (itemClick == R.id.sort_date) {
            sortMode = "date";
        } else if (itemClick == R.id.sort_desc) {
            sortMode = "description";
        } else if (itemClick == R.id.sort_comment) {
            sortMode = "comment";
        } else if (itemClick == R.id.sort_make) {
            sortMode = "make";
        } else if (itemClick == R.id.sort_value) {
            sortMode = "value";
        } else {
            return false;
        }

        itemList.sort(sortMode, sortAscending);
        itemArrayAdapter.notifyDataSetChanged();
        activateFilters();
        return true;
    }

    /**
     * Changes the order items are sorted in
     * @return True if the new sort order is ascending, otherwise false
     */
    private boolean toggleSortOrder() {
        sortAscending = !sortAscending;
        itemList.sort(sortMode, sortAscending);
        itemArrayAdapter.notifyDataSetChanged();
        activateFilters();
        return sortAscending;
    }

    // FILTER MENU HANDLING

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
     * Handles clicking of filter menu items
     *
     * @param item the menu item that was clicked
     * @return true if an item is clicked, false otherwise
     */
    private boolean onFilterMenuClick(MenuItem item) {
        int itemClick = item.getItemId();
        // Switch cases do not work with android ID's idk why
        if (itemClick == R.id.date) {
            showDateFilter();
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
            // set searchView texts to nothing and reset adapter so no filters are present
            resetAdapter();
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
        ArrayList<String> makeList;
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

    // FILTERING UTILITY FUNCTIONS

    /**
     * resets the adapter to the original ItemList
     * means that the list cannot have more than one filter active at a time
     */
    private void resetAdapter() {
        filters = new ArrayList<>();
        toggleFilterVisibility();
        itemArrayAdapter = new CustomItemListAdapter(getApplicationContext(), itemList);
        itemListView.setAdapter(itemArrayAdapter);
        updateTotalValue();
    }

    /**
     * toggles visibility of the date range and description search fields
     * sets entered text to nothing
     */
    private void toggleFilterVisibility() {
        // toggle visibility of fields that should be invisible
        if (startDate.getVisibility() == VISIBLE) {
            filterDateButton.setVisibility(GONE);
            dateErrorMsg.setVisibility(GONE);
            startDate.setVisibility(GONE);
            endDate.setVisibility(GONE);
            startDate.setText("");
            endDate.setText("");

            // toggle invisible and reset query
        }
        if (searchBox.getVisibility() == VISIBLE) {
            searchBox.setVisibility(GONE);
            searchBox.setQuery(getIntent().getDataString(), false);
        }
    }

    // FILTERING LOGIC FUNCTIONS

    /**
     * Constructs filters from the filter set and applies them to the data.
     * @see CustomItemListAdapter custom filter
     */
    private void activateFilters() {
        itemArrayAdapter = new CustomItemListAdapter(getApplicationContext(), itemList);
        Gson gson = new Gson();
        String json = gson.toJson(filters);
        itemArrayAdapter.getFilter().filter(json, new Filter.FilterListener() {
            @Override
            public void onFilterComplete(int count) {
                itemListView.setAdapter(itemArrayAdapter);
                updateTotalValue();
            }
        });
    }

    /**
     * Handles click events for make submenu
     * @param menuItem the item clicked
     * @return true to avoid unintended calls to other functions
     */
    private boolean onMakeClick(MenuItem menuItem) {
        toggleFilterVisibility();
        // update adapter to show filtered results
        filters.add('M' + menuItem.toString());
        activateFilters();
        return true;
    }

    /**
     * Handles filtering itemList for make, creates a SearchView to search for a make
     */
    private void showDescriptionSearch() {
        toggleFilterVisibility();
        searchBox.setVisibility(VISIBLE);

        // create our text listener to search for search entry
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            // creates a list to then set a new adapter to
            // modified code from this video https://www.youtube.com/watch?v=7Sw98YZW-ik
            public boolean onQueryTextChange(String newText) {
                // clear previous description filters
                ArrayList<CharSequence> newFilters = filters;
                for (CharSequence filter : filters) {
                    if (filter.charAt(0) == 'D') {
                        newFilters.remove(filter);
                    }
                }
                filters = newFilters;


                if (newText.length() > 0) {
                    filters.add('D' + newText);
                }
                activateFilters();
                return false;
            }
        });
    }

    /**
     * handles filtering by date, checks for valid date then creates a new list
     * for the adapter to latch on to
     */
    private void showDateFilter() {
        toggleFilterVisibility();
        startDate.setVisibility(VISIBLE);
        endDate.setVisibility(VISIBLE);
        filterDateButton.setVisibility(VISIBLE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        // when pressed, it will filter the dates by range entered, display error message otherwise
        filterDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String startString = startDate.getText().toString();
                    String endString = endDate.getText().toString();

                    // remove existing filters
                    if (!filters.isEmpty()) {
                        ArrayList<CharSequence> newFilters = new ArrayList<>(filters);
                        for (CharSequence filter : filters) {
                            if (filter.charAt(0) == '1' || filter.charAt(0) == '2') {
                                newFilters.remove(filter);
                            }
                        }
                        filters = newFilters;
                    }


                    if (startString.length() > 0) {
                        LocalDate.parse(startString, formatter); // called for try-catch
                        filters.add('1' + startString);
                    }

                    if (endString.length() > 0) {
                        LocalDate.parse(endString, formatter); // called for try-catch
                        filters.add('2' + endString);
                    }

                    // update adapter to new filter
                    dateErrorMsg.setVisibility(GONE);
                    activateFilters();
                } catch (DateTimeParseException e) {
                    dateErrorMsg.setVisibility(VISIBLE);
                }
            }
        });
        }

}