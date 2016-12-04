package com.haru2036.sleepchart.presentation.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.name

    //話を単純化するためにcontextで試してます
    @Inject
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SleepChart.getAppComponent().inject(this)
        context.applicationInfo.className
//        sleepUsecase.fetchSleeps().subscribe({ Log.d(it.toString(), it.toString())},{ Log.d(it.toString(), it.toString())})

    }
}
