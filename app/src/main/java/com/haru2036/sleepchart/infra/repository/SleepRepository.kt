package com.haru2036.sleepchart.infra.repository

import com.haru2036.sleepchart.infra.api.client.SleepClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by haru2036 on 2016/11/28.
 */
@Singleton
open class SleepRepository @Inject constructor(private val client: SleepClient){
    open fun fetchSleeps() = client.sleeps()
}
