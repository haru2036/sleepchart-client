package com.haru2036.sleepchart.di.module

import com.haru2036.sleepchart.domain.usecase.AccountUseCase
import com.haru2036.sleepchart.infra.api.client.AccountClient
import com.haru2036.sleepchart.infra.api.service.AccountService
import com.haru2036.sleepchart.infra.repository.AccountRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AccountModule(private val retrofit: Retrofit){
    @Provides
    fun provideAccountUseCase(accountRepository: AccountRepository) = AccountUseCase(accountRepository)

    @Provides
    fun provideAccountRepository(accountClient: AccountClient) = AccountRepository(accountClient)

    @Provides
    fun provideAccountClient() = AccountClient(retrofit.create(AccountService::class.java))

}
