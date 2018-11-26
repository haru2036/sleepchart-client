package com.haru2036.sleepchart.domain.usecase

import com.haru2036.sleepchart.infra.repository.GadgetBridgeRepository
import javax.inject.Inject


class GadgetBridgeUseCase @Inject constructor(private val repository: GadgetBridgeRepository) {
    fun syncActivity() = repository.syncActivity()
}
