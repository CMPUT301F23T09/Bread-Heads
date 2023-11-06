package com.example.breadheadsinventorymanager;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ItemDetailsActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    //runs before each test it creates a sample item
    public void createSampleItem() {
        onView(withId(R.id.add_item)).perform(click());
        onView(withId(R.id.item_name_text)).perform(replaceText("Sample Item"));
        onView(withId(R.id.item_make_text)).perform(replaceText("Sample Make"));
        onView(withId(R.id.item_model_text)).perform(replaceText("Sample Model"));
        onView(withId(R.id.item_acquisition_date_text)).perform(replaceText("01/01/2023"));
        onView(withId(R.id.item_value_text)).perform(replaceText("1000"));
        onView(withId(R.id.item_comments_text)).perform(replaceText("Sample Comment"));
        onView(withText("OK")).perform(click());
    }

    @Test
    // goes into item details and checks that the details in item details match the item
    // that was clicked
    public void testItemDetailsActivityInfo() {
        onView(withText("Sample Item")).perform(click());

        onView(withId(R.id.itemDescription)).check(matches(withText("Sample Item")));
        onView(withId(R.id.makeText)).check(matches(withText("Sample Make")));
        onView(withId(R.id.modelText)).check(matches(withText("Sample Model")));
        onView(withId(R.id.dateText)).check(matches(withText("01/01/2023")));
        onView(withId(R.id.commentText)).check(matches(withText("Sample Comment")));
        onView(withId(R.id.valueText)).check(matches(withText("10.00")));  // Adjust this value based on formatting
    }

    @Test
    //test that the back button works by going into item details activity then clicking back
    // then it checks for the search_list which is only in Main Activity
    public void testBackButton() {
        onView(withText("Sample Item")).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.search_list)).check(matches(isDisplayed()));
    }
}
