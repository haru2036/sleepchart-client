package com.haru2036.sleepchart.app

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.haru2036.sleepchart.di.component.AppComponent
import com.haru2036.sleepchart.di.component.DaggerAppComponent
import com.haru2036.sleepchart.di.module.AppModule
import com.jakewharton.threetenabp.AndroidThreeTen
import retrofit2.Retrofit
import javax.inject.Inject
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber


open class SleepChart : Application(){

    companion object{

        private lateinit var application: SleepChart

        fun getAppComponent(): AppComponent{
            return application.appComponent
        }

        fun getContext(): Context {
            return application.applicationContext
        }

        fun getRetrofit() = application.retrofit
    }

    private lateinit var appComponent: AppComponent

    @Inject
    lateinit var retrofit: Retrofit

    override fun onCreate() {
        super.onCreate()
        initializeDagger()
        application = this
        AndroidThreeTen.init(this)
        Timber.plant(Timber.DebugTree())
        Stetho.initializeWithDefaults(this)
        Fabric.with(this, Crashlytics())

    }

    fun initializeDagger(){
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
        appComponent.inject(this)
    }
}
