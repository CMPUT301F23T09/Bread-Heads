package com.example.breadheadsinventorymanager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Main menu activity. Contains the entire inventory, the ability to filter, sort, and search it,
 * the ability to add new items or delete existing ones, and related functionality.
 *
 * @version 2
 */
public class MainActivity extends AppCompatActivity implements AddItemFragment.OnFragmentInteractionListener, AddTagFragment.OnFragmentInteractionListener{
    // id for search box to filter by description
    private SearchView searchBox;
    private EditText startDate;
    private EditText endDate;
    private TextView dateErrorMsg;
    private Button filterDateButton;
    private TextView totalValue;
    private ImageButton sortButton;
    private Button sortOrderButton;
    private ImageButton filterButton;
    private ImageButton searchButton;
    private ImageButton clearButton;

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

    // The recyclerView for displaying active filters setup
    private RecyclerView filterView;
    private ArrayList<String> recyclerViewList;
    private LinearLayoutManager linearLayoutManager;
    private FilterRecyclerAdapter filterRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.user_icon);

        filterView = findViewById(R.id.active_filter_recycler_view);
        searchBox = findViewById(R.id.search_view);
        startDate = findViewById(R.id.filter_date_start);
        endDate = findViewById(R.id.filter_date_end);
        dateErrorMsg = findViewById(R.id.invalid_date_message);
        filterDateButton = findViewById(R.id.date_filter_button);
        totalValue = findViewById(R.id.total_value);
        sortButton = findViewById(R.id.sort_button);
        sortOrderButton = findViewById(R.id.sort_order_button);
        filterButton = findViewById(R.id.filter_popup);
        searchButton = findViewById(R.id.quick_search);
        clearButton = findViewById(R.id.clear_filter);

        // filter recyclerView setup
        recyclerViewList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        filterRecyclerAdapter = new FilterRecyclerAdapter(getApplicationContext(), recyclerViewList, this);
        filterView.setLayoutManager(linearLayoutManager);
        filterView.setAdapter(filterRecyclerAdapter);

        //ListView and adapter setup
        database = new FirestoreInteract();

        updateTags().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            }
        });


        updateList().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                defaultItemClickListener();
            }
        });

        sortButton.setOnClickListener(v -> showSortMenu());
        sortOrderButton.setOnClickListener(v -> onSortOrderButtonClick());
        filterButton.setOnClickListener(v -> showFilterMenu());
        searchButton.setOnClickListener(v -> onSearchButtonClick());
        clearButton.setOnClickListener(v -> onClearFilterClick());
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
                intent.putExtra("tagList", tagList);
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
        return database.populateWithTags(tagList).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
            }
        });
    }

    /**
     *
     */
    public TagList getGlobalTagList() {
        // Implement this method to return the global tag list
        // For example, if globalTagList is a field in MainActivity:
        // return globalTagList;
        return tagList;
    }

    // ADD ITEM DIALOG HANDLING

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
        updateTags();
    }

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
        for (Map.Entry<String, Uri> image : imageMap.entrySet()) {
            assert (item.getImagePaths().contains(image.getKey()));
        }
        database.uploadImages(imageMap, findViewById(android.R.id.content).getRootView());
    }
    @Override
    public void onOKPressed(Tag tag) {
        database.putTag(tag).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateTags();
            }
        });
    }



    /**
     * removes the filter from the recyclerView and refilters the list
     * @param position position of filter object that was clicked
     */
    @Override
    public void onRecyclerItemPressed(int position) {
        filters.remove(position);
        recyclerViewList.remove(position);
        activateFilters();
        filterRecyclerAdapter.notifyDataSetChanged();

    }
    /**
     * handles creating the dialog and switching to associated fragment
     */
    private void showAddItem() {
        new AddItemFragment().show(getSupportFragmentManager(), "ADD_ITEM");
    }

    private void showAddTag() {
        AddTagFragment addTagFragment = new AddTagFragment();
        addTagFragment.show(getSupportFragmentManager(), "ADD_TAG");

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

    private void showAddMenu() {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.add_element));
        popup.setOnMenuItemClickListener(item -> {
            // Handle item clicks here using if-else statements
            if (item.getItemId() == R.id.add_new_item) {
                showAddItem();
                return true;
            } else if (item.getItemId() == R.id.add_new_tag) {
                showAddTag();
                return true;
            } else {
                return false;
            }
        });
        popup.getMenuInflater().inflate(R.menu.add_menu, popup.getMenu());
        popup.show();
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
        } else if (id == R.id.add_element) {
            // show dialog for adding an item
            showAddMenu();
            //showAddItem();
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
        Button add_tags_button = findViewById(R.id.select_mode_add_tags);

        // bring ups popup with text to let the user know to select items now
        PopupMenu select_text_popup = new PopupMenu(this, this.findViewById(R.id.delete_item));
        select_text_popup.getMenuInflater().inflate(R.menu.select_item_text, select_text_popup.getMenu());
        select_text_popup.show();

        // make all the buttons visible and clickable
        confirm_button.setVisibility(View.VISIBLE);
        confirm_button.setClickable(true);
        cancel_button.setVisibility(View.VISIBLE);
        cancel_button.setClickable(true);
        add_tags_button.setVisibility(View.VISIBLE);
        add_tags_button.setClickable(true);

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

        // when the add tags button is pressed
        add_tags_button.setOnClickListener(v -> {
            defaultItemClickListener();

            // make the add tags screen appear
            List<String> selectedTags = new ArrayList<>();
            List<String> currentTagsStrList = new ArrayList<>();
            TagList currentTags;
            Set<String> allTagsSet = new HashSet<>();
            List<String> allTagsList = new ArrayList<>();
            TagList allTags;

            // Show the tag selection dialog
            TagList globalTagList = getGlobalTagList();
            TagSelectionDialog.show_selected(this, selectedTags, globalTagList, (dialog, which) -> {
                // Handle Confirm button click if needed
                Log.d("TagSelection", "Selected Tags: " + selectedTags);
            });


            // hide the buttons and make them not clickable so they aren not accidentally pressed
            confirm_button.setVisibility(View.INVISIBLE);
            confirm_button.setClickable(false);
            cancel_button.setVisibility(View.INVISIBLE);
            cancel_button.setClickable(false);
            add_tags_button.setVisibility(View.INVISIBLE);
            add_tags_button.setClickable(false);

            for (int i = itemList.size()-1; i > -1; i--) {
                // get the item at position i
                Item current_item = itemList.get(i);
                CheckBox checkbox = current_item.getCheckBox();
                if (checkbox != null){
                    if (checkbox.isChecked()){
                        // add the the selected tags to the item
//                        currentTags = current_item.getTags(); // get the list of tags already present for the item
//                        currentTagsStrList = currentTags.toList(); // convert it to a string list
//
//                        // use a set to prevent duplicates from being added
//                        allTagsSet = new HashSet<>();
//                        allTagsSet.addAll(currentTagsStrList);
//                        allTagsSet.addAll(selectedTags);
//
//                        // convert the set to a list
//                        allTagsList = new ArrayList<>(allTagsSet);
//
                        allTagsList.add("Boop");
                        // convert the list to a tag list
                        allTags = new TagList(allTagsList);

//                        allTags = new TagList(selectedTags);
                        // set the tags for the item to the list of all the tags
                        current_item.setTags(allTags);

                    }
                    // uncheck and hide the checkbox
                    checkbox.setChecked(false);
                    checkbox.setVisibility(View.INVISIBLE);
                }

            }

            updateList();
        });

        // when the confirm button is pressed
        confirm_button.setOnClickListener(v -> {
            defaultItemClickListener();

            // hide the buttons and make them not clickable so they aren not accidentally pressed
            confirm_button.setVisibility(View.INVISIBLE);
            confirm_button.setClickable(false);
            cancel_button.setVisibility(View.INVISIBLE);
            cancel_button.setClickable(false);
            add_tags_button.setVisibility(View.INVISIBLE);
            add_tags_button.setClickable(false);

            for (int i = itemList.size()-1; i > -1; i--) {
                // get the item at position i
                Item current_item = itemList.get(i);
                CheckBox checkbox = current_item.getCheckBox();
                if (checkbox != null){
                    if (checkbox.isChecked()){
                        // Delete item in firebase database
                        itemList.remove(current_item);
                        // Delete all associated images from firebase storage
                        database.deleteImages(current_item.getImagePaths());
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
            add_tags_button.setVisibility(View.INVISIBLE);
            add_tags_button.setClickable(false);

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
     * Handles sort menu creation.
     */
    public void showSortMenu() {
        PopupMenu popup = new PopupMenu(this, this.findViewById(R.id.sort_button));
        popup.setOnMenuItemClickListener(this::onSortMenuClick);
        popup.getMenuInflater().inflate(R.menu.sort_menu, popup.getMenu());
        popup.show();
    }

    /**
     * Handles clicking of sort button
     */
    public void onSortOrderButtonClick() {
        boolean order = toggleSortOrder();
        if (order) {
            sortOrderButton.setText(R.string.ascending);
        } else {
            sortOrderButton.setText(R.string.descending);
        }
    }

    /**
     * Handles clicking of sort menu items.
     * @param item the menu item that was clicked
     * @return true if an item is clicked, false otherwise
     */
    public boolean onSortMenuClick(MenuItem item) {
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
     * Changes the order items are sorted in.
     * @return True if the new sort order is ascending, otherwise false
     */
    public boolean toggleSortOrder() {
        sortAscending = !sortAscending;
        itemList.sort(sortMode, sortAscending);
        itemArrayAdapter.notifyDataSetChanged();
        activateFilters();
        return sortAscending;
    }

    // FILTER MENU HANDLING

    /**
     * Handles the menu creation after the "filter button" is tapped.
     */
    public void showFilterMenu() {
        // shows the menu of filterable objects
        PopupMenu popup = new PopupMenu(this, this.findViewById(R.id.filter_popup));
        popup.setOnMenuItemClickListener(this::onFilterMenuClick);
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
        popup.show();
    }

    /**
     * Handles clicking of filter menu items.
     * @param item the menu item that was clicked
     * @return true if an item is clicked, false otherwise
     */
    public boolean onFilterMenuClick(MenuItem item) {
        int itemClick = item.getItemId();
        clearButton.setVisibility(VISIBLE);
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
        } else {
            return false;
        }
    }

    /**
     * Shows and populates submenu for filtering by make.
     */
    public void showMakeSubMenu() {
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

    /**
     * Handles clicking of search button
     */
    public void onSearchButtonClick() {
        // if the search bar was visible, we just want to close it!
        boolean wasVisible = (searchBox.getVisibility() == View.VISIBLE);
        resetAdapter();
        if (!wasVisible) {
            showDescriptionSearch();

            // open the keyboard
            searchBox.post(new Runnable() {
                @Override
                public void run() {
                    // open the keyboard
                    if (searchBox.requestFocusFromTouch()) {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                .showSoftInput(searchBox, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            });
        }
        clearButton.setVisibility(VISIBLE);
    }

    /**
     * Handles clicking of clear filter button
     */
    public void onClearFilterClick() {
        resetAdapter();
    }

    // FILTERING UTILITY FUNCTIONS

    /**
     * Resets the adapter to the original ItemList.
     * resets the adapter to the original ItemList and the recyclerview of active filters
     */
    @SuppressLint("NotifyDataSetChanged")
    public void resetAdapter() {
        filters = new ArrayList<>();
        recyclerViewList.clear();
        toggleFilterVisibility();
        clearButton.setVisibility(GONE);
        filterRecyclerAdapter.notifyDataSetChanged();
        itemArrayAdapter = new CustomItemListAdapter(getApplicationContext(), itemList);
        itemListView.setAdapter(itemArrayAdapter);
        updateTotalValue();
    }

    /**
     * Toggles visibility of the date range and description search fields.
     * Sets entered text to nothing.
     */
    public void toggleFilterVisibility() {
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
            //searchBox.setQuery(getIntent().getDataString(), false);
        }
    }

    // FILTERING LOGIC FUNCTIONS

    /**
     * Constructs filters from the filter set and applies them to the data.
     * @see CustomItemListAdapter custom filter
     */
    @SuppressLint("NotifyDataSetChanged")
    public void activateFilters() {
        itemArrayAdapter = new CustomItemListAdapter(getApplicationContext(), itemList);
        recyclerViewList.clear();
        Gson gson = new Gson();
        String json = gson.toJson(filters);
        itemArrayAdapter.getFilter().filter(json, count -> {
            itemListView.setAdapter(itemArrayAdapter); // putting in listener prevents blinking
            updateTotalValue(); // uses sum of values of filtered items

            // basically recreates the recyclerView list
            for(int i = 0; i < filters.size(); i++) {
                // skip the string identifier and add to recyclerView for visual purposes
                String item = filters.get(i).toString();
                item = item.substring(1);
                recyclerViewList.add(item);
            }
            filterView.setVisibility(VISIBLE);
            filterRecyclerAdapter.notifyDataSetChanged();
            itemArrayAdapter.getFilter().filter(json);
            itemListView.setAdapter(itemArrayAdapter);
        });
    }

    /**
     * Handles click events for make submenu.
     * @param menuItem the item clicked
     * @return true to avoid unintended calls to other functions
     */
    public boolean onMakeClick(MenuItem menuItem) {
        toggleFilterVisibility();
        // update adapter to show filtered results
        String makeCheck = "M" + menuItem.toString();
        if (!(filters.contains(makeCheck))) {
            filters.add('M' + menuItem.toString());
        }

        activateFilters();
        return true;
    }

    /**
     * Handles filtering itemList for description, creates a SearchView to search for a description.
     */
    public void showDescriptionSearch() {
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

                // adds the description to filters if it is not already a filter (prevents duplicates)
                if (newText.length() > 0) {
                    String descCheck = "D" + newText;
                    if(!(filters.contains(descCheck))) {
                        filters.add('D' + newText);
                    }
                }
                activateFilters();
                return false;
            }
        });
    }

    /**
     * Handles filtering by date, checks for valid date then creates a new list
     * for the adapter to latch on to.
     */
    public void showDateFilter() {
        toggleFilterVisibility();
        startDate.setVisibility(VISIBLE);
        endDate.setVisibility(VISIBLE);
        filterDateButton.setVisibility(VISIBLE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        // when pressed, it will filter the dates by range entered, display error message otherwise
        filterDateButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Fixes a date's format to comply with a uniform dd/MM/yyyy format.
             * Throws an exception if invalid date.
             * @param input Date in d/M/yyyy format (e.g. 01/1/2000)
             * @return Date in dd/MM/yyyy format (e.g. 01/01/2000)
             */
            public String fixDateFormatting(String input) {
                return LocalDate.parse(input, formatter)
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }


            @SuppressLint("NotifyDataSetChanged")
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
                        startString = fixDateFormatting(startString);
                        String dateCheck =  "1" + startString;
                        if (!(filters.contains(dateCheck))) {
                            filters.add('1' + startString);
                        }
                    }

                    if (endString.length() > 0) {
                        endString = fixDateFormatting(endString);
                        String dateCheck =  "2" + endString;
                        if (!(filters.contains(dateCheck))) {
                            filters.add('2' + endString);
                        }

                    }

                    // update adapter to new filter
                    dateErrorMsg.setVisibility(GONE);
                    activateFilters();
                } catch (DateTimeParseException e) {
                    dateErrorMsg.setVisibility(VISIBLE);
                    // reset filters - avoids bugs
                    filters = new ArrayList<>();
                    recyclerViewList.clear();
                    activateFilters();
                    filterRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}