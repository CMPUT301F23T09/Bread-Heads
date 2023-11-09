package com.example.breadheadsinventorymanager;

import static com.google.android.gms.tasks.Tasks.await;
import static org.junit.Assert.assertEquals;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class FirestoreInteractTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    @Mock
    private FirebaseFirestore firestore;
    @Mock
    private CollectionReference itemDB;
    @Mock
    private CollectionReference userDB;
    @Mock
    private FirestoreInteract interact;
    private Item item1;
    private Item item2;
    @Before
    public void setup() {
        firestore = FirebaseFirestore.getInstance();
        itemDB = firestore.collection("test");
        userDB = firestore.collection("users");
        interact = new FirestoreInteract(firestore, itemDB, userDB);
        item1 = new Item("30/06/2001", "Sample Item 1", "Sample Make 1", "Sample Model 1", "comment 1", 1000);
        item2 = new Item("05/09/2020", "Sample Item 2", "Sample Make 2", "Sample Model 2", "comment 2", 1000);
    }

    @Test
    public void testA_testFirestoreInteractConstructor() {
        assertEquals(interact.getDatabase(), firestore);
        assertEquals(FirestoreInteract.getItemDB(), itemDB);
        assertEquals(FirestoreInteract.getUserDB(), userDB);
    }

    // tests for basic interactions with firestore
    @Test
    public void testB_populate() throws ExecutionException, InterruptedException {
        ItemList list1 = new ItemList();

        // make sure test database starts empty
        await(interact.populateWithItems(list1).addOnCompleteListener(task1 ->
                assertEquals(0, list1.size())));
    }

    @Test
    public void testC_putAndRemove() throws ExecutionException, InterruptedException {
        // test putting items
        ItemList list2 = new ItemList();

        // size should now be 2
        await(interact.putItem(item1).addOnCompleteListener(task ->
                interact.putItem(item2).addOnCompleteListener(task2 ->
                        interact.populateWithItems(list2).addOnCompleteListener(task3 -> {
                            assertEquals(2, list2.size());


                            // the below code breaks Java indentation conventions
                            // but I think the lambda chain is more readable like this
                            // as the lines are executed "in order"

                            // remove item by ID
                            interact.deleteItem(item1.getId()).addOnCompleteListener(task4 ->
                            interact.populateWithItems(list2).addOnCompleteListener(task5 ->
                            assertEquals(1,list2.size())));
                            // remove item by item object
                            interact.deleteItem(item2).addOnCompleteListener(task6 ->
                            interact.populateWithItems(list2).addOnCompleteListener(task7 ->
                            assertEquals(0, list2.size())));
                        })))); // yuck...
    }
}
