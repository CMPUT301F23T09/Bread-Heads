package com.example.breadheadsinventorymanager;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.app.Activity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
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
    public void testSelectItems(){
        // Add a couple items to the list

        // Click on select and enter select mode
        onView(withId(R.id.delete_item)).perform(click());
        // Click on first item
        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        // Click on second item
        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        // Test to see whether both of the items are selected
        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).check(isSelected());
        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item_list)).atPosition(1).check(isSelected());
    }

    @Test
    public void testDeletingMultipleItems(){
        // Add a couple items to the list

        // Click on select and enter select mode
        onView(withId(R.id.delete_item)).perform(click());
        // Click on first item
        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        // Click on second item
        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());

        // Press confirm to delete all the selected items

        // Check that they have been deleted

    }
}
