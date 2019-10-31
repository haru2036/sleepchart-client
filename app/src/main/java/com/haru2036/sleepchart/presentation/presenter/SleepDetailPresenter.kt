package com.haru2036.sleepchart.presentation.presenter

import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import javax.inject.Inject

class SleepDetailPresenter @Inject constructor(private val usecase: SleepUseCase) {
    fun loadSleep(id: Long) = usecase.getSleepById(id)
}