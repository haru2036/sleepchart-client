package com.haru2036.sleepchart.infra.repository

import com.haru2036.sleepchart.infra.api.client.AccountClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AccountRepository @Inject constructor(private val client: AccountClient) {
    open fun register() = client.register()
}
