package com.example.breadheadsinventorymanager;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.AdditionalMatchers.not;

@RunWith(AndroidJUnit4.class)
public class ItemDetailsActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @Before
 //runs before each test, it creates a sample item
    public void createSampleItem() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(allOf(withId(R.id.add_element), withContentDescription("Add Element"), isDisplayed())).perform(click());
        onView(allOf(withId(android.R.id.title), withText("Add Item"), isDisplayed())).perform(click());

        onView(withId(R.id.item_name_text)).perform(typeText("00 Sample Item"), closeSoftKeyboard());
        onView(withId(R.id.item_make_text)).perform(typeText("Sample Make"), closeSoftKeyboard());
        onView(withId(R.id.item_model_text)).perform(typeText("Sample Model"), closeSoftKeyboard());
        onView(withId(R.id.serial_number_text)).perform(typeText("123456789"), closeSoftKeyboard());
        onView(withId(R.id.item_acquisition_date_text)).perform(typeText("12/12/2012"), closeSoftKeyboard());
        onView(withId(R.id.item_value_text)).perform(typeText("100"), closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(scrollTo(), click());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    // checks that information on the item details screen is correct
    public void checkItemDetailsScreen() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(0).perform(click());

        onView(withId(R.id.itemDescription)).check(matches(withText("00 Sample Item")));
        onView(withId(R.id.makeText)).check(matches(withText("Sample Make")));
        onView(withId(R.id.modelText)).check(matches(withText("Sample Model")));
        onView(withId(R.id.dateText)).check(matches(withText("12/12/2012")));

        onView(withContentDescription("Navigate up")).perform(click());
    }

    @Test
    // Checks that information on the item details screen is correct
    public void testEditing() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(0).perform(click());

        onView(withId(R.id.edit_item)).perform(click());

        onView(withId(R.id.edit_item_name_text)).perform(replaceText("01 Sample Item"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_item_make_text)).perform(replaceText("Sample Make 2"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_item_model_text)).perform(replaceText("Sample Model 2"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(replaceText("10/10/2022"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_item_value_text)).perform(replaceText("150.0"));
        closeSoftKeyboard();

        onView(withId(android.R.id.button1)).perform(scrollTo(), click());

        onView(withId(R.id.itemDescription)).check(matches(withText("01 Sample Item")));
        onView(withId(R.id.makeText)).check(matches(withText("Sample Make 2")));
        onView(withId(R.id.modelText)).check(matches(withText("Sample Model 2")));
        onView(withId(R.id.dateText)).check(matches(withText("10/10/2022")));
        onView(withId(R.id.valueText)).check(matches(withText("150.00")));

        onView(withId(R.id.edit_item)).perform(click());

        onView(withId(R.id.edit_item_name_text)).perform(replaceText("00 Sample Item"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_item_make_text)).perform(replaceText("Sample Make"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_item_model_text)).perform(replaceText("Sample Model"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(replaceText("12/12/2012"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_item_value_text)).perform(replaceText("100.0"));
        closeSoftKeyboard();

        onView(withId(android.R.id.button1)).perform(scrollTo(), click());

        onView(withContentDescription("Navigate up")).perform(click());
    }

    @Test
    //checks that the empty fields error box appears when a field is left empty
    public void checkEmptyFields() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(0).perform(click());

        onView(withId(R.id.edit_item)).perform(click());

        onView(withId(R.id.edit_item_make_text)).perform(replaceText(""));
        onView(withId(R.id.edit_item_make_text)).perform(closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(scrollTo(), click());
        onView(withId(R.id.edit_error_text_message)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_item_make_text)).perform(click());
        onView(withId(R.id.edit_item_make_text)).perform(replaceText("Sample Make"), closeSoftKeyboard());

        onView(withId(android.R.id.button2)).perform(scrollTo(), click());
        onView(withContentDescription("Navigate up")).perform(click());
    }
    @Test
    //checks that the Invalid Date error box appears when an Invalid year is entered
    public void checkInvalidYear() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(0).perform(click());

        onView(withId(R.id.edit_item)).perform(click());

        onView(withId(R.id.edit_item_acquisition_date_text)).perform(replaceText("12/12/2032"));
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(scrollTo(), click());
        onView(withId(R.id.edit_error_text_message)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(click());
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(replaceText("12/12/2012"));
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(closeSoftKeyboard());

        onView(withId(android.R.id.button2)).perform(scrollTo(), click());
        onView(withContentDescription("Navigate up")).perform(click());
    }
    @Test
    //checks that the Invalid Date error box appears when an Invalid month is entered
    public void checkInvalidMonth() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(0).perform(click());

        onView(withId(R.id.edit_item)).perform(click());

        onView(withId(R.id.edit_item_acquisition_date_text)).perform(click());
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(replaceText("12/42/2012"));
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(scrollTo(), click());
        onView(withId(R.id.edit_error_text_message)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(click());
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(replaceText("12/12/2012"));
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(closeSoftKeyboard());

        onView(withId(android.R.id.button2)).perform(scrollTo(), click());
        onView(withContentDescription("Navigate up")).perform(click());
    }
    @Test
    //checks that the Invalid Date error box appears when an Invalid Day is entered
    public void checkInvalidDay() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(0).perform(click());

        onView(withId(R.id.edit_item)).perform(click());

        onView(withId(R.id.edit_item_acquisition_date_text)).perform(click());
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(replaceText("82/12/2012"));
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(scrollTo(), click());
        onView(withId(R.id.edit_error_text_message)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(click());
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(replaceText("12/12/2012"));
        onView(withId(R.id.edit_item_acquisition_date_text)).perform(closeSoftKeyboard());

        onView(withId(android.R.id.button2)).perform(scrollTo(), click());
        onView(withContentDescription("Navigate up")).perform(click());
    }
    @Test
    //checks that the Invalid Value error box appears when an Invalid Value is entered
    public void checkInvalidValue() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(0).perform(click());

        onView(withId(R.id.edit_item)).perform(click());

        onView(withId(R.id.edit_item_value_text)).perform(replaceText("1.00.0"));
        onView(withId(R.id.edit_item_value_text)).perform(closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(scrollTo(), click());
        onView(withId(R.id.edit_error_text_message)).check(matches(isDisplayed()));

        onView(withId(android.R.id.button2)).perform(scrollTo(), click());
        onView(withContentDescription("Navigate up")).perform(click());
    }

    @After
    // Deletes Item
    public void checkDelete() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(anything()).inAdapterView(withId(R.id.items_main_list)).atPosition(0).perform(click());
        onView(withId(R.id.delete_item)).perform(click());

        onView(allOf(withId(android.R.id.message), withText("Are you sure you want to delete this item?"))).check(matches(isDisplayed()));

        // Click the confirmation button to delete the item
        onView(withId(android.R.id.button1)).perform(scrollTo(), click());

        // Check that the item is no longer present in the list
        //onView(withText("00 Sample Item")).check(doesNotExist());
    }
}
