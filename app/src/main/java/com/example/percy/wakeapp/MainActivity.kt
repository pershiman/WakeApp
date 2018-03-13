package com.example.percy.wakeapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.percy.wakeapp.MathIssueActivity.Companion.CORRECT_ANSWER
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.name

    private var pendingIntent : PendingIntent? = null
    private lateinit var alarmManager : AlarmManager
    private lateinit var calendar : Calendar
    private var alarmType : Int? = null

    companion object {
        val DELAY_ARRAY = arrayOf(0, 5, 7)
        const val MINUTES_FROM_MILLISEC = 60 * 1000
        const val HOUR_TAG = "hour"
        const val MINUTE_TAG = "minute"
        const val ALARM_SET_TAG = "alarmSet"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        calendar = Calendar.getInstance()
        Log.e(TAG, "onCreate called")
        Log.e(TAG, this.toString())

        displayAlarmType()

        restoreInstance(savedInstanceState)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var alarmIntent = Intent(this, AlarmReceiver::class.java)

        set_alarm.setOnClickListener {
            if(set_alarm.isChecked) {
                Log.d(TAG, "CHECKED")
                calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                calendar.set(Calendar.MINUTE, timePicker.minute)

                var hour = timePicker.hour.toString()
                var minute = timePicker.minute.toString()

                writeAlarmTime(minute, hour)
                if (calendar.before(Calendar.getInstance())) {
                    Log.d(TAG, "ALARM set before current time. Adding a day")
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                setAlarm(alarmIntent, calendar)
            } else {
                Log.d(TAG, "UNCHECKED")
                set_alarm.isChecked = true
                var toast = Toast.makeText(this, "ALARM ALREADY SET!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                toast.show()
            }
        }

        end_alarm.setOnClickListener {
            // Cancel alarm when player hasn't started yet
            if(pendingIntent != null && RingtoneService.mMediaPlayer == null) {
                cancelAlarm(alarmIntent)
            } else if (RingtoneService.mMediaPlayer != null) {    // Cancel the ongoing song (if one is ongoing)
                alarmIntent.putExtra("startPlayer", false)
                if(RingtoneService.ALARM_NBR == DELAY_ARRAY.size - 1) {    // Final alarm, cancel pendingIntent.
                    cancelAlarmAndOpenWebPage(alarmIntent)
                } else {    // Else; just stop the broadcast
                    Log.d(TAG, "RINGTONE CANCELLED")
                    var hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
                    var minute = (calendar.get(Calendar.MINUTE) + DELAY_ARRAY[RingtoneService.ALARM_NBR + 1]).toString()
                    writeAlarmTime(minute, hour)
                    sendBroadcast(alarmIntent)
                }
            }
        }

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }



    private fun displayAlarmType() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        alarmType = sharedPref.getString(SettingsActivity.KEY_PREF_ALARM_TYPE, "0").toInt()
        val alarmTypes = resources.getStringArray(R.array.alarmTypeArray)
        Toast.makeText(this, "AlarmType: " + alarmTypes[alarmType!!], Toast.LENGTH_SHORT).show()
    }

    private fun restoreInstance(savedInstanceState: Bundle?) {
        Log.e(TAG, "Saved instance: $savedInstanceState")
        if (savedInstanceState != null && savedInstanceState.getBoolean(ALARM_SET_TAG)) {
            Log.d(TAG, "stuff restored")
            val hour = savedInstanceState.getInt(HOUR_TAG)
            val minute = savedInstanceState.getInt(MINUTE_TAG)
            val alarmSet = savedInstanceState.getBoolean(ALARM_SET_TAG)
            calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            if (alarmSet) {
                writeAlarmTime(minute.toString(), hour.toString())
                set_alarm.isChecked = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.e(TAG, "onRestart called")

        if(CORRECT_ANSWER) {
            CORRECT_ANSWER = false
            Toast.makeText(this, "CORRECT ANSWER: ALARM CANCELLED", Toast.LENGTH_SHORT).show()
            cancelAlarmAndOpenWebPage(Intent(this, AlarmReceiver::class.java))
        } else {
            displayAlarmType()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            Log.e(TAG, "Saving in onSaveInstanceState")
            putInt(HOUR_TAG, calendar.get(Calendar.HOUR_OF_DAY))
            putInt(MINUTE_TAG, calendar.get(Calendar.MINUTE))
            putBoolean(ALARM_SET_TAG, set_alarm.isChecked)
        }
    }

    private fun writeAlarmTime(minute: String, hour: String) {
        var minute1 = minute
        var hour1 = hour

        if (minute.toInt() > 59) {
            minute1 = (minute.toInt() - 60).toString()
            hour1 = (hour.toInt() + 1).toString()
        }
        if (minute1.toInt() < 10) {
            minute1 = "0" + minute1
        }
        updateText.text = "Alarm set to: $hour1:$minute1"
    }

    private fun setAlarm(intent: Intent, calendar: Calendar) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        alarmType = sharedPref.getString(SettingsActivity.KEY_PREF_ALARM_TYPE, "0").toInt()

        intent.putExtra("startPlayer", true)
        intent.putExtra("cancelAlarm", false)
        intent.putExtra("alarmType", alarmType!!)
        Log.d(TAG, (calendar.timeInMillis - Calendar.getInstance().timeInMillis).toString())

        Log.d(TAG, "ALARMTYPE: $alarmType")
        for ((alarmNbr, i) in DELAY_ARRAY.withIndex()) {
            intent.putExtra("alarmNbr", alarmNbr)
            pendingIntent = PendingIntent.getBroadcast(this, i,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis + (i * MINUTES_FROM_MILLISEC),
                    pendingIntent)
        }
    }

    private fun cancelAlarmAndOpenWebPage(intent: Intent) {
        // Start webPage
        Log.d(TAG, "Starting webPage")
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.svtplay.se/rapport"))
        browserIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(browserIntent)
        cancelAlarm(intent)
    }

    private fun cancelAlarm(intent: Intent) {
        Log.d(TAG, "ALARM CANCELLED")
        for (i in DELAY_ARRAY) {
            pendingIntent = PendingIntent.getBroadcast(this, i,
                    intent, PendingIntent.FLAG_NO_CREATE)
            alarmManager.cancel(pendingIntent)
        }
        set_alarm.isChecked = false
        intent.putExtra("cancelAlarm", true)
        updateText.text = "Alarm off!"
        sendBroadcast(intent)
    }
}
