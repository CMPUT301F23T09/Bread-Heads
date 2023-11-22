package com.example.breadheadsinventorymanager;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
//import static androidx.test.espresso.intent.Intents.intended;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasToString;

import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SelectMultipleItemsTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testDeletingSingleItem(){
        // Add a item to the list
        //TODO: update tests, no longer add_item. Instead we click add_element followed by add_new_item
        onView(withId(R.id.add_element)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("testDel4"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("make1"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("model1"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("01/01/2000"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("5500")).perform(ViewActions.closeSoftKeyboard());
        onView(withText("OK")).perform(click());

        // go into select mode
        onView(withId(R.id.delete_item)).perform(click());

        // click once for popup to go away
        onView(withText("Select all items you wish to delete")).perform(click());

        // keep scrolling until you find the item
        boolean success = false;
        int position = 0;
        while(!success){
            try {
                onView(withText("testDel4")).perform(click());
                success = true;
            }
            catch (Exception e){
                onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(position).perform(swipeUp());
                position += 1;
            }
        }

        // delete the item
        onView(withId(R.id.select_mode_confirm)).perform(click());

        //check that it got deleted
        onView(withText("testDel4")).check(doesNotExist());
    }
    @Test
    public void testDeletingMultipleItems(){
        // Add a couple items to the list
        // Add a item to the list
        onView(withId(R.id.add_element)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("testDel1234"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("make1"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("model1"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("01/01/2000"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("500")).perform(ViewActions.closeSoftKeyboard());
        onView(withText("OK")).perform(click());

        // Add another item to the list
        onView(withId(R.id.add_element)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("testDel2456"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("make2"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("model2"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("02/01/2000"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("2222")).perform(ViewActions.closeSoftKeyboard());
        onView(withText("OK")).perform(click());

        // go into select mode
        onView(withId(R.id.delete_item)).perform(click());

        // click once for popup to go away
        onView(withText("Select all items you wish to delete")).perform(click());


        // Click on first item
        boolean success = false;
        int position = 0;
        while(!success){
            try {
                onView(withText("testDel1234")).perform(click());
                success = true;
            }
            catch (Exception e){
                onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(position).perform(swipeUp());
                position += 1;
            }
        }

        // Click on second item
        success = false;
        position = 0;
        while(!success){
            try {
                onView(withText("testDel2456")).perform(click());
                success = true;
            }
            catch (Exception e){
                onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(position).perform(swipeUp());
                position += 1;
            }
        }

        // Press confirm to delete all the selected items
        onView(withId(R.id.select_mode_confirm)).perform(click());

        // Check that they have been deleted
        onView(withText("testDel1234")).check(doesNotExist());
        onView(withText("testDel2456")).check(doesNotExist());
    }

}
