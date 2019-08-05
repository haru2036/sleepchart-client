package com.haru2036.sleepchart.domain.usecase

import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.domain.entity.SleepSession
import com.haru2036.sleepchart.infra.repository.PagingRepository
import com.haru2036.sleepchart.infra.repository.SleepRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

/**
 * Created by haru2036 on 2016/11/28.
 */
class SleepUseCase @Inject constructor(private val repository: SleepRepository,
                                       private val pagingRepository: PagingRepository) {
    fun fetchSleeps() = repository.fetchSleeps()

    fun fetchNextSleeps(): Single<List<Sleep>> {
        return repository.getOldestSleep()
                .flatMap {
                    val oneWeekAgo = Calendar.getInstance().apply {
                        time = it.start
                        add(Calendar.WEEK_OF_YEAR, 7)
                    }.time
                    repository.fetchSleepsWithRange(oneWeekAgo, it.start)
                }
    }

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
                            repository.createSleeps(listOf(Sleep(0, sleepSession.start, date)))
                        }.map { isSleeping }
            }
        }
    }

    fun trackSleepTwice(): Observable<Long> = repository.findSleeps()
                .toList()
                .map { it.last().end }
            .flatMapObservable { repository.createSleeps(listOf(Sleep(0, it, Calendar.getInstance().time))) }

    fun deleteSleep(id: Long) = repository.deleteSleep(id)
}
