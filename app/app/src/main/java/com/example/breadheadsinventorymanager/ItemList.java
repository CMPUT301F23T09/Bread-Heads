package com.example.breadheadsinventorymanager;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Stores a list of items
 *
 * @version 1.1
 */
public class ItemList extends ArrayList<Item> {
    private long sum = 0; // Initialize the running sum to 0

    /**
     * No-arg constructor
     */
    public ItemList() {
        super();
    }

    /**
     * Create an ItemList from an existing collection and update the running sum
     * @param c Existing collection
     */
    public ItemList(Collection<? extends Item> c) {
        super(c);
        for (Item item : this) {
            this.sum += item.getValue();
        }
    }

    /**
     * Add an item to the array list and update the running sum
     * @param item element whose presence in this collection is to be ensured
     * @return true (as per Collection.add)
     */
    @Override
    public boolean add(Item item) {
        sum += item.getValue();
        return super.add(item);
    }

    /**
     * Remove an item from the list and update the running sum
     * @param item element to be removed from this list, if present
     * @return true if an item was removed, else false
     */
    @Override
    public boolean remove(Object item) {
        if (super.remove(item)) {
            // Item was removed, update the running sum
            sum -= ((Item) item).getValue();
            return true;
        }
        return false;
    }

    /**
     * Remove an item from the list based on index and update the running sum
     * @param i index of element to be removed from this list, if present
     * @return item removed
     */
    @Override
    public Item remove(int i) {
        Item item = get(i);
        remove(item); // throws exception if index doesn't exist
        // Item was removed, update the running sum
        return item;
    }

    /**
     * Remove an item from the list based on its ID and update the running sum
     * @param id The ID of the item to remove
     * @return The removed item, or null if not found
     */
    public Item remove(String id) {
        for (Item item : this) {
            if (item.getId().equals(id)) {
                // Found the item with the matching ID, remove it and update the running sum
                super.remove(item);
                return item;
            }
        }
        return null; // Item with the specified ID not found
    }


    /**
     * Gets sum of all Items in this list
     */
    public double getSum() {
        return sum;
    }
}