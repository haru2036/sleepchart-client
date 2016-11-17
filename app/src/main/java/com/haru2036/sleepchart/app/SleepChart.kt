package com.haru2036.sleepchart.app

import android.app.Application
import android.content.Context
import com.haru2036.sleepchart.di.component.AppComponent
import com.haru2036.sleepchart.di.component.DaggerAppComponent
import com.haru2036.sleepchart.di.module.AppModule

open class SleepChart : Application(){

    companion object{

        private lateinit var application: Application

        fun getAppComponent(): AppComponent{
            return (application as SleepChart).appComponent
        }

        fun getContext(): Context {
            return application.applicationContext
        }

    }
    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        application = this
        initializeDagger()
    }

    fun initializeDagger(){
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }
}
