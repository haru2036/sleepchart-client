package com.haru2036.sleepchart.di.module

import android.content.Context
import com.haru2036.sleepchart.app.SleepChart
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
class AppModule(private val application: SleepChart){
    @Provides
    fun provideContext(): Context{
        return application.applicationContext
    }

    @Provides
    fun provideSleepChart(): SleepChart = application

    @Provides
    fun provideOkHttpClient(): OkHttpClient{
        return OkHttpClient.Builder()
                .addInterceptor{ chain ->
                    val request = chain.request().newBuilder()
                    val maxAge = 60
                    request.addHeader("cache-control", "public, max-age=" + maxAge)
                    chain.proceed(request?.build())
                }
                .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()


}
