package com.haru2036.sleepchart.infra.dao

import com.haru2036.sleepchart.di.OrmaHandler
import com.haru2036.sleepchart.domain.entity.Sleep
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by haru2036 on 2017/07/10.
 */

class SleepDao @Inject constructor(private val orma: OrmaHandler){
    fun deleteById(id: Long) = orma.db.deleteFromSleep().idEq(id).executeAsSingle()

    fun create(sleeps: List<Sleep>): Observable<Long> = orma.db.prepareInsertIntoSleepAsSingle().flatMapObservable { it.executeAllAsObservable(sleeps) }

    fun sleeps() = orma.db.selectFromSleep().orderByStartAsc().executeAsObservable()

    fun getOldestSleep() = orma.db.selectFromSleep().orderByStartAsc().executeAsObservable().firstOrError()

    fun getSleepById(id: Long) = orma.db.selectFromSleep().idEq(id).executeAsObservable()

    fun update(sleep: Sleep)= orma.db.relationOfSleep().upsert(sleep)

}
