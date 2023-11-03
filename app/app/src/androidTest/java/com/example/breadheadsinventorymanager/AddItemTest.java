package com.example.breadheadsinventorymanager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import android.view.View;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

@LargeTest
public class AddItemTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void TestAddItem() {
        //TODO change assertions on whether something is added or not
        onView(withId(R.id.add_item)).perform(click());

        // test if dialog pops up by checking if one of the Edittext views is visible
        onView(withId(R.id.item_make_text)).check(matches(isDisplayed()));

        // check if not filling fields adds to the list
        onView(withId(R.id.item_name_text)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Fields empty test")).perform(ViewActions.closeSoftKeyboard());

        onView(withId(android.R.id.button1)).perform(click());

        onView(withText("Fields empty test")).check(doesNotExist());

        //check for enormous integers value
        onView(withId(R.id.item_name_text)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Big Int test"));
        onView(withId(R.id.item_make_text)).perform(click());
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(click());
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(click());
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("08/21/2020"));
        onView(withId(R.id.item_value_text)).perform(click());
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("99999999999999999999")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        // an item this big should not be added to the itemList
        onView(withText("Big Int test")).check(doesNotExist());

        // check if improper date is added (wrong format)
        onView(withId(R.id.add_item)).perform(click());
        onView(withId(R.id.item_name_text)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Improper date test1"));
        onView(withId(R.id.item_make_text)).perform(click());
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(click());
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(click());
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("08212000"));
        onView(withId(R.id.item_value_text)).perform(click());
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withText("Improper date test1")).check(doesNotExist());

        // check if improper date is added (Date after today)
        onView(withId(R.id.add_item)).perform(click());
        onView(withId(R.id.item_name_text)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Improper date test2"));
        onView(withId(R.id.item_make_text)).perform(click());
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(click());
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(click());
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("11/04/2023"));
        onView(withId(R.id.item_value_text)).perform(click());
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withText("Improper date test2")).check(doesNotExist());

        // check if improper date is added (ancient date entered)
        onView(withId(R.id.add_item)).perform(click());
        onView(withId(R.id.item_name_text)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Improper date test3"));
        onView(withId(R.id.item_make_text)).perform(click());
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(click());
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(click());
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("11/04/1600"));
        onView(withId(R.id.item_value_text)).perform(click());
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withText("Improper date test3")).check(doesNotExist());

    }
}