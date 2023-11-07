package com.example.breadheadsinventorymanager;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.intent.Intents.intended;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SelectMultipleItemsTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

//    @Test
//    public void testSelectItems(){
//        // Add a couple items to the list
//
//        // Click on select and enter select mode
//        onView(withId(R.id.delete_item)).perform(click());
//        // Click on first item
//        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item)).atPosition(0).perform(click());
//        // Click on second item
//        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item)).atPosition(0).perform(click());
//        // Test to see whether both of the items are selected
//        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item)).atPosition(0).check(isSelected());
//        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item)).atPosition(1).check(isSelected());
//    }

    @Test
    public void testDeletingSingleItem(){
        // Add a item to the list
        onView(withId(R.id.add_item)).perform(click());
        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Item1"));
        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("make1"));
        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("model1"));
        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("01/01/2000"));
        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("500")).perform(ViewActions.closeSoftKeyboard());
        onView(withText("OK")).perform(click());

        // go into select mode
        onView(withId(R.id.delete_item)).perform(click());

        // click once for popup to go away
        onView(withText("Select all items you wish to delete")).perform(click());

//        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.items_main_list)).atPosition(0).perform(click());
//        onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(0).onChildView(isAssignableFrom(CheckBox.class)).perform(click());
        // TODO figure out how to click on checkbox

//        onData(is(instanceOf(CheckBox.class))).perform(click());

//        onData(is(instanceOf(CheckBox.class))).inAdapterView(withId(R.id.items_main_list)).atPosition(0).perform(click());
    }
//    @Test
//    public void testDeletingMultipleItems(){
//        // Add a couple items to the list
//        // Add a item to the list
//        onView(withId(R.id.add_item)).perform(click());
//        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Item1"));
//        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("make1"));
//        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("model1"));
//        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("01/01/2000"));
//        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("500")).perform(ViewActions.closeSoftKeyboard());
//        onView(withText("OK")).perform(click());
//
//        // Add another item to the list
//        onView(withId(R.id.add_item)).perform(click());
//        onView(withId(R.id.item_name_text)).perform(ViewActions.typeText("Item2"));
//        onView(withId(R.id.item_make_text)).perform(ViewActions.typeText("make2"));
//        onView(withId(R.id.item_model_text)).perform(ViewActions.typeText("model2"));
//        onView(withId(R.id.item_acquisition_date_text)).perform(ViewActions.typeText("02/01/2000"));
//        onView(withId(R.id.item_value_text)).perform(ViewActions.typeText("2222")).perform(ViewActions.closeSoftKeyboard());
//        onView(withText("OK")).perform(click());
//
//        // go into select mode
//        onView(withId(R.id.delete_item)).perform(click());
//
//        // click once for popup to go away
//        onView(withText("Select all items you wish to delete")).perform(click());
//
//
//        // Click on first item
//        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
//        // Click on second item
//        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
//
//        // Press confirm to delete all the selected items
//
//        // Check that they have been deleted
//
//    }
//private static Matcher<View> childAtPosition(
//        final Matcher<View> parentMatcher, final int position) {
//
//    return new TypeSafeMatcher<View>() {
//        @Override
//        public void describeTo(Description description) {
//            description.appendText("Child at position " + position + " in parent ");
//            parentMatcher.describeTo(description);
//        }
//
//        @Override
//        public boolean matchesSafely(View view) {
//            ViewParent parent = view.getParent();
//            return parent instanceof ViewGroup && parentMatcher.matches(parent)
//                    && view.equals(((ViewGroup) parent).getChildAt(position));
//        }
//    };
//}
}
