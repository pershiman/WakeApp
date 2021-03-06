package com.example.percy.wakeapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK

/**
 * Created by percy on 2018-03-02.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        var ringtoneIntent = Intent(context, RingtoneService::class.java)

        intent?.let {
            ringtoneIntent.putExtra("startPlayer", intent.extras.getBoolean("startPlayer"))
            ringtoneIntent.putExtra("alarmNbr", intent.extras.getInt("alarmNbr"))
            ringtoneIntent.putExtra("cancelAlarm", intent.extras.getBoolean("cancelAlarm"))
            ringtoneIntent.putExtra("alarmType", intent.extras.getInt("alarmType"))
        }
        context?.startService(ringtoneIntent)
        val mainActivityIntent = Intent(context, MainActivity::class.java)
        mainActivityIntent.flags = (FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(mainActivityIntent)
    }
}