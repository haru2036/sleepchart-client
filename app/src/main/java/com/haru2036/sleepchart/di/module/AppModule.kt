package com.haru2036.sleepchart.di.module

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.OrmaHandler
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.haru2036.sleepchart.domain.entity.OrmaDatabase
import com.haru2036.sleepchart.domain.usecase.AccountUsecase
import com.haru2036.sleepchart.infra.api.client.AccountClient
import com.haru2036.sleepchart.infra.api.service.AccountService
import com.haru2036.sleepchart.infra.repository.AccountRepository
import com.haru2036.sleepchart.infra.repository.SharedPreferencesRepository
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


@Module
class AppModule(private val application: SleepChart){
    @Provides
    fun provideContext(): Context{
        return application.applicationContext
    }

    @Provides
    fun provideAccountChart(): SleepChart = application

    @Provides
    fun provideOkHttpClient(): OkHttpClient{
        return OkHttpClient.Builder()
                .addInterceptor{ chain ->
                    val request = chain.request().newBuilder()
                    val maxAge = 60
                    val token = "" //todo: トークン読み出す
                    request.addHeader("cache-control", "public, max-age=" + maxAge)
                    request.addHeader("Authorization", "Bearer ${token}")
                    chain.proceed(request?.build())
                }.addNetworkInterceptor(StethoInterceptor())
                .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://sleepchart.haru2036.com/api/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    fun provideOrmaDatabase(context: Context): OrmaHandler = OrmaHandler(context)

    @Provides
    fun provideSharedPreferenceAccessor(context: Context) = SharedPreferencesRepository(context)

    @Provides
    fun provideAccountUseCase(accountRepository: AccountRepository) = AccountUsecase(accountRepository)

    @Provides
    fun provideAccountRepository(accountClient: AccountClient) = AccountRepository(accountClient)

    @Provides
    fun provideAccountClient(retrofit: Retrofit) = AccountClient(retrofit.create(AccountService::class.java))


}
