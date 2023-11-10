package com.example.breadheadsinventorymanager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.KeyEvent;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

@LargeTest
public class FilterListTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    // TODO write tests
    // individual date testing already has cases written for it in FilterByDatetest

    // these are not very rigorous tests, TODO add moar
    @Test
    public void testMakeFilter() {
        onView(withId(R.id.filter_popup)).perform(click());
        onView(withContentDescription(R.string.make)).perform(click());
        onView(withContentDescription("D's Make")).perform(click());

        onView(withText("B's Make")).check(doesNotExist());

        onView(withId(R.id.filter_popup)).perform(click());
        onView(withContentDescription(R.string.make)).perform(click());
        onView(withContentDescription("B's Make")).perform(click());

        // multiple makes
        onView(withText("B's Make")).check(matches(isDisplayed()));
        onView(withText("D's Make")).check(matches(isDisplayed()));

    }

    // TODO test refuses to edit the searchView
    @Test
    public void testDescriptionFilter() {
        onView(withId(R.id.filter_popup)).perform(click());
        onView(withContentDescription(R.string.description)).perform(click());
        onView(withId(R.id.search_view)).perform(click());
        onView(withId(R.id.search_view)).perform(ViewActions.typeText("d")).perform(pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withText("D's Make")).check(matches(isDisplayed()));

        onView(withId(R.id.search_view)).perform(click());
        onView(withId(R.id.search_view)).perform(ViewActions.typeText("dd"));

        onView(withText("D's Make")).check(doesNotExist());

        onView(withContentDescription("d")).perform(click());

    }

    // TODO implement
    @Test
    public void multipleFilter() {

    }

}