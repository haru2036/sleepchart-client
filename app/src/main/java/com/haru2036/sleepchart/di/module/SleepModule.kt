package com.haru2036.sleepchart.di.module

import com.haru2036.sleepchart.infra.api.client.SleepClient
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.infra.api.service.SleepService
import com.haru2036.sleepchart.infra.repository.SleepRepository
import com.haru2036.sleepchart.presentation.activity.MainActivity
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * Created by haru2036 on 2016/11/28.
 */

@Module
class SleepModule(val retrofit: Retrofit){

    @Provides
    fun provideSleepUseCase(sleepRepository: SleepRepository) = SleepUseCase(sleepRepository)

    @Provides
    fun provideSleepRepository(sleepClient: SleepClient) = SleepRepository(sleepClient)

    @Provides
    fun provideSleepClient() = SleepClient(retrofit.create(SleepService::class.java))


}
