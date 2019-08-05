package com.haru2036.sleepchart.infra.api.client

import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.infra.api.converter.SleepConverter
import com.haru2036.sleepchart.infra.api.service.SleepService
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by haru2036 on 2016/11/28.
 */
@Singleton
open class SleepClient @Inject constructor(private val service: SleepService){
    open fun sleeps() = service.sleeps().map { it.map { SleepConverter.convert(it) } }

    open fun fetchSleepsWithRange(start: Date, end: Date) = service.fetchSleepsWithRange(start, end)
            .map { it.map { SleepConverter.convert(it) } }

    open fun putSleeps(sleeps: List<Sleep>) = service.postSleeps(sleeps)
            .map { it.map { SleepConverter.convert(it) } }
}
