package com.haru2036.sleepchart.di.component

import com.haru2036.sleepchart.di.module.AppModule
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.usecase.GadgetBridgeUseCase
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.infra.api.client.SleepClient
import com.haru2036.sleepchart.infra.api.service.SleepService
import com.haru2036.sleepchart.infra.dao.GadgetBridgeDao
import com.haru2036.sleepchart.infra.repository.GadgetBridgeRepository
import com.haru2036.sleepchart.infra.repository.SleepRepository
import com.haru2036.sleepchart.presentation.activity.MainActivity
import com.haru2036.sleepchart.presentation.fragment.SleepChartFragment
import dagger.Subcomponent

/**
 * Created by haru2036 on 2016/11/28.
 */

@Subcomponent(modules = arrayOf(SleepModule::class, AppModule::class))
interface SleepComponent{

    fun inject(service: SleepService)

    fun inject(sleepUseCase: SleepUseCase)

    fun inject(sleepRepository: SleepRepository)

    fun inject(sleepClient: SleepClient)

    fun inject(activity: MainActivity)

    fun inject(fragment: SleepChartFragment)

    fun inject(gadgetBridgeUseCase: GadgetBridgeUseCase)

    fun inject(gadgetBridgeRepository: GadgetBridgeRepository)

    fun inject(gadgetBridgeDao: GadgetBridgeDao)

}
