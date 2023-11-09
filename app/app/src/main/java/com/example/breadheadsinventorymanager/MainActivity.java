package com.example.breadheadsinventorymanager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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

    // obligatory id's for lists/adapters
    private ItemList itemList;
    //private ItemList filteredList;
    private ArrayAdapter<Item> itemArrayAdapter;
    private ListView itemListView;

    // ids for recycler view for filtering view

    private Filter filter;
    private ArrayAdapter<Item> filterAdapter;
    private RecyclerView recyclerView;
    private ArrayList<RecyclerItem> recyclerList;
    private RecyclerViewAdapter recyclerAdapter;

    private FirestoreInteract database;

    // stores information about how the list is currently sorted
    private String sortMode = "description"; // which field to sort by
    private boolean sortAscending = true; // whether to sort in ascending or descending order
    
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

        // filter recycler view setup
        filter = new Filter();
        recyclerList = new ArrayList<RecyclerItem>();
        recyclerView = findViewById(R.id.filter_recycler_view);
        recyclerAdapter = new RecyclerViewAdapter(this, recyclerList, this);
        recyclerView.setAdapter(recyclerAdapter);

        //ListView and adapter setup
        database = new FirestoreInteract();
        itemList = new ItemList();
        updateList().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Item selectedItem = (Item) itemListView.getAdapter().getItem(position);
                        Intent intent = new Intent(MainActivity.this, ItemDetailsActivity.class);
                        intent.putExtra("item", selectedItem);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    // ITEM LIST HANDLING

    /**
     * Updates the total value displayed at the bottom of the screen
     */
    private void updateTotalValue() {
        totalValue.setText(getString(R.string.totalValueTitle, itemList.getSumAsDollarString()));
        totalValue.setVisibility(View.VISIBLE);
    }

    /**
     * Updates the contents of the ItemList with the contents of the Firestore database
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
                updateTotalValue();
            }
        });
    }

    // ADD ITEM DIALOG HANDLING

    @Override
    public void onOKPressed(Item item) {
        database.putItem(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateList();
            }
        });
    }

    // recycler view click listener, removes recycler adapter Item
    // bad practise, Interface should be singular
    @Override
    public void onItemClick(int position) {

    }

    /**
     * handles creating the dialog and switching to associated fragment
     */
    private void showAddItem() {
        new AddItemFragment().show(getSupportFragmentManager(), "ADD_CITY");
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
        } else {
            return super.onOptionsItemSelected(item);
        }
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
     * Handles clicking of menu items
     *
     * @param item the menu item that was clicked
     * @return true if an item is clicked, false otherwise
     */
    private boolean onFilterMenuClick(MenuItem item) {
        int itemClick = item.getItemId();
        // Switch cases do not work with android ID's idk why
        if (itemClick == R.id.date) {
            //resetAdapter();
            showDateFilter();
            return true;
        } else if (itemClick == R.id.description) {
            // show description search field
            //resetAdapter();
            showDescriptionSearch();
            return true;
        } else if (itemClick == R.id.make_menu) {
            // create "make" submenu
            //resetAdapter();
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

    // FILTERING UTILITY FUNCTIONS

    /**
     * resets the adapter to the original ItemList and clears the filtered list in filter
     */
    private void resetAdapter() {
        // toggle visibility and reset text query and filter visibility
        toggleFilterVisibility();
        filter.clearFilter();
        filter.clearCriteria();
        recyclerList.clear();
        startDate.setText("");
        endDate.setText("");
        itemListView.setAdapter(itemArrayAdapter);
        itemArrayAdapter.notifyDataSetChanged();
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
            recyclerView.setVisibility(GONE);
        }
        if (searchBox.getVisibility() == VISIBLE) {
            searchBox.setVisibility(GONE);
            searchBox.setQuery(getIntent().getDataString(), false);
        }
    }

    // FILTERING FUNCTION CALLS

    /**
     * handles click events for make submenu
     * @param menuItem the item clicked
     * @return true to avoid unintended calls to other functions
     */
    private boolean onMakeClick(MenuItem menuItem) {
        toggleFilterVisibility();

        // update adapter to show filtered results via filter class function call
        filter.filterMake(menuItem.toString(), itemList);
        filterAdapter = new CustomItemListAdapter(getApplicationContext(), filter.processFilter(itemList));
        itemListView.setAdapter(filterAdapter);
        filterAdapter.notifyDataSetChanged();

        // add an interactable filter to the recycler view
        recyclerView.setVisibility(VISIBLE);
        RecyclerItem recyclerItem = new RecyclerItem(menuItem.toString());
        recyclerList.add(recyclerItem);
        recyclerAdapter.notifyDataSetChanged();
        Log.d("h1", String.valueOf(recyclerAdapter.getItemCount()));

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
                // update adapter to show the filtered results
                filter.filterDescription(query, itemList);
                filterAdapter = new CustomItemListAdapter(getApplicationContext(), filter.processFilter(itemList));
                itemListView.setAdapter(filterAdapter);
                filterAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            // modified code from this video https://www.youtube.com/watch?v=7Sw98YZW-ik
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /**
     * handles filtering by date, checks for valid date then creates a new list for the adapter to latch on to
     */
    private void showDateFilter() {
        toggleFilterVisibility();
        startDate.setVisibility(VISIBLE);
        endDate.setVisibility(VISIBLE);
        filterDateButton.setVisibility(VISIBLE);

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // when pressed, it will filter the dates by range entered, display error message otherwise
        filterDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String startString = startDate.getText().toString();
                    String endString = endDate.getText().toString();
                    LocalDate newDateStart = LocalDate.parse(startString, formatter);
                    LocalDate newDateEnd = LocalDate.parse(endString, formatter);
                    // check for valid date range
                    if (newDateStart.isAfter(currentDate) || newDateEnd.isAfter(currentDate) || newDateEnd.isBefore(newDateStart)) {
                        dateErrorMsg.setText(R.string.date_in_future);
                        dateErrorMsg.setVisibility(VISIBLE);
                        return;
                    }

                    // update adapter to new filter
                    // call to Filter class when setting adapter
                    filter.filterDate(newDateStart, newDateEnd, itemList);
                    ItemList tempList = filter.processFilter(itemList);
                    if(tempList != null) {
                        filterAdapter = new CustomItemListAdapter(getApplicationContext(), tempList);
                        itemListView.setAdapter(filterAdapter);
                        filterAdapter.notifyDataSetChanged();
                    }

                    dateErrorMsg.setVisibility(GONE);
                } catch (DateTimeParseException e) {
                    dateErrorMsg.setText(R.string.invalid_date_range);
                    dateErrorMsg.setVisibility(VISIBLE);
                }
            }
        });
        }

}