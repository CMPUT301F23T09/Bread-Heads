package com.example.breadheadsinventorymanager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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

        onView(withId(R.id.add_item)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Valid date entry"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("08/08/2000"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("Valid date entry")).check(matches(isDisplayed()));

        onView(withId(R.id.add_item)).perform(click());

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
        onView(withId(R.id.add_item)).perform(click());
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
        onView(withId(R.id.add_item)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Improper date test1"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("08242000"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withText("Invalid Date")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());

        // check if improper date is added (Date after today)
        onView(withId(R.id.add_item)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Improper date test2"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("04/11/2030"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withText("Invalid Date")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());

        // check if improper date is added (random characters)
        onView(withId(R.id.add_item)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Improper date test3"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("1!d'.%,/-"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withText("Invalid Date")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());

    }
}