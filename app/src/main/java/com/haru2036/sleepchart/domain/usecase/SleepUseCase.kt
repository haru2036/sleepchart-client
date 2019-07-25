package com.haru2036.sleepchart.domain.usecase

import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.domain.entity.SleepSession
import com.haru2036.sleepchart.infra.api.response.SleepResponse
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
    fun syncSleepsWithServer(): Observable<List<Sleep>> {
        return repository.findSleeps()
                .toList()
                .flatMapObservable { repository.putSleeps(it) }
    }

    fun fetchSleeps() = repository.fetchSleeps()

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
                        .flatMapSingle { sleepSession ->
                            repository.deleteSleepSession(sleepSession)
                            repository.createSleep(Sleep(0, sleepSession.start, date))
                        }.map { isSleeping }
            }
        }
    }

    fun trackSleepTwice(): Single<Long> = repository.findSleeps()
                .toList()
                .map { it.last().end }
                .flatMap { repository.createSleep(Sleep(0, it, Calendar.getInstance().time)) }

    fun deleteSleep(id: Long) = repository.deleteSleep(id)
}
