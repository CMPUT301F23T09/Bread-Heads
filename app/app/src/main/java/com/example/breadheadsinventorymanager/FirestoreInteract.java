package com.example.breadheadsinventorymanager;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Class to interact with Firestore
 * @version 1.1
 */
public class FirestoreInteract {
    private FirebaseFirestore database; // can't be static; would create memory leak (uses context)
    // For storing images
    private FirebaseStorage storage;

    // static variables that point to collections in the Firebase database
    private static CollectionReference itemDB;
    private static CollectionReference userDB;
    private static CollectionReference testDB;

    // points to the Firestore storage for images (large files)
    private static StorageReference storageReference;


    /**
     * Initialize firestore database.
     * Adapted from lab 5 instructions.
     */
    public FirestoreInteract() {
        database = FirebaseFirestore.getInstance();
        itemDB = database.collection("items");
        userDB = database.collection("users");
        testDB = database.collection("test");

        // Images
        storage = FirebaseStorage.getInstance(); //maybe add an imageDB = storage.getReference("images")?
        storageReference = storage.getReference();
    }

    /**
     * Uploads images that are provided with a reference path to its prospective location on firebase storage
     * @param imageMap: maps image paths to the Uri image resource
     * @param view: the current view to display upload failure/success messages
     */
    public void uploadImages(Map<String, Uri> imageMap, View view) {
        Iterator<Map.Entry<String, Uri>> it = imageMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Uri> image = (Map.Entry<String, Uri>) it.next();
            StorageReference imageRef = storageReference.child(image.getKey());

            imageRef.putFile(image.getValue())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(view.getContext(), R.string.image_uploaded_notification, Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), R.string.image_upload_failed_message, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    /**
     * Attempts to put the given object into the Item collection on Firestore.
     *
     * @param obj The object to put into the collection.
     * @return The update task
     */
    public Task<Void> putItem(Item obj) {
        obj.put(itemDB);
        if (obj.getId() != null) {
            return itemDB.document(obj.getId()).set(obj.formatForFirestore());
        }
        else {
            DocumentReference doc = itemDB.document(); // firestore will generate ID for us
            Task<Void> task = doc.set(obj.formatForFirestore());
            return task.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    obj.setId(doc.getId()); // make sure this item's ID matches firestore's
                }
            });
        }
    }

    /**
     * Task to populate a given ArrayList with the contents of the Items collection.
     * Must return as a task because of inherent delays in accessing Firestore.
     * Execution is asynchronous so we must listen for the data to be available!
     * @param list ArrayList of Items (probably an ItemList) to place retrieved Items into
     * @return a Task; use .addOnSuccessListener() to run code after data retrieval
     */
    public Task<QuerySnapshot> populateWithItems(ArrayList<Item> list) {
        return itemDB.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("FirestoreInteract.java", document.getId() + " => " + document.getData());
                        list.add(new Item(document));
                    }
                } else {
                    Log.d("FirestoreInteract.java", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * Task to populate a given ArrayList with the contents of the test collection.
     * Must return as a task because of inherent delays in accessing Firestore.
     * Execution is asynchronous so we must listen for the data to be available!
     * @param list ArrayList of Items (probably an ItemList) to place retrieved tests into
     * @return a Task; use .addOnSuccessListener() to run code after data retrieval
     */
    public Task<QuerySnapshot> populateWithTest(ArrayList<Item> list) {
        return testDB.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("FirestoreInteract.java", document.getId() + " => " + document.getData());
                        list.add(new Item(document));
                    }
                } else {
                    Log.d("FirestoreInteract.java", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * Task to delete the item from the items collection with the specified ID
     * @param id The ID of the item to delete
     * @return The task; use .addOnSuccessListener() to do something after deletion
     */
    public Task<Void> deleteItem(String id) {
        return itemDB.document(id).delete();
    }

    /**
     * Deletes an item from Firestore item collection
     * @param obj The FirestorePuttable object representing the item to delete.
     * @return A Firestore deletion task. Use .addOnSuccessListener() to handle success.
     */
    public Task<Void> deleteItem(FirestorePuttable obj) {
        String itemId = obj.getId();
        Task<Void> firestoreDeleteTask = deleteItem(itemId);
        return firestoreDeleteTask;
    }

    public FirebaseFirestore getDatabase() {
        return database;
    }

    public void setDatabase(FirebaseFirestore database) {
        this.database = database;
    }

    public static CollectionReference getItemDB() {
        return itemDB;
    }

    public static void setItemDB(CollectionReference itemDB) {
        FirestoreInteract.itemDB = itemDB;
    }

    public static CollectionReference getUserDB() {
        return userDB;
    }

    public static void setUserDB(CollectionReference userDB) {
        FirestoreInteract.userDB = userDB;
    }

    public static CollectionReference getTestDB() {
        return testDB;
    }

    public static void setTestDB(CollectionReference testDB) {
        FirestoreInteract.testDB = testDB;
    }

    public static StorageReference getStorageReference() { return storageReference; }
}

/* example of how to call data from firestore to populate an ItemList
public void firestoreExample() {
    FirestoreInteract firestoreInteract = new FirestoreInteract();
    ItemList list = new ItemList();
    Task<QuerySnapshot> task = firestoreInteract.populateWithTest(list);
    // do some sort of "loading/please wait" screen
    task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            // end our "loading/please wait" screen

            // logging for testing:
            if (2 != list.size()) { throw new RuntimeException("oops");}
            Log.i("main", list.get(0).formatForFirestore().toString());
            Log.i("main", list.get(1).formatForFirestore().toString());
        }
    });
}
*/
