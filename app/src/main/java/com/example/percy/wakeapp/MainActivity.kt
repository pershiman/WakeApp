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
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.name

    private var pendingIntent : PendingIntent? = null
    private lateinit var alarmManager : AlarmManager
    private lateinit var calendar : Calendar
    var alarmType : Int? = null

    companion object {
        val DELAY_ARRAY = arrayOf(0, 5, 7)
        const val MINUTES_FROM_MILLISEC = 60 * 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        calendar = Calendar.getInstance()
        Log.e(TAG, "onCreate called")
        Log.e(TAG, this.toString())
        if(savedInstanceState != null) {
            Log.d(TAG, "stuff saved")
            val hour = savedInstanceState.getString("hour")
            val minute = savedInstanceState.getString("minute")
            val alarmSet = savedInstanceState.getBoolean("ALARM_SET")
            calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour.toInt())
            calendar.set(Calendar.MINUTE, minute.toInt())

            if(alarmSet) {
                writeAlarmTime(minute, hour)
                set_alarm.isChecked = true
            }
        }

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var intent = Intent(this, AlarmReceiver::class.java)

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
                setAlarm(intent, calendar)
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
                cancelAlarm(intent)
            } else if (RingtoneService.mMediaPlayer != null) {    // Cancel the ongoing song (if one is ongoing)
                intent.putExtra("startPlayer", false)
                if(RingtoneService.ALARM_NBR == DELAY_ARRAY.size - 1) {    // Final alarm, cancel pendingIntent.
                    cancelAlarm(intent)
                    // Start webpage
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.svtplay.se/rapport"))
                    startActivity(browserIntent)
                } else {    // Else; just stop the broadcast
                    Log.d(TAG, "RINGTONE CANCELLED")
                    var hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
                    var minute = (calendar.get(Calendar.MINUTE) + DELAY_ARRAY[RingtoneService.ALARM_NBR + 1]).toString()
                    writeAlarmTime(minute, hour)
                    sendBroadcast(intent)
                }
            }
        }

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
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

    override fun onDestroy() {  // Ugly solution. Shouldn't use alarmManager if this behaviour is desired
        super.onDestroy()
        cancelAlarm(Intent(this, AlarmReceiver::class.java))
        Log.e(TAG, "Destroy called, cancel Alarms")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.e(TAG, "onSaveInstanceState called")
        Log.e(TAG, this.toString())
        outState?.let {
            outState.putSerializable("hour", calendar.get(Calendar.HOUR_OF_DAY))
            outState.putSerializable("minute", calendar.get(Calendar.MINUTE))
            outState.putSerializable("ALARM_SET", set_alarm.isChecked)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause called")
        var outState = Bundle()
        onSaveInstanceState(outState)
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

    private fun cancelAlarm(intent: Intent) {
        Log.d("END_ALARM", "ALARM CANCELLED")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e(TAG, "In onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == 0) {
            Log.d(TAG, "Cancel alarm")
            cancelAlarm(Intent(this, AlarmReceiver::class.java))
        } else if (resultCode == 1) {
            Log.d(TAG, "Cancel pressed, do nothing")
        }
    }
}
