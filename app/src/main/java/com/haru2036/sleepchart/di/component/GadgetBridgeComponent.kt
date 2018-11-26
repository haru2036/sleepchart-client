package com.haru2036.sleepchart.di.component

import com.haru2036.sleepchart.di.module.GadgetBridgeModule
import com.haru2036.sleepchart.domain.usecase.GadgetBridgeUseCase
import com.haru2036.sleepchart.infra.dao.GadgetBridgeDao
import com.haru2036.sleepchart.infra.repository.GadgetBridgeRepository
import com.haru2036.sleepchart.presentation.activity.ImportActivity
import dagger.Subcomponent

@Subcomponent(modules = arrayOf(GadgetBridgeModule::class))
interface GadgetBridgeComponent {

    fun inject(gadgetBridgeUseCase: GadgetBridgeUseCase)

    fun inject(gadgetBridgeRepository: GadgetBridgeRepository)

    fun inject(gadgetBridgeDao: GadgetBridgeDao)

    fun inject(activity: ImportActivity)

}
