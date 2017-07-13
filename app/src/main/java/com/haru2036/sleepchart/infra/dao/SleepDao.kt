package com.haru2036.sleepchart.infra.dao

import com.haru2036.sleepchart.di.OrmaHandler
import com.haru2036.sleepchart.domain.entity.OrmaDatabase
import com.haru2036.sleepchart.domain.entity.Sleep
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by haru2036 on 2017/07/10.
 */

class SleepDao @Inject constructor(private val orma: OrmaHandler){

    fun create(sleep: Sleep): Completable {
        return Completable.create { orma.db.insertIntoSleep(sleep) }
    }

    fun sleeps() = orma.db.selectFromSleep().executeAsObservable()

}
