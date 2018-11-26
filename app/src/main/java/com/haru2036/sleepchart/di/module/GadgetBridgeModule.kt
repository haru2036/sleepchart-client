package com.haru2036.sleepchart.di.module

import com.haru2036.sleepchart.domain.usecase.GadgetBridgeUseCase
import com.haru2036.sleepchart.infra.dao.GadgetBridgeDao
import com.haru2036.sleepchart.infra.repository.GadgetBridgeRepository
import dagger.Module
import dagger.Provides

@Module
class GadgetBridgeModule {
    @Provides
    fun provideGadgetBridgeDao() = GadgetBridgeDao()

    @Provides
    fun provideGadgetBridgeRepository(gadgetBridgeDao: GadgetBridgeDao) = GadgetBridgeRepository(gadgetBridgeDao)

    @Provides
    fun provideGadgetBridgeUseCase(gadgetBridgeRepository: GadgetBridgeRepository) = GadgetBridgeUseCase(gadgetBridgeRepository)

}


