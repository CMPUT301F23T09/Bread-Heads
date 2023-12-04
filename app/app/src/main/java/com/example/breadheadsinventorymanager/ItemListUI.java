package com.example.breadheadsinventorymanager;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles UI functions of the item list in the MainActivity.
 *
 * Sorting/filtering functionality is not handled directly by this class, but rather by an
 * associated ItemListFilterer. See UML diagram for a visualization.
 *
 * @see MainActivity
 */
public class ItemListUI {
    private final Activity activity;
    private ListView itemListView;
    private CustomItemListAdapter itemArrayAdapter;
    private ItemList itemList;
    private TagList tagList;
    private TextView totalValueView;
    private FirestoreInteract database;
    private ItemListFilterer itemListFilterer;

    // stores information about how the list is currently sorted
    private String sortMode = "description"; // which field to sort by
    private boolean sortAscending = true; // whether to sort in ascending or descending order


    // The recyclerView for displaying active filters setup
    private RecyclerView filterView;

    /**
     * Constructs the itemListUI from given activity and lists.
     */
    protected ItemListUI(Activity activity, ItemList itemList, TagList tagList) {
        this.activity = activity;
        this.itemList = itemList;
        this.tagList = tagList;
    }

    public void setItemListView(ListView itemListView) {
        this.itemListView = itemListView;
    }

    /**
     * Sets the item array adapter by building it from the activity and item list
     */
    public void buildItemArrayAdapter() {
        itemArrayAdapter = new CustomItemListAdapter(activity, itemList);
    }

    public void setItemArrayAdapter(CustomItemListAdapter itemArrayAdapter) {
        this.itemArrayAdapter = itemArrayAdapter;
    }

    public void setFilterView(RecyclerView filterView) {
        this.filterView = filterView;
    }

    public void setTotalValueView(TextView totalValueView) {
        this.totalValueView = totalValueView;
    }

    public void setDatabase(FirestoreInteract database) {
        this.database = database;
    }

    public void setItemListFilterer(ItemListFilterer itemListFilterer) {
        this.itemListFilterer = itemListFilterer;
    }

