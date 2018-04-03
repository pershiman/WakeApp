package com.example.percy.wakeapp

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.example.percy.wakeapp.R.id.timePicker
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.android.synthetic.main.content_main.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowAlarmManager
import org.robolectric.Shadows.shadowOf
import java.util.*


/**
 * Created by percy on 2018-03-26.
 */
@RunWith(RobolectricTestRunner::class)
class AlarmReceiverTest {

    lateinit var context: Context
    lateinit var shadowAlarmManager: ShadowAlarmManager
    lateinit var mainActivity: MainActivity

    @Before
    @Throws(Exception::class)
    fun setUp() {
        context = RuntimeEnvironment.application.applicationContext
        val alarmManager = RuntimeEnvironment.application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        shadowAlarmManager = shadowOf(alarmManager)

        mainActivity = MainActivity()
    }

    @Test
    @Throws(Exception::class)
    fun shouldScheduleAlarmEveryHourStartingInOneHour() {
        assertNull(shadowAlarmManager.nextScheduledAlarm)

        var calendar : Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 11)
        calendar.set(Calendar.MINUTE, 55)


        var alarmIntent = Intent(context, AlarmReceiver::class.java)

        mainActivity.setAlarm(alarmIntent, calendar)

        val scheduledAlarm = shadowAlarmManager.nextScheduledAlarm
        assertEquals(11, scheduledAlarm.interval)
        assertEquals(SystemClock.elapsedRealtime() + 11, scheduledAlarm.triggerAtTime)
        assertEquals(AlarmManager.ELAPSED_REALTIME, scheduledAlarm.type)
    }
}