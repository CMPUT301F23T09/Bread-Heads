package com.example.breadheadsinventorymanager;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.dataflow.qual.TerminatesExecution;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;

public class ItemTest {
    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private QueryDocumentSnapshot queryDocumentSnapshot;

    @Before
    public void setUp() {
        openMocks(this); // Use openMocks to initialize mocks

        // Set up mock data for DocumentSnapshot
        when(documentSnapshot.getString("date")).thenReturn("01/01/2023");
        when(documentSnapshot.getString("description")).thenReturn("Sample Item");
        when(documentSnapshot.getString("make")).thenReturn("Sample Make");
        when(documentSnapshot.getString("model")).thenReturn("Sample Model");
        when(documentSnapshot.getString("serialNum")).thenReturn("123456");
        when(documentSnapshot.getLong("value")).thenReturn((long) 1000);
        when(documentSnapshot.getString("comment")).thenReturn("Sample Comment");

        // Set up mock data for QueryDocumentSnapshot
        when(queryDocumentSnapshot.getString("date")).thenReturn("01/01/2023");
        when(queryDocumentSnapshot.getString("description")).thenReturn("Sample Item");
        when(queryDocumentSnapshot.getString("make")).thenReturn("Sample Make");
        when(queryDocumentSnapshot.getString("model")).thenReturn("Sample Model");
        when(queryDocumentSnapshot.getString("serialNum")).thenReturn("123456");
        when(queryDocumentSnapshot.getLong("value")).thenReturn((long) 1000);
        when(queryDocumentSnapshot.getString("comment")).thenReturn("Sample Comment");
    }

    // Test the empty constructor
    @Test
    public void testEmptyConstructor() {
        Item item = new Item();
        assertNull(item.getDate());
        assertNull(item.getDescription());
        assertNull(item.getMake());
        assertNull(item.getModel());
        assertNull(item.getSerialNum());
        assertEquals(0, item.getValue());
        assertEquals("", item.getComment());
    }

    // Test the constructor with all parameters
    @Test
    public void testConstructorWithAllParams() {
        Item item = new Item("01/01/2023", "Sample Item", "Sample Make", "Sample Model", "123456", 1000);
        assertEquals("01/01/2023", item.getDate());
        assertEquals("Sample Item", item.getDescription());
        assertEquals("Sample Make", item.getMake());
        assertEquals("Sample Model", item.getModel());
        assertEquals("123456", item.getComment());
        assertEquals(1000, item.getValue());
    }

    // Test the constructor with a DocumentSnapshot
    @Test
    public void testConstructorWithDocumentSnapshot() {
        Item item = new Item(documentSnapshot);
        assertEquals("01/01/2023", item.getDate());
        assertEquals("Sample Item", item.getDescription());
        assertEquals("Sample Make", item.getMake());
        assertEquals("Sample Model", item.getModel());
        assertEquals("123456", item.getSerialNum());
        assertEquals((long) 1000, item.getValue());
        assertEquals("Sample Comment", item.getComment());
    }

    // Test the constructor with a QueryDocumentSnapshot
    @Test
    public void testConstructorWithQueryDocumentSnapshot() {
        Item item = new Item(queryDocumentSnapshot);
        assertEquals("01/01/2023", item.getDate());
        assertEquals("Sample Item", item.getDescription());
        assertEquals("Sample Make", item.getMake());
        assertEquals("Sample Model", item.getModel());
        assertEquals("123456", item.getSerialNum());
        assertEquals((long) 1000, item.getValue());
        assertEquals("Sample Comment", item.getComment());
    }

    // Test the constructor without the serial number
    @Test
    public void testConstructorWithoutSerialNumber() {
        Item item = new Item("01/01/2023", "Sample Item", "Sample Make", "Sample Model", "Sample comment", 1000);
        assertNull(item.getSerialNum());
    }

    // Test the toDollarString method
    @Test
    public void testToDollarString() {
        assertEquals("0.00", Item.toDollarString(0));
        assertEquals("10.05", Item.toDollarString(1005));
        assertEquals("5.00", Item.toDollarString(500));
    }

    // Test the getValueDollarString method
    @Test
    public void testGetValueDollarString() {
        Item item = new Item();
        item.setValue(1005);
        assertEquals("10.05", item.getValueDollarString());
    }

    // Test the formatForFirestore method
    @Test
    public void testFormatForFirestore() {
        Item item = new Item("01/01/2023", "Sample Item", "Sample Make", "Sample Model", "123456", 1000L);
        HashMap<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("date", "01/01/2023");
        expectedMap.put("description", "Sample Item");
        expectedMap.put("make", "Sample Make");
        expectedMap.put("model", "Sample Model");
        expectedMap.put("comment", "123456");
        expectedMap.put("value", 1000L);
        for (String key : expectedMap.keySet()) {
            assertTrue(item.formatForFirestore().containsKey(key));
            assertEquals(expectedMap.get(key), item.formatForFirestore().get(key));
        }
    }

    // test the toValue static method
    @Test
    public void testToValue() {
        // test with nothing after decimal
        String str1 = "$1050";
        String str2 = "1050";
        String str3 = "1050.0";
        String str4 = "$1050.00";
        String str5 = "0001050.00000";

        long val1 = Item.toValue(str1);
        long val2 = Item.toValue(str2);
        long val3 = Item.toValue(str3);
        long val4 = Item.toValue(str4);
        long val5 = Item.toValue(str5);

        long val = 105000;


        // test with things after decimal
        String str6 = "$1050.55";
        String str7 = "$1050.550";
        String str8 = "$1050.551";
        String str9 = "0001050.548002";

        assertEquals(val, val1);
        assertEquals(val, val2);
        assertEquals(val, val3);
        assertEquals(val, val4);
        assertEquals(val, val5);

        assertEquals(105055, Item.toValue(str6));
        assertEquals(105055, Item.toValue(str7));
        assertEquals(105055, Item.toValue(str8));
        assertEquals(105055, Item.toValue(str9));
    }
}
