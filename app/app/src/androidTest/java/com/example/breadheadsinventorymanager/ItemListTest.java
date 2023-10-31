package com.example.breadheadsinventorymanager;

import static junit.framework.TestCase.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ItemListTest {

    private Item item1;
    private Item item2;

    private ItemList itemList;


    @Before
    public void setUp() {
        itemList = new ItemList();
        item1 = new Item("2023-10-31", "Description1", "Make1", "Model1", "Serial1", 10);
        item1.setId("1");
        item2 = new Item("2023-9-31", "Description2", "Make2", "Model2", "Serial2", 20);
        item2.setId("2");
    }


    @Test
    public void testAddItem() {

        itemList.add(item1);
        assertEquals(1, itemList.size());

        // Check the sum
        assertEquals(10, itemList.getSum(), 0.001);

        itemList.add(item2);
        assertEquals(2, itemList.size());

        // Check the sum
        assertEquals(30, itemList.getSum(), 0.001);
    }


    @Test
    public void testRemoveItemById() {

        itemList.add(item1);
        itemList.add(item2);

        itemList.remove("1");
        assertEquals(1, itemList.size());
        assertEquals(20, itemList.getSum(), 0.001);
        itemList.remove("2");
        assertEquals(0, itemList.getSum(), 0.001);

    }


    @Test
    public void testRemoveItem() {
        // Add the test item to the ItemList
        itemList.add(item1);
        itemList.add(item2);

        // Remove the item by index
        itemList.remove(item1);
        assertEquals(20, itemList.getSum(), 0.001);
        itemList.remove(item2);
        assertEquals(0, itemList.getSum(), 0.001);

    }

    @Test
    public void testRemoveItemByIdx() {
        // Add the test item to the ItemList
        itemList.add(item1);
        itemList.add(item2);

        // Remove the item by index
        itemList.remove(0);
        assertEquals(20, itemList.getSum(), 0.001);
        itemList.remove(0);
        assertEquals(0, itemList.getSum(), 0.001);

    }


    }

