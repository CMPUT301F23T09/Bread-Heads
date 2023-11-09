package com.example.breadheadsinventorymanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ItemListTest {


    private Item item1;
    private Item item2;

    private ItemList itemList;

    private ItemList itemListC;


    @Before
    public void setUp() {
        itemList = new ItemList();

        item1 = new Item("31/08/2023", "Description1", "Make1", "Model1", "Serial1", 10);
        item1.setId("1");
        item2 = new Item("31/09/2023", "Description2", "Make2", "Model2", "Serial2", 20);
        item2.setId("2");

        Collection<Item> itemCollection = new ArrayList<>();
        itemCollection.add(item1);
        itemCollection.add(item2);
        itemListC = new ItemList(itemCollection);

    }

    @Test
    public void overriddenMethodsTest() {
        ItemList list1 = new ItemList();
        Item item1 = new Item("23/01/2023", "Item 1", "Maker",
                "My Model", "151SERIAL", 1050);
        list1.add(item1);
        ItemList list2 = new ItemList(list1);

        assertEquals(list1, list2);

        Item item2 = new Item("23/01/2023", "Item 1", "Maker",
                "My Model", "151SERIAL", 25);
        list1.add(item2);

        assertEquals(1050, list2.getSum(), 0.0);
        assertEquals(1075, list1.getSum(), 0.0);

        list1.remove(item1);

        assertEquals(1, list1.size());
        assertEquals(25, list1.getSum(), 0.0);

        // can't remove index that doesn't exist
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> list1.remove(2));

        list1.remove(0);
        list2.remove(0);
        assertEquals(0, list1.size());
        assertEquals(list1, list2);

        // can't remove index that doesn't exist
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> list1.remove(0));
        assertFalse(list1.remove(item1));
    }

    @Test
    public void getMakeListTests() {
        ItemList list1 = new ItemList();

        Item item1 = new Item("23/01/2023", "Item 1", "Apple", "My Model", "151SERIAL", 1050);
        Item item2 = new Item("23/01/2023", "Item 2", "Banana", "My Model", "151SERIAL", 25);
        Item item3 = new Item("23/01/2023", "Item 3", "Banana", "My Model", "151SERIAL", 25);

        list1.add(item1);
        list1.add(item2);
        list1.add(item3);

        // makeList should only have unique makes in it
        assertEquals(2, list1.getMakeList().size());
}

    public void testAddItem() {

        itemList.add(item1);
        TestCase.assertEquals(1, itemList.size());

        // Check the sum
        TestCase.assertEquals(10, itemList.getSum(), 0.001);

        itemList.add(item2);
        TestCase.assertEquals(2, itemList.size());

        // Check the sum
        TestCase.assertEquals(30, itemList.getSum(), 0.001);
    }


    @Test
    public void testRemoveItemById() {

        itemList.add(item1);
        itemList.add(item2);

        itemList.remove("1");
        TestCase.assertEquals(1, itemList.size());
        TestCase.assertEquals(20, itemList.getSum(), 0.001);
        itemList.remove("2");
        TestCase.assertEquals(0, itemList.getSum(), 0.001);

    }


    @Test
    public void testRemoveItem() {
        // Add the test item to the ItemList
        itemList.add(item1);
        itemList.add(item2);

        // Remove the item by index
        itemList.remove(item1);
        TestCase.assertEquals(20, itemList.getSum(), 0.001);
        itemList.remove(item2);
        TestCase.assertEquals(0, itemList.getSum(), 0.001);

    }

    @Test
    public void testRemoveItemByIdx() {
        // Add the test item to the ItemList
        itemList.add(item1);
        itemList.add(item2);

        // Remove the item by index
        itemList.remove(0);
        TestCase.assertEquals(20, itemList.getSum(), 0.001);
        itemList.remove(0);
        TestCase.assertEquals(0, itemList.getSum(), 0.001);

    }

    @Test
    public void testInitializeCollection() {
        TestCase.assertEquals(2, itemListC.size());
        TestCase.assertEquals(30, itemListC.getSum(), 0.001);
        // Remove the item by index
        itemListC.remove("2");
        TestCase.assertEquals(10, itemListC.getSum(), 0.001);
        itemListC.remove("1");
        TestCase.assertEquals(0, itemListC.getSum(), 0.001);
    }

    @Test
    public void testSort() {
        Item item3 = new Item("31/12/2000", "Description3", "z", "Z", "a", 20);
        item3.setId("3");

        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);

        // tests are ascending first, then descending
        itemList.sort("description", true);
        assertEquals(itemList.get(0), item1);
        assertEquals(itemList.get(1), item2);
        assertEquals(itemList.get(2), item3);
        assertEquals(itemList.size(), 3);

        itemList.sort("description", false);
        assertEquals(itemList.get(0), item3);
        assertEquals(itemList.get(1), item2);
        assertEquals(itemList.get(2), item1);
        assertEquals(itemList.size(), 3);

        itemList.sort("date", true);
        assertEquals(itemList.get(0), item3);
        assertEquals(itemList.get(1), item1);
        assertEquals(itemList.get(2), item2);
        assertEquals(itemList.size(), 3);

        ItemList reversedList = itemList;
        Collections.reverse(reversedList);
        itemList.sort("date", false);
        assertEquals(reversedList, itemList);
        assertEquals(itemList.size(), 3);

        itemList.sort("comment", true);
        assertEquals(itemList.get(0), item3);
        assertEquals(itemList.get(1), item1);
        assertEquals(itemList.get(2), item2);
        assertEquals(itemList.size(), 3);

        reversedList = itemList;
        Collections.reverse(reversedList);
        itemList.sort("comment", false);
        assertEquals(reversedList, itemList);
        assertEquals(itemList.size(), 3);

        itemList.sort("make", true);
        assertEquals(itemList.get(0), item1);
        assertEquals(itemList.get(1), item2);
        assertEquals(itemList.get(2), item3);
        assertEquals(itemList.size(), 3);

        reversedList = itemList;
        Collections.reverse(reversedList);
        itemList.sort("make", false);
        assertEquals(reversedList, itemList);
        assertEquals(itemList.size(), 3);

        itemList.sort("value", true);
        assertEquals(itemList.size(), 3);
        assertTrue(itemList.get(0).getValue() <= itemList.get(1).getValue());
        assertTrue(itemList.get(1).getValue() <= itemList.get(2).getValue());
        assertTrue(itemList.get(0).getValue() <= itemList.get(2).getValue());

        reversedList = itemList;
        Collections.reverse(reversedList);
        itemList.sort("value", false);
        assertEquals(reversedList, itemList);
        assertEquals(itemList.size(), 3);
    }
}
