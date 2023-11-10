package com.example.breadheadsinventorymanager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;

/**
 * User-defineable tag that can be applied to items.
 */
public class Tag implements Serializable, FirestorePuttable {
    private String tag;
    private String id;

    /**
     * Constructs a Tag object with the specified tag string.
     *
     * @param tag The tag string.
     */
    public Tag(String tag) {
        this.tag = tag;
    }

    /**
     * Constructs a Tag object from a Firebase document
     * @param document The QueryDocumentSnapshot retrieved from Firebase containing a tag
     */
    public Tag(QueryDocumentSnapshot document) {
        this.tag = document.getString("tag");
    }

               /**
     * Gets the tag string.
     *
     * @return The tag string.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the tag string.
     *
     * @param tag The new tag string to set.
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Returns the tag string representation.
     *
     * @return The tag string.
     */
    @Override
    public String toString() {
        return tag;
    }

    @Override
    public HashMap<String, Object> formatForFirestore() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("tag", tag);
        return map;
    }

    @Override
    public void put(CollectionReference collection) {
        // pass
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
