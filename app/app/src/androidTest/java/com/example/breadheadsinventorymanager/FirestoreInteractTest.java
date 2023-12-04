package com.example.breadheadsinventorymanager;

import static com.google.android.gms.tasks.Tasks.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class FirestoreInteractTest {
    public static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("skip_auth", true);
        intent.putExtras(bundle);
    }
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(intent);
    @Mock
    private static FirebaseFirestore firestore;
    @Mock
    private static CollectionReference itemDB;
    @Mock
    private static CollectionReference userDB;
    @Mock
    private static FirestoreInteract interact;
    private Item item1;
    private Item item2;
    @Before
    public void setup() {
        firestore = FirebaseFirestore.getInstance();
        itemDB = firestore.collection("testItems");
        userDB = firestore.collection("testUsers");
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

    @Test
    public void testC_putAndRemove() throws ExecutionException, InterruptedException {
        // test putting items
        ItemList list2 = new ItemList();

        // size should now be 2
        await(interact.putItem(item1).addOnCompleteListener(task ->
                interact.putItem(item2).addOnCompleteListener(task2 ->
                        interact.populateWithItems(list2).addOnCompleteListener(task3 -> {
                            assertTrue("Database should now have item1's info", list2.getMakeList().contains(item1.getMake()));
                            assertTrue("Database should now have item1's info", list2.getMakeList().contains(item2.getMake()));

                            // the below code breaks Java indentation conventions
                            // but I think the lambda chain is more readable like this
                            // as the lines are executed "in order"

                            // remove item by ID
                            interact.deleteItem(item1.getId()).addOnCompleteListener(task4 ->
                            interact.populateWithItems(list2).addOnCompleteListener(task5 ->
                                    assertFalse("Database should not have item1 anymore", list2.getMakeList().contains(item1.getMake()))));
                            // remove item by item object
                            interact.deleteItem(item2).addOnCompleteListener(task6 ->
                            interact.populateWithItems(list2).addOnCompleteListener(task7 ->
                                    assertFalse("Database should not have item2 anymore", list2.getMakeList().contains(item2.getMake()))));
                        }))));
    }

    @AfterClass
    public static void cleanup() {
        deleteCollection(itemDB, 2048);
    }

    /**
     * Code adapted from official Google documentation
     * <a href="https://cloud.google.com/firestore/docs/samples/firestore-data-delete-collection#firestore_data_delete_collection-java">...</a>
     *
     * Delete a collection in batches to avoid out-of-memory errors. Batch size may be tuned based
     * on document size (at most 1MB) and application requirements.
     */
    private static void deleteCollection(CollectionReference collection, int batchSize) {
        try {
            // retrieve a small batch of documents to avoid out-of-memory errors
            QuerySnapshot future = collection.limit(batchSize).get().getResult();
            int deleted = 0;
            // future.get() blocks on document retrieval
            List<DocumentSnapshot> documents = future.getDocuments();
            for (DocumentSnapshot document : documents) {
                document.getReference().delete();
                ++deleted;
            }
            if (deleted >= batchSize) {
                // retrieve and delete another batch
                deleteCollection(collection, batchSize);
            }
        } catch (Exception e) {
            System.err.println("Error deleting collection : " + e.getMessage());
        }
    }
}
