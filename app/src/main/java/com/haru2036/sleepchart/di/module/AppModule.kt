package com.haru2036.sleepchart.di.module

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.haru2036.sleepchart.BuildConfig
import com.haru2036.sleepchart.LoginActivity
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.OrmaHandler
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.haru2036.sleepchart.domain.usecase.AccountUsecase
import com.haru2036.sleepchart.infra.api.client.AccountClient
import com.haru2036.sleepchart.infra.api.service.AccountService
import com.haru2036.sleepchart.infra.repository.AccountRepository
import com.haru2036.sleepchart.infra.repository.SharedPreferencesRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Rfc3339DateJsonAdapter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.util.*


@Module
class AppModule(private val application: SleepChart){
    @Provides
    fun provideContext(): Context{
        return application.applicationContext
    }

    @Provides
    fun provideAccountChart(): SleepChart = application

    @Provides
    fun provideOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor{ chain ->
                    val request = chain.request().newBuilder()
                    val maxAge = 60
                    val account = GoogleSignIn.getLastSignedInAccount(context)
                    if (account?.isExpired != false) {
                        LoginActivity.start(context)
                    }
                    request.addHeader("cache-control", "public, max-age=" + maxAge)
                    request.addHeader("Authorization", "Bearer ${account?.idToken}")
                    chain.proceed(request?.build())
                }.addNetworkInterceptor(StethoInterceptor())
                .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val url = BuildConfig.SERVER_URL
        return Retrofit.Builder()
            .client(okHttpClient)
                .baseUrl(url)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter()).build()))
            .build()
    }

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
