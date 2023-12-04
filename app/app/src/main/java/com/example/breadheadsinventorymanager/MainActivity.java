package com.example.breadheadsinventorymanager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.CloseGuard;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
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
 * @version 3
 */
public class MainActivity extends AppCompatActivity implements
        AddItemFragment.OnFragmentInteractionListener, AddTagFragment.OnFragmentInteractionListener{
    GoogleSignInAccount account = null; // the signed in Google account

    ItemListUI itemListUI;

    private boolean doneInitial = false; // tracks whether we've retrieved initial Firestore info

    // obligatory id's for lists/adapter
    private FirestoreInteract database;

    // stores information about how the list is currently sorted

    private final ArrayList<String> recyclerViewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.user_icon);

        learnAccount(getIntent().getBooleanExtra("skip_auth", false));

        // The recyclerView for displaying active filters setup
        RecyclerView filterView = findViewById(R.id.active_filter_recycler_view);
        // id for search box to filter by description
        Button sortOrderButton = findViewById(R.id.sort_order_button);

        // filter recyclerView setup
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        FilterRecyclerAdapter filterRecyclerAdapter = new FilterRecyclerAdapter(
                getApplicationContext(), recyclerViewList, this);
        filterView.setLayoutManager(linearLayoutManager);
        filterView.setAdapter(filterRecyclerAdapter);

        // Firestore, ListView, and adapter setup
        database = new FirestoreInteract();
        Task<Void> alignTask = database.alignToAccount(account);
        itemListUI = new ItemListUI(this, new ItemList(), new TagList());
        itemListUI.setItemListView(findViewById(R.id.items_main_list));
        itemListUI.setFilterView(filterView);
        itemListUI.buildItemArrayAdapter();
        itemListUI.setTotalValueView(findViewById(R.id.total_value));
        itemListUI.setDatabase(database);
        itemListUI.setItemListFilterer(new ItemListFilterer(itemListUI, recyclerViewList));

        // we don't always need to wait on a task
        if (alignTask != null) {
            alignTask.addOnCompleteListener(task -> initialRead());
        } else { initialRead(); }

        itemListUI.getItemListFilterer().setupListeners();
    }

    /**
     * Calls initialRead() from ItemListUI class and, when done, changes the doneInitial boolean
     * flag to indicate that the initial read is completed.
     */
    public void initialRead() {
        itemListUI.initialRead().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                doneInitial = true;
            }
        });
    }


    /**
     * Attempts to set the current GoogleSignInAccount account, or if unable, opens the UserActivity
     * so that users can authenticate.
     * @param skipAuth True if authentification should be skipped
     */
    private void learnAccount(boolean skipAuth) {
        if (skipAuth) {
            account = null;
            return;
        }

        GoogleSignInAccount lastSignIn = GoogleSignIn.getLastSignedInAccount(this);
        GoogleSignInAccount passedAccount = getIntent().getParcelableExtra("account");
        if (passedAccount != null) {
            // account passed from UserActivity
            account = passedAccount;
        } else if (lastSignIn != null) {
            // account previously signed in
            account = lastSignIn;
        } else {
            // no account found; open UserActivity
            this.startActivity(new Intent(this, UserActivity.class));
        }
    }

    @Override
    protected void onResume() {
        // we need to update the list whenever we return from another activity
        super.onResume();
        // if we don't make sure we've already done our initial setup,
        // then we may try to update something that doesn't exist (= crash!)
        if (doneInitial) {
            itemListUI.updateList();
            itemListUI.updateTags();
        }
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
                itemListUI.updateList();
            }
        });

        // Upload images
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
                itemListUI.updateTags();
            }
        });
    }



    /**
     * removes the filter from the recyclerView and refilters the list
     * @param position position of filter object that was clicked
     */
    @Override
    public void onRecyclerItemPressed(int position) {
        itemListUI.removeFilter(position);
    }

    /**
     * Handles creating the add item dialog and switching to associated fragment
     */
    private void showAddItem() {
        new AddItemFragment().show(getSupportFragmentManager(), "ADD_ITEM");
    }

    /**
     * Handles creating the add tag dialog and switching to associated fragment
     */
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

    /**
     * Shows the option to add either a new item or a new tag.
     */
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
            Intent intent = new Intent(this, UserLoggedInActivity.class);
            intent.putExtra("account", account);
            this.startActivity(intent);
            return true;
        } else if (id == R.id.add_element) {
            // show dialog for adding an item
            showAddMenu();
            //showAddItem();
            return true;
        } else if (id == R.id.delete_item) {
            // enter select mode to be able to delete one or more items
            itemListUI.selectMode(findViewById(R.id.select_mode_confirm),
                                  findViewById(R.id.select_mode_cancel),
                                  findViewById(R.id.select_mode_add_tags),
                    new PopupMenu(this, this.findViewById(R.id.delete_item)),
                    R.menu.select_item_text,
                    account);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Gets the UI handler for the item list.
     * @return An ItemListUI object tied to the item list.
     */
    public ItemListUI getItemListUI() {
        return itemListUI;
    }

    /**
     * Sets the UI handler for the item list.
     * @param itemListUI An ItemListUI object to change the UI handler to.
     */
    public void setItemListUI(ItemListUI itemListUI) {
        this.itemListUI = itemListUI;
    }
}