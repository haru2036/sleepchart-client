package com.haru2036.sleepchart.domain.usecase

import com.haru2036.sleepchart.infra.repository.SleepRepository
import javax.inject.Inject

/**
 * Created by haru2036 on 2016/11/28.
 */
class SleepUseCase @Inject constructor(private val repository: SleepRepository){
    fun fetchSleeps() = repository.fetchSleeps()
}
