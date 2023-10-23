package com.example.breadheadsinventorymanager;

import java.util.ArrayList;

public class ItemList extends ArrayList<Item> {
    private double sum = 0.0; // Initialize the running sum to 0.0

    @Override
    public boolean add(Item item) {
        sum += item.getValue();
        return super.add(item);
    }

    @Override
    public boolean remove(Object item) {
        if (super.remove(item)) {
            // Item was removed, update the running sum
            sum -= ((Item) item).getValue();
            return true;
        }
        return false;
    }

    public double getSum() {
        return sum;
    }
}
