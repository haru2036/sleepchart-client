package com.haru2036.sleepchart.infra.repository

import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.domain.entity.SleepSession
import com.haru2036.sleepchart.infra.api.client.SleepClient
import com.haru2036.sleepchart.infra.dao.SleepDao
import com.haru2036.sleepchart.infra.dao.SleepSessionDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class SleepRepository @Inject constructor(private val client: SleepClient,
                                               private val sleepDao: SleepDao,
                                               private val sleepSessionDao: SleepSessionDao){
    open fun fetchSleeps() = client.sleeps()

    fun putSleeps(sleeps: List<Sleep>) = client.putSleeps(sleeps)

    fun findSleeps() = sleepDao.sleeps()

    fun findSleepSession() = sleepSessionDao.sleepSessions()

    fun createSleeps(sleeps: List<Sleep>) =
            client.putSleeps(sleeps).flatMap { sleepDao.create(sleeps) }


    fun deleteSleep(id: Long) = sleepDao.deleteById(id)

    fun createSleepSession(sleepSession: SleepSession) = sleepSessionDao.create(sleepSession)

    fun deleteSleepSession(sleepSession: SleepSession) = sleepSessionDao.delete(sleepSession)

}
