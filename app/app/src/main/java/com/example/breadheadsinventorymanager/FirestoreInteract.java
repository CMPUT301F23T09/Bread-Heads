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

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Class to interact with Firestore. Keeps track of the database and collections, and both sends
 * and retrieves data when necessary.
 * @version 1.2
 * @see FirestorePuttable
 */
public class FirestoreInteract {
    private FirebaseFirestore database; // can't be static; would create memory leak (uses context)
    // For storing images
    private FirebaseStorage storage;

    // static variables that point to collections in the Firebase database
    private static CollectionReference itemDB;
    private static CollectionReference userDB;
    private static CollectionReference testDB;
    private static CollectionReference tagDB;


    // points to the Firestore storage for images (large files)
    private static StorageReference storageReference;


    /**
     * Initialize firestore database using defaults.
     * Adapted from lab 5 instructions.
     */
    public FirestoreInteract() {
        database = FirebaseFirestore.getInstance();
        itemDB = database.collection("items");
        userDB = database.collection("users");
        testDB = database.collection("test");
        tagDB = database.collection("tags");
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
     * Initialize firestore database given specific variables.
     */
    public FirestoreInteract(FirebaseFirestore database, CollectionReference itemDB, CollectionReference userDB) {
        this.database = database;
        FirestoreInteract.itemDB = itemDB;
        FirestoreInteract.userDB = userDB;
    }

    /**
     * Attempts to put the given object into the items collection on Firestore.
     *
     * @param obj The object to put into the collection.
     * @return The update task
     */
    public Task<Void> putItem(Item obj) {
        obj.put(itemDB);
        if (obj.getId() != null) {
            return itemDB.document(obj.getId()).set(obj.formatForFirestore());
        } else {
            DocumentReference doc = itemDB.document(); // firestore will generate ID for us
            Task<Void> task = doc.set(obj.formatForFirestore());
            return task.addOnCompleteListener(task1 -> {
                obj.setId(doc.getId()); // make sure this item's ID matches firestore's
            });
        }
    }

    /**
     * Attempts to put the given Tag into the tags collection on Firestore.
     *
     * @param tag The Tag to put into the collection.
     * @return The update task
     */
    public Task<Void> putTag(Tag tag) {
        tag.put(tagDB);
        if (tag.getId() != null) {
            return tagDB.document(tag.getId()).set(tag.formatForFirestore());
        } else {
            DocumentReference doc = tagDB.document(); // firestore will generate ID for us
            Task<Void> task = doc.set(tag.formatForFirestore());
            return task.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    tag.setId(doc.getId());
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
        return itemDB.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("FirestoreInteract.java", document.getId() + " => " + document.getData());
                    list.add(new Item(document));
                }
            } else {
                Log.d("FirestoreInteract.java", "Error getting documents: ", task.getException());
            }
        });
    }

    public Task<QuerySnapshot> populateWithTags(TagList list) {
        return tagDB.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("FirestoreInteract.java", document.getId() + " => " + document.getData());
                        list.addTag(new Tag(document));
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
     * Task to delete an item from Firestore item collection
     * @param obj The FirestorePuttable object representing the item to delete.
     * @return A Firestore deletion task. Use .addOnSuccessListener() to handle success.
     */
    public Task<Void> deleteItem(FirestorePuttable obj) {
        return deleteItem(obj.getId());
    }

    /**
     * Task to delete a tag from the tags collection with the specified ID
     * @param id The ID of the tag to delete
     * @return The task; use .addOnSuccessListener() to do something after deletion
     */
    public Task<Void> deleteTag(String id) {
        return tagDB.document(id).delete();
    }
    /**
     *
     * Task to delete an item from Firestore item collection
     * @param tag The FirestorePuttable object representing the item to delete.
     * @return A Firestore deletion task. Use .addOnSuccessListener() to handle success.
     */
    public Task<Void> deleteTag(FirestorePuttable tag) {
        return deleteTag(tag.getId());
    }

    /**
     * Gets the Firebase database
     */
    public FirebaseFirestore getDatabase() {
        return database;
    }

    /**
     * Sets the Firebase database
     */
    public void setDatabase(FirebaseFirestore database) {
        this.database = database;
    }

    /**
     * Gets the (static) item collection
     */
    public static CollectionReference getItemDB() {
        return itemDB;
    }

    /**
     * Sets the (static) item collection
     */
    public static void setItemDB(CollectionReference itemDB) {
        FirestoreInteract.itemDB = itemDB;
    }

    /**
     * Gets the (static) user collection
     */
    public static CollectionReference getUserDB() {
        return userDB;
    }

    /**
     * Sets the (static) item collection
     */
    public static void setUserDB(CollectionReference userDB) {
        FirestoreInteract.userDB = userDB;
    }


    /**
     * Gets the (static) test item collection
     */
    public static CollectionReference getTestDB() {
        return testDB;
    }

    /**
     * Sets the (static) test item collection
     */
    public static void setTestDB(CollectionReference testDB) {
        FirestoreInteract.testDB = testDB;
    }
  
    public static StorageReference getStorageReference() { return storageReference; }
  
    /**
     * Gets the (static) tag collection
     */
    public static CollectionReference getTagDB() {
        return tagDB;
    }

    /**
     * Sets the (static) tag collection
     */
    public static void setTagDB(CollectionReference tagDB) {
        FirestoreInteract.tagDB = tagDB;
    }
}
