package com.example.breadheadsinventorymanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Stores a list of items
 *
 * @version
 * 1.0
 */
public class ItemList extends ArrayList<Item> {
    private int sum = 0; // Initialize the running sum to 0

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
        Iterator<com.example.breadheadsinventorymanager.Item> it = this.iterator();
        while (it.hasNext()) {
            this.sum += ((Item) it.next()).getValue();
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
     * Gets sum of all Items in this list
     */
    public double getSum() {
        return sum;
    }
}
