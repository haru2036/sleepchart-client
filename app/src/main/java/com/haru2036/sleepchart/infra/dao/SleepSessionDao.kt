package com.haru2036.sleepchart.infra.dao

import com.haru2036.sleepchart.di.OrmaHandler
import com.haru2036.sleepchart.domain.entity.OrmaDatabase
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.domain.entity.SleepSession
import javax.inject.Inject

class SleepSessionDao @Inject constructor(private val orma: OrmaHandler){

    fun create(sleepSession: SleepSession){
        orma.db.insertIntoSleepSession(sleepSession)
    }

    fun delete(sleepSession: SleepSession){
        orma.db.deleteFromSleepSession()
                .idEq(sleepSession.id)
                .execute()
    }

    fun sleepSessions() = orma.db.selectFromSleepSession().executeAsObservable()

}
