package com.example.breadheadsinventorymanager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Stores a list of items
 *
 * @version 1.2
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
        this.sum = 0;
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
    public boolean remove(Item item) {
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
            if (id.equals(item.getId())) {
                // Found the item with the matching ID, remove it and update the running sum
                this.remove(item);
                return item;
            }
        }
        return null;
    }


    /**
     * Gets sum of values of all Items in this list
     */
    public double getSum() {
        return sum;
    }

    /**
     * Gets sum of values of all Items in this list
     * @return A string formatted as 10.50 - add dollar sign if necessary
     */
    public String getSumAsDollarString() {
        return Item.toDollarString(sum);
    }

    /**
     * Gets all unique makes from ItemList and puts them in an array
     * @return the list of unique "makes" in ItemList
     */
    public ArrayList<String> getMakeList() {
        ArrayList<String> makeList = new ArrayList<String>();
        for(int i = 0; i < this.size(); i++) {
            if(!(makeList.contains(this.get(i).getMake()))) {
                makeList.add(this.get(i).getMake());
            }
        }

        return makeList;
    }

    /**
     * Returns a list of items that have all the specified tags.
     *
     * @param tags A list of tags to filter items by.
     * @return A list of items that have all the specified tags.
     */
    public List<Item> filterTags(Collection<Tag> tags) {
        List<Item> matchingItems = new ArrayList<>();

        for (Item item : this) {
            TagList itemTags = item.getTags();

            // Check if the item's tags contain all the specified tags
            boolean containsAllTags = true;

            for (Tag tag : tags) {
                boolean tagFound = false;
                for (Tag itemTag : itemTags) {
                    if (tag.getTag().equals(itemTag.getTag())) {
                        tagFound = true;
                        break;
                    }
                }
                if (!tagFound) {
                    containsAllTags = false;

                    break;
                }
            }

            if (containsAllTags) {
                matchingItems.add(item);
            }
        }

        return matchingItems;
    }


    /**
     * Sorts the ItemList by tags alphabetically.
     *
     * @param ascending True if the sorted list should be in ascending order, else false for descending order.
     */
    public void sortItemsByTagsAlphabetically(boolean ascending) {
        Collections.sort(this, (item1, item2) -> {
            TagList tags1 = item1.getTags();
            TagList tags2 = item2.getTags();

            // Extract tag strings and sort them alphabetically
            List<String> sortedTags1 = new ArrayList<>();
            for (Tag tag : tags1) {
                sortedTags1.add(tag.getTag());
            }

            List<String> sortedTags2 = new ArrayList<>();
            for (Tag tag : tags2) {
                sortedTags2.add(tag.getTag());
            }

            Collections.sort(sortedTags1);
            Collections.sort(sortedTags2);

            // Compare the sorted tag lists
            int size1 = sortedTags1.size();
            int size2 = sortedTags2.size();
            int minSize = Math.min(size1, size2);

            for (int i = 0; i < minSize; i++) {
                int tagComparison = sortedTags1.get(i).compareTo(sortedTags2.get(i));
                if (tagComparison != 0) {
                    return ascending ? tagComparison : -tagComparison;
                }
            }

            // If the common tags are the same, compare based on the number of tags
            return Integer.compare(size1, size2);
        });

    }

    /**
     * Sorts the ItemList by the given parameter in either ascending or descending order.
     *
     * @param field     The field to sort by. Supports "description", "comment", "date", "make", "value", or "tags".
     * @param ascending True if the sorted list should be ascending, else false.
     */
    public void sort(String field, boolean ascending) {
        if ("tags".equals(field)) {
            sortItemsByTagsAlphabetically(ascending);
        } else {
            Collections.sort(this, (lhs, rhs) -> {
                int result = 0;

                switch (field) {
                    case "description":
                        result = lhs.getDescription().toLowerCase().compareTo(rhs.getDescription().toLowerCase());
                        break;
                    case "comment":
                        result = lhs.getComment().toLowerCase().compareTo(
                            rhs.getComment().toLowerCase());
                        break;
                    case "date":
                        result = lhs.getDateObj().compareTo(rhs.getDateObj());
                        break;
                    case "make":
                        result = lhs.getMake().toLowerCase().compareTo(
                            rhs.getMake().toLowerCase());
                        break;
                    case "value":
                        result = Long.compare(lhs.getValue(), rhs.getValue());
                        break;
                }

                // If descending, flip all comparisons
                return ascending ? result : -result;
            });
        }
    }

}