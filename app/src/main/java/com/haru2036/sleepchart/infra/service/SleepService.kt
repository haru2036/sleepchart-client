package com.haru2036.sleepchart.infra.service

import com.haru2036.sleepchart.infra.service.Response.SleepResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by haru2036 on 16/11/08.
 */
interface SleepService {
    @GET("/sleeps")
    fun sleeps(@Query("city") city: String): Call<SleepResponse>
}
