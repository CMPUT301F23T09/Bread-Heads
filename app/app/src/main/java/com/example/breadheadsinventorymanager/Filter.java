package com.example.breadheadsinventorymanager;

import android.util.Log;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a utility class to filter lists by make, description, date range and model
 */
public class Filter {

    private ItemList filteredList;
    private final HashMap<String, ItemList> descriptionComp;
    private final HashMap<String, ItemList> makeComp;
    private final HashMap<String, ItemList> dateComp;
    // arbitrary number to ensure keys are unique
    private Integer num = 0;

    /**
     * initializes the filtered list
     */
    Filter() {
        filteredList = new ItemList();
        descriptionComp = new HashMap<>();
        makeComp = new HashMap<>();
        dateComp = new HashMap<>();
    }

    /**
     * resets the copy of ItemList
     */
    public void clearFilter() {
        this.filteredList.clear();
    }

    /**
     * clears the hashMap
     */
    public void clearCriteria() {
        this.descriptionComp.clear();
        this.makeComp.clear();
        this.dateComp.clear();
    }

    /**
     * filters the list by make and adds it to the hashMap
     */
    public void filterMake(String make, ItemList list) {
        ItemList makeList = new ItemList();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMake().equals(make)) {
                makeList.add(list.get(i));
            }
        }
        // add the make list to the hashmap
        // arbitrary number to make hash keys unique
        makeComp.put(make, makeList);
        num++;
    }

    /**
     * filters the list by description and adds it to the hashMap
     *
     * @param description the description to look for
     * @param list        the list ot filter
     */
    public void filterDescription(String description, ItemList list) {
        ItemList descList = new ItemList();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDescription().contains(description)) {
                descList.add(list.get(i));
            }
        }
        // TODO check if we can put multiple descriptions in here
        descriptionComp.put(description, descList);
        num++;
    }

    /**
     * filters the list by date range and adds it to the hashMap
     *
     * @param dateLow  lower date bound
     * @param dateHigh upper date bound
     * @param list     the list ot filter
     */
    public void filterDate(LocalDate dateLow, LocalDate dateHigh, ItemList list) {
        ItemList dateList = new ItemList();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDateObj().isAfter(dateLow) && list.get(i).getDateObj().isBefore(dateHigh)) {
                dateList.add(list.get(i));
            }
        }
        String date = dateLow.toString() + "-" + dateHigh.toString();
        dateComp.put(date, dateList);
        num++;
    }


    /**
     * given an item and a list, this will check if that item is a valid entry for the filtered list
     * @param list the list that item will check against
     * @param item the item to check against the date filter
     * @return true if the item is valid to add to the filtered list, false otherwise
     */
    public boolean checkAgainstDateFilter(ItemList list, Item item) {
        boolean valid = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if(dateComp.isEmpty()) {
            return true;
        } else {
            for (Map.Entry<String, ItemList> entry : dateComp.entrySet()) {
                String key = entry.getKey();

                // split up the key into the date bounds since the key is always 21 characters
                String stringLow = key.substring(0, 10);
                String stringHigh = key.substring(11, 21);
                Log.d("h1", stringLow + " " + stringHigh);
                LocalDate low = LocalDate.parse(stringLow, formatter);
                LocalDate high = LocalDate.parse(stringHigh, formatter);
                if (item.getDateObj().isAfter(low) && item.getDateObj().isBefore(high)) {
                    valid = true;
                }
            }
        }
        Log.d("h1",  "dateFilterValid " + String.valueOf(valid));
        Log.d("h1",  String.valueOf(list.size()));
        return valid;
    }

    /**
     * given an item and a list, this will check if that item is a valid entry for the filtered list
     * @param list the list that item will check against
     * @param item the item to check against the date filter
     * @return true if the item is valid to add to the filtered list, false otherwise
     */
    public boolean checkAgainstDescriptionFilter(ItemList list, Item item) {
        boolean valid = false;
        if(descriptionComp.isEmpty()) {
            return true;
        } else {
            for (Map.Entry<String, ItemList> entry : descriptionComp.entrySet()) {
                String key = entry.getKey();
                // key is our description
                if (item.getDescription().equals(key)) {
                    valid = true;
                }
            }
        }
        Log.d("h1",  "DescriptionFilterValid " + String.valueOf(valid));
        return valid;

    }

    /**
     * look for commonalities between lists and add them to a new list, filtering by makes uses logical "or"
     * while date and description filter by logical "and"
     *
     * @param list the original list of items to filter
     * @return a filtered list
     */
    // TODO MAKE NICER PLZ - FUTURE EVAN
    public ItemList processFilter(ItemList list) {

        // filter in order of description then date then make since multiple dates/makes filters can be added
        // it can be changed if more than one description can be searched for
        filteredList.addAll(list);
        if (descriptionComp.size() > 0) {
            for (Map.Entry<String, ItemList> entry : descriptionComp.entrySet()) {
                // get values of hashmap
                String key = entry.getKey();
                ItemList arrayList = entry.getValue();

                // nifty way of checking if this is our first time filtering the list
                if (list.size() > this.filteredList.size()) {
                    // since we can filter by multiple descriptions, we simply add to the filtered list if it doesn't already exist
                    for (int i = 0; i < filteredList.size(); i++) {
                        if( !(arrayList.contains(filteredList.get(i))) && checkAgainstDateFilter(filteredList, arrayList.get(i))) {
                            filteredList.remove(i);
                        }
                        //Log.d("h1", "second go around " + list.size() + " " + this.filteredList.size());
                    }

                } else {
                    // clear the filtered list as it is the first time we are performing a filter operation
                    this.filteredList.clear();
                    filteredList.addAll(arrayList);
                    //Log.d("h1", "first go around " + list.size() + " " + this.filteredList.size());

                }
            }
        }
        if (makeComp.size() > 0) {
            // TODO filtering of makes
            for (Map.Entry<String, ItemList> entry : makeComp.entrySet()) {
                // get values of hashmap
                String key = entry.getKey();
                ItemList makeArrayList = entry.getValue();

                // nifty way of checking if this is our first time filtering the list
                if (list.size() > this.filteredList.size()) {
                    // since we can filter by multiple descriptions, we simply add to the filtered list if it doesn't already exist
                    for (int i = 0; i < makeArrayList.size(); i++) {
                        if( !(filteredList.contains(makeArrayList.get(i))) && checkAgainstDateFilter(filteredList, makeArrayList.get(i)) && checkAgainstDescriptionFilter(filteredList, makeArrayList.get(i))) {
                            this.filteredList.add(makeArrayList.get(i));
                        }
                        //Log.d("h1", "second go around " + list.size() + " " + this.filteredList.size());
                    }
                } else {
                    // clear the filtered list as it is the first time we are performing a filter operation
                    this.filteredList.clear();
                    filteredList.addAll(makeArrayList);
                    //Log.d("h1", "first go around " + list.size() + " " + this.filteredList.size());
                }
            }
        }
        if (dateComp.size() > 0) {
            for (Map.Entry<String, ItemList> entry : dateComp.entrySet()) {
                // get values of hashmap
                String key = entry.getKey();
                ItemList dateArrayList = entry.getValue();

                if (list.size() > this.filteredList.size()) {
                    // since we can filter by multiple dates, we simply add to the filtered list if it doesn't already exist
                    //Log.d("h1", "date filter " + filteredList.size() + " " + dateArrayList.size());
                    for (int j = 0; j < filteredList.size(); j++) {
                        //Log.d("h1", "date filter " + filteredList.get(j).getDate() +  " " + filteredList.size());
                        if(!(dateArrayList.contains(filteredList.get(j)))) {
                            //Log.d("h1", "Item " + filteredList.get(j).getDate() + " is not in arrayList");
                            this.filteredList.remove(j);
                            // decrement j so our index doesn't mess up GOOD GOD
                            j--;
                        }
                    }
                } else {
                    // clear the filtered list as it is the first time we are performing a filter operation, then add dates in range
                    this.filteredList.clear();
                    filteredList.addAll(dateArrayList);
                }
            }
        }

        return this.filteredList;
        }
}

