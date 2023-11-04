package com.example.breadheadsinventorymanager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.view.View;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

@LargeTest
public class FilterByDateTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void FilterByEmptyDate() {

        // check if it works with no items in list
        onView(withId(R.id.filter_popup)).perform(click());
        onView(withContentDescription(R.string.Date)).perform(click());
        onView(withId(R.id.filter_date_start)).perform(ViewActions.typeText("01/01/1999"));
        onView(withId(R.id.filter_date_end)).perform(ViewActions.typeText("03/11/2023"));

        onView(withText("Valid date entry2")).check(doesNotExist());

        onView(withId(R.id.filter_popup)).perform(click());
        onView(withContentDescription(R.string.remove_filter)).perform(click());

    }
    @Test
    public void FilterByDate() {
        // add test data
        onView(withId(R.id.add_item)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Valid date entry1"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("23/04/2000"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.add_item)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Valid date entry2"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("abcdef"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("08/08/1990"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("123")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        // enter dates
        onView(withId(R.id.filter_popup)).perform(click());
        onView(withContentDescription(R.string.Date)).perform(click());
        onView(withId(R.id.filter_date_start)).perform(ViewActions.typeText("01/01/1999"));
        onView(withId(R.id.filter_date_end)).perform(ViewActions.typeText("02/11/2023"));
        onView(withId(R.id.date_filter_button)).perform(click());

        onView(withText("Valid date entry2")).check(doesNotExist());
    }
}

