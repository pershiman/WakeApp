package com.example.percy.wakeapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val KEY_PREF_ALARM_TYPE = "alarmPref"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }
}
