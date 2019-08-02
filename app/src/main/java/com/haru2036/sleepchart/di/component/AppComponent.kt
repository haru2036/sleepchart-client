package com.haru2036.sleepchart.di.component

import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.module.AppModule
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.usecase.AccountUsecase
import com.haru2036.sleepchart.infra.api.client.AccountClient
import com.haru2036.sleepchart.infra.api.service.AccountService
import com.haru2036.sleepchart.infra.repository.AccountRepository
import com.haru2036.sleepchart.infra.repository.SharedPreferencesRepository
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

    fun inject(service: AccountService)

    fun inject(useCase: AccountUsecase)

    fun inject(repository: AccountRepository)

    fun inject(client: AccountClient)

    fun inject(sleepChart: SleepChart)

    fun inject(okHttpClient: OkHttpClient)

    fun inject(retrofit: Retrofit)

    fun inject(sharedPreferencesRepository: SharedPreferencesRepository)

    fun plus(sleepModule: SleepModule): SleepComponent
}
