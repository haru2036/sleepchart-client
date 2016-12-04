package com.haru2036.sleepchart.infra.api.service

import com.haru2036.sleepchart.infra.api.SleepResponse
import retrofit2.http.GET
import rx.Observable

/**
 * Created by haru2036 on 16/11/08.
 */
interface SleepService {
    @GET("/sleeps")
    fun sleeps(): Observable<SleepResponse>
}
