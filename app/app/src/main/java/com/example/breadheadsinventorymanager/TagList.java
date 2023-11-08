package com.example.breadheadsinventorymanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom ArrayList for managing tags.
 */
public class TagList extends ArrayList<Tag> {
    /**
     * No-arg constructor
     */
    protected TagList() {
        super();
    }

    /**
     * Constructs a TagList from the given List of Strings
     * @param list List<String> of all tags
     */
    protected TagList(List<String> list) {
        super();
        if (list == null) {
            // pass
        } else if (!list.isEmpty()) {
            for (String str : list) {
                addTag(new Tag(str));
            }
        }
    }

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

    /**
     * Removes and returns the Tag at the specified index.
     *
     * @param index The index of the Tag to remove.
     * @return The removed Tag.
     */
    public Tag remove(int index) {
        if (index >= 0 && index < size()) {
            return super.remove(index);
        } else {
            throw new IndexOutOfBoundsException("Index is out of bounds.");
        }
    }

    /**
     * Removes and returns the first Tag with the specified tag string.
     *
     * @param tag The tag string to remove.
     * @return The removed Tag, or null if the tag is not found.
     */
    public Tag remove(String tag) {
        for (Tag t : this) {
            if (t.getTag().equals(tag)) {
                super.remove(t);
                return t;
            }
        }
        return null; // Tag not found
    }

    /**
     * Removes the first occurrence of the specified Tag.
     *
     * @param tag The Tag object to remove.
     * @return true if the Tag was found and removed, false otherwise.
     */
    public boolean remove(Tag tag) {
        return super.remove(tag);
    }

}
