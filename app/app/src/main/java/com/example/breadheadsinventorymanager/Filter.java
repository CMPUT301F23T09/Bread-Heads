package com.example.breadheadsinventorymanager;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * This class is a utility class to filter lists by make, description, date range and model
 */
public class Filter {

    private ItemList filteredList;

    /**
     * initializes the filtered list
     */
    Filter() {
        filteredList = new ItemList();
    }

    /**
     * resets the copy of ItemList
     */
    public void clearFilter() {
        this.filteredList.clear();
    }

    /**
     * Will output a list consisting of the make to search for
     * @param list the list to filter
     * @return the filtered list of makes searched for
     */
    public ItemList filterByMake(ItemList list, String make) {

        // filters the list to the make entered if t doesn't already exist in the filtered list
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMake().equals(make) && !(filteredList.contains(list.get(i)))) {
                filteredList.add(list.get(i));
            }
        }
        return this.filteredList;
    }

    /**
     * Will output a list consisting the description that was searched for
     * @param list the list to filter
     * @return the filtered list description
     */
    public ItemList filterByDescription(ItemList list, String description) {
        // filters the list to only add makes if it doesn't already exist in the filtered list
        for (int i = 0; i < list.size(); i++) {
            if (!(list.get(i).getDescription().contains(description)) && filteredList.contains(list.get(i))) {
                filteredList.remove(list.get(i));
            } else if (list.get(i).getDescription().contains(description) && !(filteredList.contains(list.get(i)))) {
                filteredList.add(list.get(i));
            }
        }
        return this.filteredList;
    }

    /**
     * Will filter the copied list by date range
     * @param list the list to filter
     * @param dateLow lower bound for date
     * @param dateHigh upper bound for date
     * @return the filtered list
     */
    public ItemList filterByDateRange(ItemList list, LocalDate dateLow, LocalDate dateHigh) {

        // filter the list to show only the dates within the range

        if(list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (!(list.get(i).getDateObj().isAfter(dateLow) && list.get(i).getDateObj().isBefore(dateHigh)) && filteredList.contains(list.get(i))) {
                    filteredList.remove(list.get(i));
                } else if (list.get(i).getDateObj().isAfter(dateLow) && list.get(i).getDateObj().isBefore(dateHigh) && !(filteredList.contains(list.get(i)))) {
                    filteredList.add(list.get(i));
                }
            }
        }

        return this.filteredList;
    }

}
