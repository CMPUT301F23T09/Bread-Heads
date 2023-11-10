package com.example.breadheadsinventorymanager;

import com.google.firebase.firestore.CollectionReference;

import java.util.HashMap;

/**
 * Implementing this allows for objects to be put into a Firestore database collection.
 * @see FirestoreInteract
 */
public interface FirestorePuttable {
    /**
     * Gets the data associated with the object in a format suitable for Firestore
     * @return
     * A HashMap containing the object's data that can be added to Firestore
     */
    HashMap<String, Object> formatForFirestore();

    /**
     * Puts the object into a collection. Creates an entry for the object if one doesn't already
     * exist, or otherwise edits an existing entry.
     * @param collection Reference to the collection to put the object into.
     */
    void put(CollectionReference collection);

    /**
     * Sets the item's DB in the database.
     * ID should default to null. If it isn't stored by the class, just return null.
     */
    void setId(String id);

    /**
     * Gets the item's ID in the database.
     */
    String getId();
}
