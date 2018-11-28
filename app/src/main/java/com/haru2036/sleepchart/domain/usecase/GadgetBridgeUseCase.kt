package com.haru2036.sleepchart.domain.usecase

import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.infra.repository.GadgetBridgeRepository
import com.haru2036.sleepchart.infra.repository.SleepRepository
import java.util.*
import javax.inject.Inject


class GadgetBridgeUseCase @Inject constructor(private val gadgetBridgeRepository: GadgetBridgeRepository) {
    fun syncActivity() = gadgetBridgeRepository.syncActivity().map {
        (1..(it.size - 1))
                .map { index -> Pair(it[index - 1], it[index]) }
                .map { currentItems ->
                    if (currentItems.first.activityType == 112 && currentItems.second.activityType != 112) {
                        return@map SleepEvent(Date(currentItems.first.time), SleepEventType.START)
                    } else if (currentItems.first.activityType != 112 && currentItems.second.activityType == 112) {
                        return@map SleepEvent(Date(currentItems.second.time), SleepEventType.END)
                    } else {
                        return@map null
                    }
                }.filterNotNull()
                .fold(Pair(null as SleepEvent?, mutableListOf<Sleep>())) { (previous: SleepEvent?, sleeps: List<Sleep>), sleepEvent ->
                    //startとendのイベントをくっつけてSleepにする
                    when (sleepEvent.kind) {
                        SleepEventType.START -> Pair(sleepEvent, sleeps)
                        SleepEventType.END -> previous?.let {
                            sleeps.add(Sleep(0, previous.time, sleepEvent.time))
                            Pair(null, sleeps)
                        } ?: Pair(previous, sleeps)
                    }
                }.second
    }
}

data class SleepEvent(val time: Date, val kind: SleepEventType)
enum class SleepEventType {
    START,
    END
}