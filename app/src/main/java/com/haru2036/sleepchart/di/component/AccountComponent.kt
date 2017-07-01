package com.haru2036.sleepchart.di.component

import com.haru2036.sleepchart.di.module.AccountModule
import com.haru2036.sleepchart.domain.usecase.AccountUseCase
import com.haru2036.sleepchart.infra.api.client.AccountClient
import com.haru2036.sleepchart.infra.api.service.AccountService
import com.haru2036.sleepchart.infra.repository.AccountRepository
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Subcomponent(modules = arrayOf(AccountModule::class))
interface AccountComponent {

    fun inject(service: AccountService)

    fun inject(accountUseCase: AccountUseCase)

    fun inject(accountRepository: AccountRepository)

    fun inject(accountClient: AccountClient)


}
