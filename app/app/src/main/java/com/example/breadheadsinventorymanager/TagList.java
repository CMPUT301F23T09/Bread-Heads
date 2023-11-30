package com.example.breadheadsinventorymanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Custom ArrayList for managing tags.
 */
public class TagList extends ArrayList<Tag> implements Serializable {
    /**
     * No-arg constructor
     */
    protected TagList() {
        super();
    }

    /**
     * Constructs a TagList from the given List of Strings
     * @param list List of all tags as Strings
     */
    public TagList(List<String> list) {
        super();
        if (list == null) {
            // pass
        } else if (!list.isEmpty()) {
            for (String str : list) {
                addTag(new Tag(str));
            }
        }
    }

    public TagList(HashMap<String,String> list) {

    }


    /**
     * Adds a tag to the list only if it doesn't already exist in the list.
     *
     * @param tag The tag to add.
     * @return true if the tag was added, false if it already exists in the list.
     */
    public boolean addTag(Tag tag) {
        // Check if a tag with the same name already exists
        boolean tagExists = this.stream().anyMatch(existingTag -> existingTag.getTag().equals(tag.getTag()));

        // Check if the exact same Tag object already exists
        //boolean sameTagExists = this.contains(tag);

        if (tagExists) {
            return false; // Tag with the same name or the exact same Tag object already exists
        } else {
            return super.add(tag); // Add the tag
        }
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

    /**
     * Converts the TagList to a List of strings.
     *
     * @return A List<String> containing the string representation of each Tag.
     */
    public List<String> toList() {
        List<String> stringList = new ArrayList<>();
        for (Tag tag : this) {
            stringList.add(tag.getTag());
        }
        return stringList;
    }
    // TODO: fill out function description
    /**
     *
     *
     * @return
     */
    public List<String> getTagStrings() {
        List<String> tagStrings = new ArrayList<>();
        for (Tag tag : this) {
            tagStrings.add(tag.getTag());
        }
        return tagStrings;
    }


}
