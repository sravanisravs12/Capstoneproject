package com.example.automativesurvallianceseven;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class IntegrationTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testCaptureAndStoreImage() {
        // Assuming you have a button to start the capture process
        onView(withId(R.id.captureButton)).perform(click());

        // Wait for a while to let the image capture and store process complete
        try {
            Thread.sleep(5000); // Adjust the sleep time as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the image is stored correctly
        // For example, check the TextView or ImageView that displays the image path or thumbnail
        onView(withId(R.id.alertTextView)).check(matches(withText("Expected Image Path")));
}
}

