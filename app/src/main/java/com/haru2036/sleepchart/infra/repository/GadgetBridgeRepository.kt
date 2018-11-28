package com.haru2036.sleepchart.infra.repository

import com.haru2036.sleepchart.infra.dao.GadgetBridgeDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class GadgetBridgeRepository @Inject constructor(private val gadgetBridgeDao: GadgetBridgeDao) {
    fun syncActivity() = gadgetBridgeDao.getActivitySamples()
}