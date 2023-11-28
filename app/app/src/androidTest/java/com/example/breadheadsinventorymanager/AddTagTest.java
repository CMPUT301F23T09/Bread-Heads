package com.example.breadheadsinventorymanager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddTagTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testAddTag() {
        // Click on the add_element button to show the menu
        onView(withId(R.id.add_element)).perform(click());

        // Click on the "Add Tag" option
        onView(withText("Add Tag")).perform(click());

        // Type tag name in the EditText
        onView(withId(R.id.tag_name_text)).perform(typeText("Test Tag"), closeSoftKeyboard());

        // Click on the "OK" button
        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void testAddTagCancel() {
        // Click on the add_element button to show the menu
        onView(withId(R.id.add_element)).perform(click());

        // Click on the "Add Tag" option
        onView(withText("Add Tag")).perform(click());

        // Click on the "Cancel" button
        onView(withId(android.R.id.button2)).perform(click());

        // Verify that the tag is not added
        onView(withText("Test Tag")).check(doesNotExist());
    }
}

