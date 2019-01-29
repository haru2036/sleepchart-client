package com.haru2036.sleepchart.presentation.fragment

import android.os.Bundle
import android.preference.PreferenceFragment
import com.haru2036.sleepchart.R

class SettingsFragment : PreferenceFragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference)
    }
}