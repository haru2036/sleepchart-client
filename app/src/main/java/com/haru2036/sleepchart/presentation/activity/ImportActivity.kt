package com.haru2036.sleepchart.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.usecase.GadgetBridgeUseCase
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_import.*
import javax.inject.Inject

class ImportActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ImportActivity::class.java))
        }
    }

    @Inject
    lateinit var gadgetBridgeUseCase: GadgetBridgeUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SleepChart.getAppComponent().plus(SleepModule()).inject(this)
        setContentView(R.layout.activity_import)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            gadgetBridgeUseCase.syncActivity()
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        Snackbar.make(view, "sleeps imported", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()

                    }, {
                        Log.e("Failed to import GB DB", it.toString())
                    })
        }
    }

}
