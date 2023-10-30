package com.example.breadheadsinventorymanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Assert;
import org.junit.Test;

public class ItemListTest {
    @Test
    public void overriddenMethodsTest() {
        ItemList list1 = new ItemList();
        Item item1 = new Item("Jan 23", "Item 1", "Maker",
                "My Model", "151SERIAL", 1050);
        list1.add(item1);
        ItemList list2 = new ItemList(list1);

        assertEquals(list1, list2);

        Item item2 = new Item("Jan 23", "Item 1", "Maker",
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

        Item item1 = new Item("Jan 23", "Item 1", "Apple", "My Model", "151SERIAL", 1050);
        Item item2 = new Item("Jan 23", "Item 2", "Banana", "My Model", "151SERIAL", 25);
        Item item3 = new Item("Jan 23", "Item 3", "Banana", "My Model", "151SERIAL", 25);

        list1.add(item1);
        list1.add(item2);
        list1.add(item3);

        // makeList should only have unique makes in it
        assertEquals(2, list1.getMakeList().size());
    }

}
