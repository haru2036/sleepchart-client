package com.haru2036.sleepchart.infra.api.service

import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.infra.api.response.SleepResponse
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by haru2036 on 16/11/08.
 */
interface SleepService {
    @GET("sleeps")
    fun sleeps(): Observable<List<SleepResponse>>

    @GET("sleeps/range")
    fun fetchSleepsWithRange(@Query("start") start: String,
                             @Query("count") count: Int): Observable<List<SleepResponse>>

    @POST("sleeps")
    fun postSleeps(@Body sleeps: List<Sleep>): Observable<List<SleepResponse>>

    @POST("sleeps/{id}")
    fun postSleepWithId(@Path("id") id: Long, @Body sleep: Sleep): Observable<SleepResponse>
}
