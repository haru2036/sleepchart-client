package com.haru2036.sleepchart.domain.usecase

import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.domain.entity.SleepSession
import com.haru2036.sleepchart.infra.repository.SleepRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

/**
 * Created by haru2036 on 2016/11/28.
 */
class SleepUseCase @Inject constructor(private val repository: SleepRepository) {
    fun fetchSleeps() = repository.fetchSleeps()

    fun findSleeps() = repository.findSleeps()

    fun isSleeping() = repository.findSleepSession().isEmpty.map { !it }

    fun logSleepToggle(date: Date): Single<Boolean> {
        return repository.findSleepSession().isEmpty.map { !it }.doOnSuccess{ isSleeping->
            if (!isSleeping) {
                repository.createSleepSession(SleepSession(0, date))
            } else {
                repository.findSleepSession()
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .flatMapCompletable { sleepSession ->
                            repository.deleteSleepSession(sleepSession)
                            repository.createSleep(Sleep(0, sleepSession.start, date)).andThen {
                            }
                        }.subscribe({

                })

            }
        }
    }

    fun saveSleep(sleep: Sleep) = repository.createSleep(sleep)
}
