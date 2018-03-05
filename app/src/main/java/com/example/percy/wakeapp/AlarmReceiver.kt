package com.example.percy.wakeapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by percy on 2018-03-02.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        var ringtoneIntent = Intent(context, RingtoneService::class.java)
        ringtoneIntent.putExtra("startPlayer", intent!!.extras.getBoolean("startPlayer"))
        ringtoneIntent.putExtra("alarmNbr", intent!!.extras.getInt("alarmNbr"))
        context?.startService(ringtoneIntent)
    }
}