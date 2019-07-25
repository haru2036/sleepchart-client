package com.haru2036.sleepchart.infra.api.service

import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.infra.api.response.SleepResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by haru2036 on 16/11/08.
 */
interface SleepService {
    @GET("sleeps")
    fun sleeps(): Observable<List<SleepResponse>>

    @POST("sleeps")
    fun postSleeps(@Body sleeps: List<Sleep>): Observable<List<SleepResponse>>
}
