package com.haru2036.sleepchart.infra.api.service

import io.reactivex.Observable
import retrofit2.http.GET

interface AccountService {
    @GET("register")
    fun register(): Observable<String>

}
