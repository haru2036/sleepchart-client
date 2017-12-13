package com.haru2036.sleepchart.infra.dao

import com.haru2036.sleepchart.di.OrmaHandler
import com.haru2036.sleepchart.domain.entity.Sleep
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by haru2036 on 2017/07/10.
 */

class SleepDao @Inject constructor(private val orma: OrmaHandler){
    fun deleteById(id: Long) = orma.db.deleteFromSleep().idEq(id).executeAsSingle()

    fun create(sleep: Sleep): Single<Long> = orma.db.prepareInsertIntoSleepAsSingle().map { it.execute(sleep) }

    fun sleeps() = orma.db.selectFromSleep().executeAsObservable()

}
