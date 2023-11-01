package com.example.breadheadsinventorymanager;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
        when(documentSnapshot.getString("date")).thenReturn("2023-01-01");
        when(documentSnapshot.getString("description")).thenReturn("Sample Item");
        when(documentSnapshot.getString("make")).thenReturn("Sample Make");
        when(documentSnapshot.getString("model")).thenReturn("Sample Model");
        when(documentSnapshot.getString("serialNum")).thenReturn("123456");
        when(documentSnapshot.get("value")).thenReturn(1000L);
        when(documentSnapshot.getString("comment")).thenReturn("Sample Comment");

        // Set up mock data for QueryDocumentSnapshot
        when(queryDocumentSnapshot.getString("date")).thenReturn("2023-01-01");
        when(queryDocumentSnapshot.getString("description")).thenReturn("Sample Item");
        when(queryDocumentSnapshot.getString("make")).thenReturn("Sample Make");
        when(queryDocumentSnapshot.getString("model")).thenReturn("Sample Model");
        when(queryDocumentSnapshot.getString("serialNum")).thenReturn("123456");
        when(queryDocumentSnapshot.get("value")).thenReturn(1000L);
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
        Item item = new Item("2023-01-01", "Sample Item", "Sample Make", "Sample Model", "123456", 1000);
        assertEquals("2023-01-01", item.getDate());
        assertEquals("Sample Item", item.getDescription());
        assertEquals("Sample Make", item.getMake());
        assertEquals("Sample Model", item.getModel());
        assertEquals("123456", item.getSerialNum());
        assertEquals(1000, item.getValue());
        assertEquals("", item.getComment());
    }

    // Test the constructor with a DocumentSnapshot
    @Test
    public void testConstructorWithDocumentSnapshot() {
        Item item = new Item(documentSnapshot);
        assertEquals("2023-01-01", item.getDate());
        assertEquals("Sample Item", item.getDescription());
        assertEquals("Sample Make", item.getMake());
        assertEquals("Sample Model", item.getModel());
        assertEquals("123456", item.getSerialNum());
        assertEquals(1000, item.getValue());
        assertEquals("Sample Comment", item.getComment());
    }

    // Test the constructor with a QueryDocumentSnapshot
    @Test
    public void testConstructorWithQueryDocumentSnapshot() {
        Item item = new Item(queryDocumentSnapshot);
        assertEquals("2023-01-01", item.getDate());
        assertEquals("Sample Item", item.getDescription());
        assertEquals("Sample Make", item.getMake());
        assertEquals("Sample Model", item.getModel());
        assertEquals("123456", item.getSerialNum());
        assertEquals(1000, item.getValue());
        assertEquals("Sample Comment", item.getComment());
    }

    // Test the constructor without the serial number
    @Test
    public void testConstructorWithoutSerialNumber() {
        Item item = new Item("2023-01-01", "Sample Item", "Sample Make", "Sample Model", 1000);
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
        Item item = new Item("2023-01-01", "Sample Item", "Sample Make", "Sample Model", "123456", 1000L);
        HashMap<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("date", "2023-01-01");
        expectedMap.put("description", "Sample Item");
        expectedMap.put("make", "Sample Make");
        expectedMap.put("model", "Sample Model");
        expectedMap.put("serialNum", "123456");
        expectedMap.put("value", 1000L);
        expectedMap.put("comment", "");
        assertEquals(expectedMap.size(), item.formatForFirestore().size());
        for (String key : expectedMap.keySet()) {
            assertTrue(item.formatForFirestore().containsKey(key));
            assertEquals(expectedMap.get(key), item.formatForFirestore().get(key));
        }



    }
}
