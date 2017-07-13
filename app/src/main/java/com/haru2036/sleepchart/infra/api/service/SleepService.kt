package com.haru2036.sleepchart.infra.api.service

import com.haru2036.sleepchart.infra.api.response.SleepResponse
import retrofit2.http.GET
import rx.Observable

/**
 * Created by haru2036 on 16/11/08.
 */
interface SleepService {
    @GET("/sleepSessions")
    fun sleeps(): Observable<List<SleepResponse>>
}
