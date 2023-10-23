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
 * @version
 * 1.0
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
     *
     * @param collection
     * @return a task to be used
     */
    public Task<QuerySnapshot> populateFromCollection(CollectionReference collection) {
        return collection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("ItemList.java", document.getId() + " => " + document.getData());
                        add(new Item(document));
                    }
                } else {
                    Log.d("ItemList.java", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * Gets sum of all Items in this list
     */
    public double getSum() {
        return sum;
    }
}

/* example of how to call data from firestore to populate an ItemList
public void firestoreExample() {
    CollectionReference collection = FirebaseFirestore.getInstance().collection("test");
    ItemList list = new ItemList();
    Task<QuerySnapshot> task = list.populateFromCollection(collection);
    // do some sort of "loading/please wait" screen
    task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            // end our "loading/please wait" screen

            // logging for testing:
            if (2 != list.size()) { throw new RuntimeException("oops");}
            Log.i("main", list.get(0).getData().toString());
            Log.i("main", list.get(1).getData().toString());
        }
    });
}
 */