package com.example.breadheadsinventorymanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom ArrayList for managing tags.
 */
public class TagList extends ArrayList<Tag> {

    /**
     * Adds a tag to the list only if it doesn't already exist in the list.
     *
     * @param tag The tag to add.
     * @return true if the tag was added, false if it already exists in the list.
     */
    public boolean addTag(Tag tag) {
        if (!contains(tag)) {
            return super.add(tag);
        }
        return false;
    }

    /**
     * Filters the tags in the list by a single tag.
     *
     * @param filter The tag to filter by.
     * @return A list of tags that match the provided tag.
     */
    public List<Tag> filterByTag(String filter) {
        List<Tag> filteredList = new ArrayList<>();
        for (Tag tag : this) {
            if (tag.getTag().equals(filter)) {
                filteredList.add(tag);
            }
        }
        return filteredList;
    }

    /**
     * Filters the tags in the list by multiple tags.
     *
     * @param filters A list of tags to filter by.
     * @return A list of tags that match any of the provided tags.
     */
    public List<Tag> filterByTags(List<String> filters) {
        List<Tag> filteredList = new ArrayList<>();
        for (Tag tag : this) {
            if (filters.contains(tag.getTag())) {
                filteredList.add(tag);
            }
        }
        return filteredList;
    }

    /**
     * Sorts the tags in the list in alphabetical order based on the tag strings.
     */
    public void sortByTag() {
        Collections.sort(this, (tag1, tag2) -> tag1.getTag().compareTo(tag2.getTag()));
    }

    @Override
    public boolean add(Tag tag) {
        return addTag(tag);
    }
}
