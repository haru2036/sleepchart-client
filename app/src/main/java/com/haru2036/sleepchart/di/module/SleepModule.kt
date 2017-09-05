package com.haru2036.sleepchart.di.module

import com.haru2036.sleepchart.infra.api.client.SleepClient
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.OrmaHandler
import com.haru2036.sleepchart.domain.entity.OrmaDatabase
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.infra.api.service.SleepService
import com.haru2036.sleepchart.infra.dao.SleepDao
import com.haru2036.sleepchart.infra.dao.SleepSessionDao
import com.haru2036.sleepchart.infra.repository.SleepRepository
import com.haru2036.sleepchart.presentation.activity.MainActivity
import com.haru2036.sleepchart.presentation.viewmodel.EditSleepViewModel
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * Created by haru2036 on 2016/11/28.
 */

@Module
class SleepModule{

    @Provides
    fun provideSleepDao(ormaHandler: OrmaHandler) = SleepDao(ormaHandler)

    @Provides
    fun provideSleepSessionDao(ormaHandler: OrmaHandler) = SleepSessionDao(ormaHandler)

    @Provides
    fun provideSleepUseCase(sleepRepository: SleepRepository) = SleepUseCase(sleepRepository)

    @Provides
    fun provideSleepRepository(sleepClient: SleepClient, sleepDao: SleepDao, sleepSessionDao: SleepSessionDao) = SleepRepository(sleepClient, sleepDao, sleepSessionDao)

    @Provides
    fun provideSleepClient(retrofit: Retrofit) = SleepClient(retrofit.create(SleepService::class.java))

    @Provides
    fun provideEditSleepViewModel(sleepUseCase: SleepUseCase) = EditSleepViewModel(sleepUseCase)


}
