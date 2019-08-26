package com.haru2036.sleepchart.infra.repository

import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.domain.entity.SleepSession
import com.haru2036.sleepchart.infra.api.client.SleepClient
import com.haru2036.sleepchart.infra.dao.SleepDao
import com.haru2036.sleepchart.infra.dao.SleepSessionDao
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class SleepRepository @Inject constructor(private val client: SleepClient,
                                               private val sleepDao: SleepDao,
                                               private val sleepSessionDao: SleepSessionDao){
    open fun fetchSleeps() : Single<List<Sleep>>{
        return client.sleeps()
                .flatMap { sleepDao.create(it) }
                .flatMap { findSleeps() }
                .toList()
    }

    fun fetchSleepsWithRange(start: Date, count: Int): Single<List<Sleep>> {
        return client.fetchSleepsWithRange(start, count)
                .flatMap {
                    sleepDao.create(it)
                    Observable.just(it)
                }.map { it.sortedBy { it.start } }
                .firstOrError()
    }

    fun putSleeps(sleeps: List<Sleep>) = client.putSleeps(sleeps)

    fun findSleeps() = sleepDao.sleeps()

    fun findSleepsWithRange(start: Date, count: Int) = sleepDao.findSleepsWithRange(start, count)

    fun getOldestSleep() = sleepDao.getOldestSleep()

    fun findSleepSession() = sleepSessionDao.sleepSessions()

    fun createSleeps(sleeps: List<Sleep>) =
            client.putSleeps(sleeps).flatMap { sleepDao.create(sleeps) }


    fun deleteSleep(id: Long) = sleepDao.deleteById(id)

    fun createSleepSession(sleepSession: SleepSession) = sleepSessionDao.create(sleepSession)

    fun deleteSleepSession(sleepSession: SleepSession) = sleepSessionDao.delete(sleepSession)

}
