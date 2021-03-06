package com.haru2036.sleepchart.infra.repository

import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.domain.entity.SleepSession
import com.haru2036.sleepchart.infra.api.client.SleepClient
import com.haru2036.sleepchart.infra.dao.SleepDao
import com.haru2036.sleepchart.infra.dao.SleepSessionDao
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class SleepRepository @Inject constructor(private val client: SleepClient,
                                               private val sleepDao: SleepDao,
                                               private val sleepSessionDao: SleepSessionDao){

    fun fetchSleepsWithRange(start: Date, count: Int): Single<List<Sleep>> {
        return client.fetchSleepsWithRange(start, count)
                .flatMap { sleeps ->
                    sleepDao.create(sleeps).map { sleeps }
                }.map { it.sortedBy { it.start } }
                .firstOrError()
    }

    fun getSleepById(id: Long) = sleepDao.getSleepById(id)

    fun findSleeps() = sleepDao.sleeps()

    fun getOldestSleep() = sleepDao.getOldestSleep()

    fun findSleepSession() = sleepSessionDao.sleepSessions()

    fun createSleeps(sleeps: List<Sleep>) =
            client.postSleeps(sleeps).flatMap { sleepDao.create(sleeps) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())

    fun updateSleep(sleep: Sleep): Observable<Sleep> = client.putSleep(sleep)
            .map { sleepDao.update(it.apply { id = sleep.id }) }

    fun deleteSleep(id: Long) = sleepDao.deleteById(id)

    fun createSleepSession(sleepSession: SleepSession) = sleepSessionDao.create(sleepSession)

    fun deleteSleepSession(sleepSession: SleepSession) = sleepSessionDao.delete(sleepSession)

}
