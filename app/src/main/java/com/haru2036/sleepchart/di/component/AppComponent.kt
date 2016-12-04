package com.haru2036.sleepchart.di.component

import android.content.Context
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.module.AppModule
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.presentation.activity.MainActivity
import dagger.Component
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by haru2036 on 16/11/11.
 */

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent{

    fun inject(sleepChart: SleepChart)

    fun inject(activity: MainActivity)

    fun inject(okHttpClient: OkHttpClient)

    fun inject(retrofit: Retrofit)

    fun plus(sleepModule: SleepModule): SleepComponent
}
