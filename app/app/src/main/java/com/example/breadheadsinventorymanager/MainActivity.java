package com.example.breadheadsinventorymanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

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
        /*
        // testing
        HashMap<String, String> data = new HashMap<>();
        data.put("Make", "Toyota");
        data.put("Model", "Tacoma");
        itemDB.document().set(data);
        
         */
    }
}