    /**
     * Reads the tags and list from Firestore database.
     *
     * @return a task for when the initial read is done
     */
    public Task<QuerySnapshot> initialRead() {
        updateTags();
        return updateList().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                defaultItemClickListener();
            }
        });
    }

    /**
     * Sets itemListView's onItemClickListener to the default.
     */
    public void defaultItemClickListener() {
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem = itemArrayAdapter.getItem(position);
                Intent intent = new Intent(activity, ItemDetailsActivity.class);
                intent.putExtra("item", selectedItem);
                intent.putExtra("tagList", tagList);
                activity.startActivity(intent);
            }
        });
    }

    /**
     * Updates the total value displayed at the bottom of the screen
     */
    public void updateTotalValue() {
        totalValueView.setText(activity.getString(R.string.totalValueTitle,
                itemArrayAdapter.getSumAsDollarString()));
        totalValueView.setVisibility(View.VISIBLE);
    }

    /**
     * Updates the contents of itemList with the contents of the Firestore database
     * @return A Task tracking the update
     */
    public Task<QuerySnapshot> updateList() {
        itemList = new ItemList();
        return database.populateWithItems(itemList).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                itemList.sort(sortMode, sortAscending);
                itemArrayAdapter = new CustomItemListAdapter(activity.getApplicationContext(), itemList);
                itemListView.setAdapter(itemArrayAdapter);
                itemListFilterer.activateFilters();
            }
        });
    }

    /**
     * Handles when the delete button is pressed, which causes the app to enter "select mode".
     * Causes checkboxes to appear that allow users to select item elements.
     */
    public void selectMode(Button confirm_button, Button cancel_button, Button add_tags_button,
                           PopupMenu select_text_popup, int select_item_text, GoogleSignInAccount account) {
        // bring ups popup with text to let the user know to select items now
        select_text_popup.getMenuInflater().inflate(select_item_text, select_text_popup.getMenu());
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
            List<CheckBox> checkBoxes = new ArrayList<>();
            for (int i = itemList.size()-1; i > -1; i--) {
                // get the item at position i
                Item current_item = itemList.get(i);
                CheckBox checkbox = current_item.getCheckBox();
                checkBoxes.add(checkbox);
                Log.d("Checkbox", "Item:" + current_item.getDescription() + " Checkbox:" + checkbox.isChecked());
            }
            // reverse the order of the checkbox list to
            Collections.reverse(checkBoxes);
            // make the add tags screen appear
            List<String> selectedTags = new ArrayList<>();

            // Show the tag selection dialog
            TagList globalTagList = getGlobalTagList();
            TagSelectionDialog.show_selected(activity, selectedTags, globalTagList, (dialog, which) -> {
                // Handle Confirm button click if needed
                Log.d("TagSelection", "Selected Tags: " + selectedTags);

                TagList currentTags;

                for (int i = itemList.size()-1; i > -1; i--) {
                    // get the item at position i
                    Item current_item = itemList.get(i);
                    CheckBox checkbox = current_item.getCheckBox();

                    if (checkbox != null){
                        if(checkBoxes.get(i).isChecked()){
                            Log.d("AddTagsToMultipleItems", "Item Name: " + current_item.getDescription());

                            // add the the selected tags to the item
                            Log.d("AddTagsToMultipleItems", "Starting TagList: " + current_item.getTags());
                            currentTags = current_item.getTags();
                            TagList selectedTagList = new TagList(selectedTags);
                            for (Tag aTag : selectedTagList){
                                currentTags.addTag(aTag);

                            }

                            Log.d("AddTagsToMultipleItems", "End TagList: " + current_item.getTags());

                            // update the tag list for the item in firebase
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            User currentUser = new User(account);
                            currentUser.getId();
                            DocumentReference docRef = db.collection("users").document(currentUser.getId());
                            docRef = docRef.collection("items").document(current_item.getId());
                            docRef.update("tags",current_item.getTags())
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Tags updated successfully!");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error updating tags", e);
                                    });

                        }
                        // uncheck and hide the checkbox
                        checkbox.setChecked(false);
                        checkbox.setVisibility(View.INVISIBLE);
                    }

                }
                updateList();
            });

            // hide the buttons and make them not clickable so they are not accidentally pressed
            confirm_button.setVisibility(View.INVISIBLE);
            confirm_button.setClickable(false);
            cancel_button.setVisibility(View.INVISIBLE);
            cancel_button.setClickable(false);
            add_tags_button.setVisibility(View.INVISIBLE);
            add_tags_button.setClickable(false);
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



    /**
     * Updates the contents of tagList with the contents of the Firestore database
     * @return A Task tracking the update
     */
    public Task<QuerySnapshot> updateTags() {
        tagList = new TagList();
        return database.populateWithTags(tagList).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
            }
        });
    }

    /**
     * Getter for global tag list
     */
    public TagList getGlobalTagList() {
        // Implement this method to return the global tag list
        // For example, if globalTagList is a field in MainActivity:
        // return globalTagList;
        return tagList;
    }

    public Activity getActivity() {
        return activity;
    }

    public ListView getItemListView() {
        return itemListView;
    }

    public CustomItemListAdapter getItemArrayAdapter() {
        return itemArrayAdapter;
    }

    public ItemList getItemList() {
        return itemList;
    }

    public TagList getTagList() {
        return tagList;
    }

    public TextView getTotalValueView() {
        return totalValueView;
    }

    public FirestoreInteract getDatabase() {
        return database;
    }

    public ItemListFilterer getItemListFilterer() {
        return itemListFilterer;
    }

    public String getSortMode() {
        return sortMode;
    }

    public boolean isSortAscending() {
        return sortAscending;
    }

    public void setSortMode(String sortMode) {
        this.sortMode = sortMode;
    }

    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public RecyclerView getFilterView() {
        return filterView;
    }

    public LinearLayoutManager getFilterLayoutManager() {
        return (LinearLayoutManager) filterView.getLayoutManager();
    }

    public FilterRecyclerAdapter getFilterAdapter() {
        return (FilterRecyclerAdapter) filterView.getAdapter();
    }

    /**
     * Removes an item from the filter
     * @param position Integer representing position of the filter
     */
    public void removeFilter(int position) {
        itemListFilterer.removeFilter(position);
    }
}
