package com.haru2036.sleepchart.presentation.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.presentation.adapter.MainSleepAdapter
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.name

    @Inject
    lateinit var sleepUsecase: SleepUseCase

    val subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listView = findViewById(R.id.activity_main_list) as ListView
        SleepChart.getAppComponent().plus(SleepModule(SleepChart.getRetrofit())).inject(this)
        subscriptions.add(sleepUsecase.fetchSleeps()
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(it.toString(), it.toString())
                    listView.adapter = MainSleepAdapter(this, it)
                },{
                    Log.e(it.toString(), it.toString())
                })
        )
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.unsubscribe()
    }
}
