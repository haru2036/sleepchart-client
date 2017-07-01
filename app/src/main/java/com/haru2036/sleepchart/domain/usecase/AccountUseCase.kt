package com.haru2036.sleepchart.domain.usecase

import com.haru2036.sleepchart.infra.repository.AccountRepository
import javax.inject.Inject

class AccountUseCase @Inject constructor(private val repository: AccountRepository){
}
