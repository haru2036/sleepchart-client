package com.haru2036.sleepchart.domain.usecase

import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.domain.entity.SleepSession
import com.haru2036.sleepchart.infra.repository.SleepRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

/**
 * Created by haru2036 on 2016/11/28.
 */
class SleepUseCase @Inject constructor(private val repository: SleepRepository) {

    fun fetchOlderSleeps(): Single<List<Sleep>> {
        return repository.getOldestSleep()
                .flatMap {
                    repository.fetchSleepsWithRange(it.start, 20)
                }
    }

    fun restoreLatestSleeps(): Single<List<Sleep>> {
        return repository.fetchSleepsWithRange(Calendar.getInstance().time, 40)
    }

    fun getSleepById(id: Long) = repository.getSleepById(id)

    fun createSleeps(sleeps: List<Sleep>) = repository.createSleeps(sleeps)

    fun findSleeps() = repository.findSleeps()

    fun isSleeping() = repository.findSleepSession().isEmpty.map { !it }

    fun logSleepToggle(date: Date): Observable<Boolean> {
        return repository.findSleepSession().isEmpty.map { !it }.flatMapObservable{ isSleeping ->
            if (!isSleeping) {
                repository.createSleepSession(SleepSession(0, date))
                        .toObservable()
                        .map { isSleeping }
            } else {
                repository.findSleepSession()
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .flatMap { sleepSession ->
                            repository.deleteSleepSession(sleepSession)
                            repository.createSleeps(listOf(Sleep(id = 0, start = sleepSession.start, end = date)))
                        }.map { isSleeping }
            }
        }
    }

    fun trackSleepTwice(): Observable<Long> = repository.findSleeps()
                .toList()
                .map { it.last().end }
            .flatMapObservable { repository.createSleeps(listOf(Sleep(id = 0, start = it, end = Calendar.getInstance().time))) }

    fun deleteSleep(id: Long) = repository.deleteSleep(id)
}
