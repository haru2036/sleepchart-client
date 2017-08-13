package com.haru2036.sleepchart.presentation.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.presentation.fragment.SleepChartFragment
import rx.subscriptions.CompositeSubscription

class MainActivity : AppCompatActivity() {

    val fragment: SleepChartFragment by lazy { SleepChartFragment.newInstance() }


    val subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null){
            fragmentManager.beginTransaction()
                    .add(R.id.activity_main_fragment_container, fragment)
                    .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_main_export -> fragment.exportChart()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.unsubscribe()
    }
}
