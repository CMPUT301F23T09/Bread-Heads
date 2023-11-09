package com.example.breadheadsinventorymanager;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TagListTest {

    private TagList tagList;

    @Before
    public void setup() {
        tagList = new TagList();
    }

    @Test
    public void testAddTag() {
        Tag tag1 = new Tag("ATag");
        Tag tag2 = new Tag("BTag");
        Tag tag3 = new Tag("ATag"); // Create a different Tag object with the same tag name
        Tag tag4 = new Tag("123-Tag");

        assertTrue(tagList.addTag(tag1));
        assertTrue(tagList.addTag(tag2));
        assertFalse(tagList.addTag(tag3)); // Adding a different Tag object with the same name should return true
        assertTrue(tagList.addTag(tag4));
    }
    @Test
    public void testRemoveByTagObject() {
        Tag tag1 = new Tag("ATag");
        Tag tag2 = new Tag("BTag");
        Tag tag3 = new Tag("ATag"); // Create a different Tag object with the same tag name
        Tag tag4 = new Tag("123-Tag");

        tagList.add(tag1);
        tagList.add(tag2);
        tagList.add(tag3);
        tagList.add(tag4);

        assertTrue(tagList.remove(tag2));
        assertFalse(tagList.remove(tag2)); // The original Tag object with the same name should still be removed
    }
}
