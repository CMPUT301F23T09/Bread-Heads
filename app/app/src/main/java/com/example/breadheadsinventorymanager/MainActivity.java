package com.example.breadheadsinventorymanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Main activity
 *
 * @version
 * 0.1
 */
public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore database;
    private CollectionReference itemDB;
    private CollectionReference userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.user_icon);

        // initialize firestore database
        // adapted from lab 5 instructions https://eclass.srv.ualberta.ca/pluginfile.php/10037758/mod_resource/content/3/Lab%205%20Instructions.pdf
        database = FirebaseFirestore.getInstance();

        // TODO: finish this functionality and move it to ItemList class
        itemDB = database.collection("items");
        itemDB.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) { // error handling
                    Log.e("Firestore", error.toString());
                    return;
                }

                if (value != null) {
                    // TODO: update contents of class, etc.
                }
            }
        });

        // TODO: finish this functionality and move it to UserList class
        userDB = database.collection("users");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Do stuff
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}