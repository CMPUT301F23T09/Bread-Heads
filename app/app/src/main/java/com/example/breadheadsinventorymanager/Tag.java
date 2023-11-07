package com.example.breadheadsinventorymanager;

/**
 * A class representing a tag, which is a string identifier.
 */
public class Tag {
    private String tag;

    /**
     * Constructs a Tag object with the specified tag string.
     *
     * @param tag The tag string.
     */
    public Tag(String tag) {
        this.tag = tag;
    }

    /**
     * Gets the tag string.
     *
     * @return The tag string.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the tag string.
     *
     * @param tag The new tag string to set.
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Returns the tag string representation.
     *
     * @return The tag string.
     */
    @Override
    public String toString() {
        return tag;
    }
}
