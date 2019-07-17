package com.haru2036.sleepchart.di.module

import android.content.Context
import com.haru2036.sleepchart.di.OrmaHandler
import com.haru2036.sleepchart.domain.usecase.GadgetBridgeUseCase
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.infra.repository.SharedPreferencesRepository
import com.haru2036.sleepchart.infra.api.client.SleepClient
import com.haru2036.sleepchart.infra.api.service.SleepService
import com.haru2036.sleepchart.infra.dao.GadgetBridgeDao
import com.haru2036.sleepchart.infra.dao.SleepDao
import com.haru2036.sleepchart.infra.dao.SleepSessionDao
import com.haru2036.sleepchart.infra.repository.GadgetBridgeRepository
import com.haru2036.sleepchart.infra.repository.SleepRepository
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
    fun provideSharedPreferenceAccessor(context: Context) = SharedPreferencesRepository(context)

    @Provides
    fun provideSleepSessionDao(ormaHandler: OrmaHandler) = SleepSessionDao(ormaHandler)

    @Provides
    fun provideSleepUseCase(sleepRepository: SleepRepository) = SleepUseCase(sleepRepository)

    @Provides
    fun provideSleepRepository(sleepClient: SleepClient, sleepDao: SleepDao, sleepSessionDao: SleepSessionDao) = SleepRepository(sleepClient, sleepDao, sleepSessionDao)

    @Provides
    fun provideSleepClient(retrofit: Retrofit) = SleepClient(retrofit.create(SleepService::class.java))

    @Provides
    fun provideGadgetBridgeDao() = GadgetBridgeDao()

    @Provides
    fun provideGadgetBridgeRepository(gadgetBridgeDao: GadgetBridgeDao) = GadgetBridgeRepository(gadgetBridgeDao)

    @Provides
    fun provideGadgetBridgeUseCase(gadgetBridgeRepository: GadgetBridgeRepository, sleepRepository: SleepRepository, sharedPreferencesRepository: SharedPreferencesRepository) = GadgetBridgeUseCase(gadgetBridgeRepository, sleepRepository, sharedPreferencesRepository)


}
