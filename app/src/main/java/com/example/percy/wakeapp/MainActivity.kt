package com.example.percy.wakeapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.view.Gravity

class MainActivity : AppCompatActivity() {

    private var pendingIntent : PendingIntent? = null
    private lateinit var alarmManager : AlarmManager

    companion object {
        val DELAY_ARRAY = arrayOf(0, 5, 7)
        const val MINUTES_FROM_MILLISEC = 60 * 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        var intent = Intent(this, AlarmReceiver::class.java)

        set_alarm.setOnClickListener {

            if(set_alarm.isChecked) {
                Log.d("SetOnClick", "CHECKED")
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                calendar.set(Calendar.MINUTE, timePicker.minute)

                var hour = timePicker.hour.toString()
                var minute = timePicker.minute.toString()

                writeAlarmTime(minute, hour)

                setAlarm(intent, calendar)
            } else {
                Log.d("SetOnClick", "UNCHECKED")
                set_alarm.isChecked = true
                var toast = Toast.makeText(this, "ALARM ALREADY SET!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                toast.show()
            }
        }

        end_alarm.setOnClickListener {
            // Cancel alarm when player hasn't started yet
            if(pendingIntent != null && RingtoneService.mediaPlayer == null) {
                cancelAlarm(intent)
            } else if (RingtoneService.mediaPlayer != null) {    // Cancel the ongoing song (if one is ongoing)
                intent.putExtra("startPlayer", false)
                if(RingtoneService.ALARM_NBR == DELAY_ARRAY.size - 1) {    // Final alarm, cancel pendingIntent
                    cancelAlarm(intent)
                } else {    // Else; just stop the broadcast
                    Log.d("END_ALARM", "RINGTONE CANCELLED")
                    var hour = timePicker.hour.toString()
                    var minute = (timePicker.minute + DELAY_ARRAY[RingtoneService.ALARM_NBR + 1]).toString()
                    writeAlarmTime(minute, hour)
                    sendBroadcast(intent)
                }
            }
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

    private fun cancelAlarm(intent: Intent) {
        Log.d("END_ALARM", "ALARM CANCELLED")
        for (i in DELAY_ARRAY) {
            pendingIntent = PendingIntent.getBroadcast(this, i,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
        }
        set_alarm.isChecked = false
        intent.putExtra("cancelAlarm", true)
        updateText.text = "Alarm off!"
        sendBroadcast(intent)
    }

    private fun setAlarm(intent: Intent, calendar: Calendar) {
        intent.putExtra("startPlayer", true)
        for ((alarmNbr, i) in DELAY_ARRAY.withIndex()) {
            intent.putExtra("alarmNbr", alarmNbr)
            intent.putExtra("cancelAlarm", false)
            pendingIntent = PendingIntent.getBroadcast(this, i,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis + (i * MINUTES_FROM_MILLISEC),
                    pendingIntent)
        }
    }
}
