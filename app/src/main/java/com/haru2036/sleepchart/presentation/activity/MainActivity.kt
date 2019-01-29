package com.haru2036.sleepchart.presentation.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.presentation.fragment.SleepChartFragment

class MainActivity : AppCompatActivity() {

    val fragment: SleepChartFragment by lazy { SleepChartFragment.newInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrapper)
        if(savedInstanceState == null){
            fragmentManager.beginTransaction()
                    .add(R.id.activity_wrapper_fragment_container, fragment)
                    .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_main_import -> ImportActivity.start(this)
            R.id.menu_main_export -> fragment.exportChart()
            R.id.menu_main_setting -> SettingsActivity.start(this)
        }
        return super.onOptionsItemSelected(item)
    }

}
