package com.haru2036.sleepchart.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.presentation.fragment.SettingsFragment

class SettingsActivity : AppCompatActivity(){
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrapper)
        fragmentManager.beginTransaction()
                .replace(R.id.activity_wrapper_fragment_container, SettingsFragment())
                .commit()
    }
}
