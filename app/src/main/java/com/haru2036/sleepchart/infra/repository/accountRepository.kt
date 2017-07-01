package com.haru2036.sleepchart.infra.repository

import com.haru2036.sleepchart.infra.api.client.AccountClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by haru2036 on 2017/06/28.
 */
@Singleton
open class AccountRepository @Inject constructor(private val client: AccountClient) {
}
