package com.haru2036.sleepchart.domain.usecase

import com.haru2036.sleepchart.infra.repository.AccountRepository
import javax.inject.Inject

class AccountUsecase @Inject constructor(private val repository: AccountRepository) {
    fun register() = repository.register()
}
