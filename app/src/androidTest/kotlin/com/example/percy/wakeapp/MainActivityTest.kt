package com.example.percy.wakeapp

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.view.View
import kotlinx.android.synthetic.main.content_main.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**ŒÜ
 * Created by percy on 2018-03-16.
 */
@LargeTest
@RunWith(JUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val mActivityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun ensureViewsArePresentActivity() {
        val activity = mActivityRule.activity as MainActivity
        val viewById: View? = activity.findViewById(R.id.activity_main)
        assertNotNull(viewById)
    }

    @Test
    fun alarmTextShouldNotBeSet() {
        val activity = mActivityRule.activity as MainActivity
        assertEquals(activity.updateText.text,"Did you set the alarm?")
    }

    @Test
    fun alarmShouldBeSetAfterClick() {
        val activity = mActivityRule.activity as MainActivity
        onView(withId(R.id.set_alarm)).perform(click());
        assertNotEquals(activity.updateText.text,"Did you set the alarm?")
    }
}