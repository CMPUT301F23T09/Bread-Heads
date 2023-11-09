package com.example.breadheadsinventorymanager;

import static com.google.android.gms.tasks.Tasks.await;
import static org.junit.Assert.assertEquals;

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
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
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
