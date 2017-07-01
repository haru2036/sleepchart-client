package com.haru2036.sleepchart.infra.api.client

import com.haru2036.sleepchart.infra.api.service.AccountService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AccountClient @Inject constructor(private val service: AccountService){
}
