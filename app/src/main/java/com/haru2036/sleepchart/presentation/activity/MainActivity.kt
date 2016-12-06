package com.haru2036.sleepchart.presentation.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.name

    @Inject
    lateinit var sleepUsecase: SleepUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SleepChart.getAppComponent().plus(SleepModule(SleepChart.getRetrofit())).inject(this)
        sleepUsecase.fetchSleeps()
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Timber.d(it.toString(), it.toString())},{ Timber.e(it.toString(), it.toString())})

    }
}
