package com.example.percy.wakeapp
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import android.app.PendingIntent


/**
 * Created by percy on 2018-03-02.
 */
class RingtoneService : Service() {

    companion object {
        var mediaPlayer: MediaPlayer? = null
        var ALARM_NBR : Int = 0
        const val NOTIFICATION_ID = 123
        const val CHANNEL_ID = "my_channel_01"
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
            intent.extras["startPlayer"] == true -> {
                Log.d("RingtoneService", "TRUE")
                startPlayingSound(intent)
            }
            intent.extras["startPlayer"] == false -> {
                Log.d("RingtoneService", "FALSE")
                if(mediaPlayer!!.isPlaying) {
                    Log.d("STOP_PLAYER", "MEDIA PLAYER IS PLAYING")
                    mediaPlayer?.stop()
                } else {    // When the user tries to cancel the app during a snooze
                    Log.d("STOP_PLAYER", "TRYING TO DISABLE NOTIFICATION")

                    val intent = Intent(this, MathIssueActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val pendingIntent = PendingIntent.getActivity(this, 123, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)

                    val notification = Notification.Builder(this, CHANNEL_ID)
                            .setContentTitle("DISABLE SNOOZE")
                            .setContentText("Click to disable snooze")
                            .setSmallIcon(R.drawable.ic_disable_snooze)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)

                    val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val mChannel = NotificationChannel(CHANNEL_ID, "someChannelName", NotificationManager.IMPORTANCE_HIGH)
                        mNotificationManager.createNotificationChannel(mChannel)
                    }
                    mNotificationManager.notify(NOTIFICATION_ID, notification.build())
                }
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
            intent.extras["alarmNbr"] == 0 -> {
                mediaPlayer = MediaPlayer.create(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                Log.d("startPlayingSound", "Default started")
            }
            intent.extras["alarmNbr"] == 1 -> {
                mediaPlayer = MediaPlayer.create(this, R.raw.cranebird)
                Log.d("startPlayingSound", "Cranebird started")
            }
            intent.extras["alarmNbr"] == 2 -> {
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