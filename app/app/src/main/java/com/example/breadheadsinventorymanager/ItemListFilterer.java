package com.example.breadheadsinventorymanager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Handles sorting and filtering of the item list in MainActivity.
 */
public class ItemListFilterer {
    private final Activity activity;
    private final ItemListUI itemListUI;
    private Button sortOrderButton;
    private Button filterDateButton;
    private ImageButton clearButton;
    private SearchView searchBox;
    private EditText startDate;
    private EditText endDate;
    private TextView dateErrorMsg;
    private ImageButton sortButton;
    private ImageButton filterButton;
    private ImageButton searchButton;

    // stores the filters to apply to the adapter
    // first character in each filter determines the filter type
    // 'D': description. '1'/'2': lower/upper bound on date resp. 'M': make.
    // multiple makes can be applied at once! multiple filters are ANDed; different makes are ORed.
    private ArrayList<CharSequence> filters = new ArrayList<>();
    ArrayList<String> recyclerViewList;


    /**
     * Constructs an ItemListFilterer from provided itemListUI
     */
    protected ItemListFilterer(ItemListUI itemListUI) {
        this.itemListUI = itemListUI;
        this.activity = itemListUI.getActivity();
        retrieveButtonsFromActivity();
    }

    /**
     * Constructs an ItemListFilterer from provided itemListUI and given ArrayList to act as
     * a recycler view list
     */
    protected ItemListFilterer(ItemListUI itemListUI, ArrayList<String> recyclerViewList) {
        this.itemListUI = itemListUI;
        this.activity = itemListUI.getActivity();
        this.recyclerViewList = recyclerViewList;
        retrieveButtonsFromActivity();
    }

    /**
     * Attempts to retrieve buttons via findViewById calls on the filterer's activity.
     */
    public void retrieveButtonsFromActivity() {
        sortOrderButton = activity.findViewById(R.id.sort_order_button);
        filterDateButton = activity.findViewById(R.id.date_filter_button);
        searchBox = activity.findViewById(R.id.search_view);
        sortButton = activity.findViewById(R.id.sort_button);
        filterButton = activity.findViewById(R.id.filter_popup);
        searchButton = activity.findViewById(R.id.quick_search);
        clearButton = activity.findViewById(R.id.clear_filter);

        // date filtering
        startDate = activity.findViewById(R.id.filter_date_start);
        endDate = activity.findViewById(R.id.filter_date_end);
        dateErrorMsg = activity.findViewById(R.id.invalid_date_message);


    }

    /**
     * Sets up listeners for UI elements.
     */
    public void setupListeners() {
        sortButton.setOnClickListener(v -> itemListUI.getItemListFilterer().showSortMenu());
        sortOrderButton.setOnClickListener(v -> itemListUI.getItemListFilterer().onSortOrderButtonClick());
        filterButton.setOnClickListener(v -> itemListUI.getItemListFilterer().showFilterMenu());
        searchButton.setOnClickListener(v -> itemListUI.getItemListFilterer().onSearchButtonClick());
        clearButton.setOnClickListener(v -> itemListUI.getItemListFilterer().onClearFilterClick());
    }

    // SORT MENU HANDLING

    /**
     * Handles sort menu creation.
     */
    public void showSortMenu() {
        PopupMenu popup = new PopupMenu(activity, activity.findViewById(R.id.sort_button));
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
            itemListUI.setSortMode("date");
        } else if (itemClick == R.id.sort_desc) {
            itemListUI.setSortMode("description");
        } else if (itemClick == R.id.sort_make) {
            itemListUI.setSortMode("make");
        } else if (itemClick == R.id.sort_value) {
            itemListUI.setSortMode("value");
        } else if (itemClick == R.id.sort_tags){
            itemListUI.setSortMode("tag");
        }
        else {
            return false;
        }

