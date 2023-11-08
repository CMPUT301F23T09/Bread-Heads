package com.example.breadheadsinventorymanager;

import java.util.ArrayList;

/**
 * This class is a utility class to filter lists by make, description, date range and model
 */
public class Filter {

    private ItemList ItemListCopy;

    /**
     * saves a copy of the current ItemList to call on whenever
     * @param copyList
     */
    Filter(ItemList copyList) {
        ItemListCopy = new ItemList();
        this.ItemListCopy.addAll(copyList);
    }

    /**
     * resets the copy of ItemList
     */
    public void clearFilter() {
        this.ItemListCopy.clear();
    }

    /**
     * Resets the filtered list to the current ItemList
     * @param newList the current ItemList to reset the filter
     */
    public void resetFilter(ItemList newList) {
        clearFilter();
        this.ItemListCopy.addAll(newList);
    }

    /**
     * Will output a list consisting of the make to search for
     * @param list the list to filter
     * @return the filtered list of makes searched for
     */
    public ItemList filterByMake(ItemList list, String make) {


        return this.ItemListCopy;
    }

    /**
     * Will output a list consisting the description that was searched for
     * @param list the list to filter
     * @return the filtered list description
     */
    public ItemList filterByDescription(ItemList list, String description) {

        return this.ItemListCopy;
    }

    /**
     * Will filter the copied list by date range
     * @param list the list to filter
     * @param dateLow lower bound for date
     * @param dateHigh upper bound for date
     * @return the filtered list
     */
    public ItemList filterByDateRange(ItemList list, String dateLow, String dateHigh) {

        return this.ItemListCopy;
    }

}
