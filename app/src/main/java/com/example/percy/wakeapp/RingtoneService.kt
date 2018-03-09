package com.example.percy.wakeapp
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast


/**
 * Created by percy on 2018-03-02.
 */
class RingtoneService : Service() {

    companion object {
        var mMediaPlayer: MediaPlayer? = null
        var ALARM_NBR : Int = 0
        const val NOTIFICATION_ID = 123
        const val CHANNEL_ID = "my_channel_01"
    }

    private val TAG = RingtoneService::class.java.name


    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "startCommand")

        when {
            intent!!.extras["cancelAlarm"] == true -> {
                Log.d(TAG, "Cancel alarm")
                mMediaPlayer?.stop()
                mMediaPlayer?.release()
                mMediaPlayer = null
                ALARM_NBR = 0
            }
            intent.extras["startPlayer"] == true -> {
                Log.d(TAG, "TRUE")
                startPlayingSound(intent)
            }
            intent.extras["startPlayer"] == false -> {
                Log.d(TAG, "FALSE")
                stopPlayingSound()
            }
        }
        return START_NOT_STICKY
    }

    private fun stopPlayingSound() {
        if (mMediaPlayer!!.isPlaying) {
            Log.d(TAG, "MEDIA PLAYER IS PLAYING")
            mMediaPlayer?.stop()
        } else {    // When the user tries to cancel the app during a snooze
            Log.d(TAG, "TRYING TO DISABLE NOTIFICATION")

            val notificationIntent = Intent(this, MathIssueActivity::class.java)
            notificationIntent.action = Intent.ACTION_MAIN
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val pendingIntent = PendingIntent.getActivity(this, 123, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)


            val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("DISABLE SNOOZE")
                        .setContentText("Click to disable snooze")
                        .setSmallIcon(R.drawable.ic_disable_snooze)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
            } else {
                Notification.Builder(this)
                        .setContentTitle("DISABLE SNOOZE")
                        .setContentText("Click to disable snooze")
                        .setSmallIcon(R.drawable.ic_disable_snooze)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
            }

            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mChannel = NotificationChannel(CHANNEL_ID, "someChannelName", NotificationManager.IMPORTANCE_HIGH)
                mNotificationManager.createNotificationChannel(mChannel)
            }
            mNotificationManager.notify(NOTIFICATION_ID, notification.build())
        }
    }

    private fun startPlayingSound(intent: Intent?) {
        ALARM_NBR = intent!!.getIntExtra("alarmNbr", 0)
        val alarmType = intent!!.getIntExtra("alarmType", 0)

        if (mMediaPlayer != null) {
            mMediaPlayer?.release()
            mMediaPlayer = null
        }
        mMediaPlayer = MediaPlayer.create(this, pickAlarmTune(ALARM_NBR, alarmType))

        mMediaPlayer?.isLooping = true
        mMediaPlayer?.start()
    }

    private fun pickAlarmTune(alarmNbr: Int, alarmType: Int) : Int {

        return when (alarmType) {
            0 -> // Bird
                return when (alarmNbr) {
                    0 -> {
                        Log.d(TAG, "Cranebird started")
                        R.raw.cranebird
                    }
                    1 -> {
                        Log.d(TAG, "Nightbird started")
                        R.raw.nightbird
                    }
                    2 -> {
                        Log.d(TAG, "Warningbird started")
                        R.raw.warningbird
                    }
                    else -> R.raw.cranebird
                }
            1 -> // Cat
                return when (alarmNbr) {
                    0 -> {
                        Log.d(TAG, "Funny cat started")
                        R.raw.funnycat
                    }
                    1 -> {
                        Log.d(TAG, "Sad cat started")
                        R.raw.sadcat
                    }
                    2 -> {
                        Log.d(TAG, "Angry cat started")
                        R.raw.angrycat
                    }
                    else -> R.raw.funnycat
                }
            2 -> // Classic music
                return when (alarmNbr) {
                0 -> {
                    Log.d(TAG, "Canond alarm started")
                    R.raw.canondpiano
                }
                1 -> {
                    Log.d(TAG, "Swanlake alarm started")
                    R.raw.swanlake
                }
                2 -> {
                    Log.d(TAG, "Classicguitar alarm started")
                    R.raw.classicguitar
                }
                else -> R.raw.canondpiano
            }
            3 -> // Alarm
                return when (alarmNbr) {
                    0 -> {
                        Log.d(TAG, "Soft alarm started")
                        R.raw.softalarm
                    }
                    1 -> {
                        Log.d(TAG, "Happy alarm started")
                        R.raw.happyalarm
                    }
                    2 -> {
                        Log.d(TAG, "Annoying alarm started")
                        R.raw.annoyingalarm
                    }
                    else -> R.raw.softalarm
                }
            else -> R.raw.cranebird
        }
    }

    override fun onDestroy() {
        Toast.makeText(this, "on Destroy called", Toast.LENGTH_SHORT).show()
    }
}