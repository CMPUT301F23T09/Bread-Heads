package com.example.breadheadsinventorymanager;

import static org.junit.Assert.*;

import org.junit.Test;


public class TagTest {

    @Test
    public void testConstructor() {
        Tag tag = new Tag("TestTag");
        assertEquals("TestTag", tag.getTag());
    }

    @Test
    public void testGetTag() {
        Tag tag = new Tag("TestTag");
        assertEquals("TestTag", tag.getTag());
    }

    @Test
    public void testSetTag() {
        Tag tag = new Tag("TestTag");
        tag.setTag("NewTag");
        assertEquals("NewTag", tag.getTag());
    }

    @Test
    public void testToString() {
        Tag tag = new Tag("TestTag");
        assertEquals("TestTag", tag.toString());
    }
}
