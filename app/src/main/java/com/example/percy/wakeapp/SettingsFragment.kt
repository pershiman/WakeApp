package com.example.percy.wakeapp


import android.app.Fragment
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat


/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
