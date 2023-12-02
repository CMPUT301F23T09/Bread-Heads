package com.example.breadheadsinventorymanager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

@LargeTest
public class AddItemTest {
    // code to test activity with intent adapted from https://stackoverflow.com/a/57777912
    public static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("skip_auth", true);
        intent.putExtras(bundle);
    }
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(intent);

    @Test
    public void TestAddItem() {
        onView(withId(R.id.add_element)).perform(click());
        onView(withText("Add Item")).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Valid date entry"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("08/08/2000"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("Valid date entry")).check(matches(isDisplayed()));

        onView(withId(R.id.add_element)).perform(click());
        onView(withText("Add Item")).perform(click());

        // test if dialog pops up by checking if one of the Edittext views is visible
        onView(withId(R.id.item_make_text)).check(matches(isDisplayed()));

        // check if not filling fields adds to the list
        onView(withId(R.id.item_name_text)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Fields empty test")).perform(ViewActions.closeSoftKeyboard());

        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("Empty Fields")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());

    }

    @Test
    public void TestWeirdValue() {

        //check for enormous integers value
        onView(withId(R.id.add_element)).perform(click());
        onView(withText("Add Item")).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Big Int test"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("21/08/2020"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("99999999999999999999")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withText("Invalid Value")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());

    }

    @Test
    public void TestDate() {

        // check if improper date is added (wrong format)
        onView(withId(R.id.add_element)).perform(click());
        onView(withText("Add Item")).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Improper date test1"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("08242000"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withText("Invalid Date")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());

        // check if improper date is added (Date after today)
        onView(withId(R.id.add_element)).perform(click());
        onView(withText("Add Item")).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Improper date test2"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("04/11/2030"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withText("Invalid Date")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());

        // check if improper date is added (random characters)
        onView(withId(R.id.add_element)).perform(click());
        onView(withText("Add Item")).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Improper date test3"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("1!d'.%,/-"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("Invalid Date")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());
    }

    @Test
    public void TestAddingTag() {
        // Click on the add_element button to show the menu
        onView(withId(R.id.add_element)).perform(click());

        // Click on the "Add Tag" option
        onView(withText("Add Tag")).perform(click());

        // Type tag name in the EditText
        onView(withId(R.id.tag_name_text)).perform(typeText("Test Tag"), closeSoftKeyboard());

        // Click on the "OK" button
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.add_element)).perform(click());
        onView(withText("Add Item")).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Tagged Item"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("08/08/2000"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.add_tag)).perform(click());
        onView(withText("Test Tag")).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.add_tag)).perform(click());
        onView(withText("Test Tag")).check(matches(isChecked()));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("Tagged Item")).check(matches(isDisplayed()));

    }



}