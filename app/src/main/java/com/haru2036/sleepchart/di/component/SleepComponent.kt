package com.haru2036.sleepchart.di.component

import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.infra.api.client.SleepClient
import com.haru2036.sleepchart.infra.api.service.SleepService
import com.haru2036.sleepchart.infra.repository.SleepRepository
import com.haru2036.sleepchart.presentation.activity.MainActivity
import dagger.Subcomponent

/**
 * Created by haru2036 on 2016/11/28.
 */

@Subcomponent(modules = arrayOf(SleepModule::class))
interface SleepComponent{

    fun inject(service: SleepService)

    fun inject(sleepUseCase: SleepUseCase)

    fun inject(sleepRepository: SleepRepository)

    fun inject(sleepClient: SleepClient)


}
