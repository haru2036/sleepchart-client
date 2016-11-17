package com.haru2036.sleepchart.di.component

import android.content.Context
import com.haru2036.sleepchart.di.module.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by haru2036 on 16/11/11.
 */

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent{
    fun inject(context: Context)
}
