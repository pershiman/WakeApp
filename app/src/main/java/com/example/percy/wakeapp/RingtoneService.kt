package com.example.percy.wakeapp

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.widget.Toast

/**
 * Created by percy on 2018-03-02.
 */
class RingtoneService : Service() {

    companion object {
        var mediaPlayer: MediaPlayer? = null
        var ALARM_NBR : Int = 0
    }

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("In the ringtone service", "startCommand")

        when {
            intent!!.extras["cancelAlarm"] == true -> {
                Log.d("RingtoneService", "Cancel alarm")
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                ALARM_NBR = 0
            }
            intent!!.extras["startPlayer"] == true -> {
                Log.d("RingtoneService", "TRUE")
                startPlayingSound(intent)
            }
            intent!!.extras["startPlayer"] == false -> {
                Log.d("RingtoneService", "FALSE")
                mediaPlayer?.stop()
            }
        }
        return START_NOT_STICKY
    }

    private fun startPlayingSound(intent: Intent?) {
        ALARM_NBR = intent!!.getIntExtra("alarmNbr", 0)

        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
        when {
            intent!!.extras["alarmNbr"] == 0 -> {
                mediaPlayer = MediaPlayer.create(this, R.raw.cranebird)
                Log.d("startPlayingSound", "Cranebird started")
            }
            intent!!.extras["alarmNbr"] == 1 -> {
                mediaPlayer = MediaPlayer.create(this, R.raw.nightbird)
                Log.d("startPlayingSound", "Nightbird started")
            }
            intent!!.extras["alarmNbr"] == 2 -> {
                mediaPlayer = MediaPlayer.create(this, R.raw.warningbird)
                Log.d("startPlayingSound", "Warningbird started")
            }
            else -> return
        }
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        Toast.makeText(this, "on Destroy called", Toast.LENGTH_SHORT).show()
    }
}