        itemListUI.getItemList().sort(itemListUI.getSortMode(), itemListUI.isSortAscending());
        itemListUI.getItemArrayAdapter().notifyDataSetChanged();
        activateFilters();
        return true;
    }

    /**
     * Changes the order items are sorted in.
     * @return True if the new sort order is ascending, otherwise false
     */
    public boolean toggleSortOrder() {
        itemListUI.setSortAscending(!itemListUI.isSortAscending());
        itemListUI.getItemList().sort(itemListUI.getSortMode(), itemListUI.isSortAscending());
        itemListUI.getItemArrayAdapter().notifyDataSetChanged();
        activateFilters();
        return itemListUI.isSortAscending();
    }

    // FILTER MENU HANDLING

    /**
     * Handles the menu creation after the "filter button" is tapped.
     */
    public void showFilterMenu() {
        // shows the menu of filterable objects
        PopupMenu popup = new PopupMenu(activity, activity.findViewById(R.id.filter_popup));
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
        } else if (itemClick == R.id.tag_menu){
            showTagSubMenu();
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
        makeList = itemListUI.getItemList().getMakeList();
        PopupMenu popup = new PopupMenu(activity, activity.findViewById(R.id.filter_popup));

        // populate the submenu with makeList strings
        for(int i = 0; i < makeList.size(); i++) {
            popup.getMenu().add(makeList.get(i));
        }
        popup.setOnMenuItemClickListener(this::onMakeClick);
        popup.getMenuInflater().inflate(R.menu.filter_make_submenu, popup.getMenu());
        popup.show();
    }

    public void showTagSubMenu() {
        // show submenu of all available tags
        ArrayList<String> tagList;
        tagList = itemListUI.getGlobalTagList().toArrayList();
        PopupMenu popup = new PopupMenu(activity, activity.findViewById(R.id.filter_popup));

        // populate the submenu with makeList strings
        for(int i = 0; i < tagList.size(); i++) {
            popup.getMenu().add(tagList.get(i));
        }
        popup.setOnMenuItemClickListener(this::onTagClick);
        popup.getMenuInflater().inflate(R.menu.filter_tag_submenu, popup.getMenu());
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
                        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
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
        itemListUI.getFilterAdapter().notifyDataSetChanged();
        itemListUI.setItemArrayAdapter(new CustomItemListAdapter(activity.getApplicationContext(),
                itemListUI.getItemList()));
        itemListUI.getItemListView().setAdapter(itemListUI.getItemArrayAdapter());
        itemListUI.updateTotalValue();
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
        itemListUI.setItemArrayAdapter(new CustomItemListAdapter(activity.getApplicationContext(),
                itemListUI.getItemList()));
        recyclerViewList.clear();
        Gson gson = new Gson();
        String json = gson.toJson(filters);
        itemListUI.getItemArrayAdapter().getFilter().filter(json, count -> {
            // putting in listener prevents blinking
            itemListUI.getItemListView().setAdapter(itemListUI.getItemArrayAdapter());
            itemListUI.updateTotalValue(); // uses sum of values of filtered items

            // basically recreates the recyclerView list
            for(int i = 0; i < filters.size(); i++) {
                // skip the string identifier and add to recyclerView for visual purposes
                String item = filters.get(i).toString();
                item = item.substring(1);
                recyclerViewList.add(item);
            }
            itemListUI.getFilterView().setVisibility(VISIBLE);
            itemListUI.getFilterAdapter().notifyDataSetChanged();
            itemListUI.getItemArrayAdapter().getFilter().filter(json);
            itemListUI.getItemListView().setAdapter(itemListUI.getItemArrayAdapter());
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
     * Handles click events for Tag submenu.
     * @param menuItem the item clicked
     * @return true to avoid unintended calls to other functions
     */
    public boolean onTagClick(MenuItem menuItem) {
        toggleFilterVisibility();
        // update adapter to show filtered results
        String makeCheck = "T" + menuItem.toString();
        if (!(filters.contains(makeCheck))) {
            filters.add('T' + menuItem.toString());
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
                    itemListUI.getFilterAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Removes an item from the filter
     * @param position Integer representing position of the filter
     */
    public void removeFilter(int position) {
        filters.remove(position);
        recyclerViewList.remove(position);
        activateFilters();
        itemListUI.getFilterAdapter().notifyDataSetChanged();
    }
}
