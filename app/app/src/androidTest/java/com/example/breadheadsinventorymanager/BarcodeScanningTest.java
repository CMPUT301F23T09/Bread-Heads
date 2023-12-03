package com.example.breadheadsinventorymanager;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.widget.MenuPopupWindow;
import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.common.value.qual.StaticallyExecutable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)

@LargeTest
public class BarcodeScanningTest {
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
    private static CollectionReference itemDb;
    @Mock
    private static CollectionReference userDb;

    @Mock static FirestoreInteract interact;

    private Item itemDefault;
    private Item itemWithTags;
    private Item itemWithImages;

    @Before
    public void setup() {
        /*firestore = FirebaseFirestore.getInstance();
        itemDb = firestore.collection("testItems");
        userDb = firestore.collection("testUsers");
        interact = new FirestoreInteract(firestore, itemDb, userDb);

        ArrayList<String> imagePaths = new ArrayList<>();
        imagePaths.add("images/8a4100bd-7a73-4212-b7bd-ab0fd787cf0c");
        imagePaths.add("images/d2dee9b4-dcc1-42a9-88ca-a7b649e90c5c");

        ArrayList<Uri> imageUris = new ArrayList<>();
        imageUris.add(Uri.parse("content://media/picker/0/com.android.providers.media.photopicker/media/1000000070"));
        imageUris.add(Uri.parse("content://media/picker/0/com.android.providers.media.photopicker/media/1000000209"));

        ArrayList<String> tags = new ArrayList<>();
        tags.add("tagert");
        tags.add("tagalicious");
        itemDefault = new Item("01/01/2000", "itemDefault", "make", "model", "comments",1, "1234ABCD", imagePaths, imageUris, tags, "065800132245");
*/
        // intents for doing camera stuff
        Intents.init();
    }

    @Test
    public void startBarcode() {
        onView(withId(R.id.add_element)).perform(click());
        onView(withContentDescription(R.string.add_item)).perform(click());
        // I expect this to launch the ZXing scanner
        onView(withId(R.id.scan_barcode_button)).perform(click());

        // I cant seem to change the barcode in the emulated camera so I am manually going to enter barcodes for the purpose of testing
        // however I've tested several different types of barcodes with my irl phone and it gets the data correctly
        Intents.intending(hasAction("com.google.zxing.client.android.SCAN")); // Match any ZXing scan intent
        Intents.intended(Matchers.allOf(hasAction("com.google.zxing.client.android.SCAN")));
    }

    @Test
    public void getItemFromBarcode() throws InterruptedException {
        onView(withId(R.id.add_element)).perform(click());
        onView(withContentDescription(R.string.add_item)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("ScanBarcode"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("make"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("model"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("08/08/2000"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.item_comments_text)).perform(ViewActions.typeText("comments")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.item_barcode_text)).perform((ViewActions.typeText("1234567890"))).perform(ViewActions.closeSoftKeyboard());
        // idk how to add images via intents (espresso cannot test against the view outside the app i.e. the image gallery), however images have been tested for and are added and copied properly

        onView(withId(R.id.add_tag)).perform(click());
        onView(withText("Tag")).perform(click());

        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.add_element)).perform(click());
        onView(withContentDescription(R.string.add_item)).perform(click());

        // if the barcode exists, data associated with it will populate the editText fields
        onView(withId(R.id.item_barcode_text)).perform((ViewActions.typeText("1234567890"))).perform(ViewActions.closeSoftKeyboard());
        // sleep so we can query firestore
        // if ScanBarcode text is up there, then the rest of the data is too as they are checked
        onView(withText("ScanBarcode")).check(matches(isDisplayed()));
        onView(withText("make")).check(matches(isDisplayed()));
        onView(withText("model")).check(matches(isDisplayed()));
        onView(withText("08/08/2000")).check(matches(isDisplayed()));
        onView(withText("comments")).check(matches(isDisplayed()));
    }

    @Test
    public void checkTagsFromScannedBarcode() {
        // will check if the tags from an item scanned via barcode are the same

        // TODO add this test once tags are implemented
    }

    @Test
    public void checkImagesFromScannedBarcode() {
        // will test if images from an item scanned via a barcode are the same

        // TODO create this test once I figure out how image intents work in espresso
        // extensive image testing from scanned barcode done by me with irl phone, it works its fine. I just dont know how to test via espresso
    }
}
