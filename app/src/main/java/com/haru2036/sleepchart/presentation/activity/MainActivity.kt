package com.haru2036.sleepchart.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.app.Constants
import com.haru2036.sleepchart.presentation.fragment.SleepChartFragment

class MainActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

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
            R.id.menu_main_export -> fragment.exportChart()
            R.id.menu_main_import_gadgetbridge -> fragment.importSleepsFromGadgetBridge()
            R.id.menu_main_import_googlefit -> fragment.importSleepsFromGoogleFit()
            R.id.menu_main_setting -> SettingsActivity.start(this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_IMPORT -> fragment.showSleeps()
        }
    }
}
