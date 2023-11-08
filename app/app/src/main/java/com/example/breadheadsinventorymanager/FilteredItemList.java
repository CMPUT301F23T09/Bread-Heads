package com.example.breadheadsinventorymanager;


import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A filtered version of ItemList
 */
public class FilteredItemList extends ItemList {

    /**
     * No-arg constructor
     */
    public FilteredItemList() {
        super();
    }

    /**
     *
     * @param c
     */
    public FilteredItemList(Collection<? extends Item> c) {
        super(c);
    }


    /**
     * clears the filter
     */
    public void clearFilter() {
        this.clear();
    }

    /**
     * modify the original list to a filtered state
     *
     */

    public void filterByMake(String make) {

        List<Item> filteredStream =  this.stream()
                .filter(item -> item.getMake().equals(make))
                .collect(Collectors.toList());
        ArrayList<Item> filteredList = new ArrayList<Item>(filteredStream);

    }


}
