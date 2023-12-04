package com.example.breadheadsinventorymanager;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddTagToMultipleItems {
    // code to test activity with intent adapted from https://stackoverflow.com/a/57777912
    public static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("skip_auth", true);
        intent.putExtras(bundle);
    }
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(intent);

    @Test
    public void testAddingTagToTwoItems(){
        // Add a tag to tag list
        onView(withId(R.id.add_element)).perform(click());
        onView(withText("Add Tag")).perform(click());
        onView(withId(R.id.tag_name_text)).perform(ViewActions.typeText("testTag"));
        onView(withText("OK")).perform(click());

        // Add a item to the list
        onView(withId(R.id.add_element)).perform(click());
        onView(withText("Add Item")).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("testTagItem"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("make"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("model"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("01/01/2000"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("5500")).perform(ViewActions.closeSoftKeyboard());
        onView(withText("OK")).perform(click());

        // Add another item to the list
        onView(withId(R.id.add_element)).perform(click());
        onView(withText("Add Item")).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("testTagItem2"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("make"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("model"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("01/01/2000"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("5500")).perform(ViewActions.closeSoftKeyboard());
        onView(withText("OK")).perform(click());

        // go into select mode
        onView(withId(R.id.delete_item)).perform(click());

        // click once for popup to go away
        onView(withText("Select items to delete or add tags")).perform(click());

        // keep scrolling until you find the item
        boolean success = false;
        int position = 0;
        while(!success){
            try {
                onView(withText("testTagItem")).perform(click());
                success = true;
            }
            catch (Exception e){
                onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(position).perform(swipeUp());
                position += 1;
            }
        }

        success = false;
        position = 0;
        while(!success){
            try {
                onView(withText("testTagItem2")).perform(click());
                success = true;
            }
            catch (Exception e){
                onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(position).perform(swipeUp());
                position += 1;
            }
        }
        // click add tags
        onView(withId(R.id.select_mode_add_tags)).perform(click());

        // click on the tag that has been added
        onView(withText("testTag")).perform(click());

        // click confirm
        onView(withText("Confirm")).perform(click());

        // Delete the Item
        // go into select mode
        onView(withId(R.id.delete_item)).perform(click());

        // click once for popup to go away
        onView(withText("Select items to delete or add tags")).perform(click());

        // keep scrolling until you find the item
        success = false;
        position = 0;
        while(!success){
            try {
                onView(withText("testTagItem")).perform(click());
                success = true;
            }
            catch (Exception e){
                onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(position).perform(swipeUp());
                position += 1;
            }
        }
        success = false;
        position = 0;
        while(!success){
            try {
                onView(withText("testTagItem2")).perform(click());
                success = true;
            }
            catch (Exception e){
                onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(position).perform(swipeUp());
                position += 1;
            }
        }

        // delete the item
        onView(withId(R.id.select_mode_confirm)).perform(click());
    }

}
