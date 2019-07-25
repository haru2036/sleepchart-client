package com.haru2036.sleepchart.domain.usecase

import com.haru2036.sleepchart.domain.entity.GadgetBridgeActivitySample
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.infra.repository.SharedPreferencesRepository
import com.haru2036.sleepchart.infra.repository.GadgetBridgeRepository
import com.haru2036.sleepchart.infra.repository.SleepRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import java.util.*
import javax.inject.Inject

class GadgetBridgeUseCase @Inject constructor(private val gadgetBridgeRepository: GadgetBridgeRepository,
                                              private val sleepRepository: SleepRepository,
                                              private val sharedPreferencesRepository: SharedPreferencesRepository) {
    //    private val sleepActivityStartType = listOf(112, 122, 121, 123, 26, 25, 80, 89, 90)
    private val sleepActivityStartType = listOf(112, 122, 121, 123)

    private fun convertActivitySamplesToSleeps(samples: List<GadgetBridgeActivitySample>): MutableList<Sleep> {
        val smoothedActivitySamples = (30..(samples.size - 1)).step(30)
                .map { samples.slice(it - 29..it) }
                .map { sublist: List<GadgetBridgeActivitySample> ->
                    sublist.groupingBy { it.activityType }
                            .eachCount()
                            .maxBy { it.value }
                            ?.let {
                                return@let GadgetBridgeActivitySample(sublist.first().time, it.key)
                            }
                }.filterNotNull()

        return (1..(smoothedActivitySamples.size - 1))
                .map { index -> Pair(smoothedActivitySamples[index - 1], smoothedActivitySamples[index]) }.mapNotNull { currentItems ->
                    if (!sleepActivityStartType.contains(currentItems.first.activityType) && sleepActivityStartType.contains(currentItems.second.activityType)) {
                        SleepEvent(currentItems.first.time, SleepEventType.START)
                    } else if (sleepActivityStartType.contains(currentItems.first.activityType) && !sleepActivityStartType.contains(currentItems.second.activityType)) {
                        SleepEvent(currentItems.second.time, SleepEventType.END)
                    } else {
                        null
                    }
                }
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

    fun syncActivity() = Single.zip(sleepRepository.findSleeps().last(Sleep(0, Date(0), Date(0))), sharedPreferencesRepository.getGadgetBridgePath(), BiFunction<Sleep, String, Pair<Sleep, String>> { sleep, path -> Pair(sleep, path) })
            .flatMap { gadgetBridgeRepository.syncActivity(it.first.end, it.second) }
            .map { convertActivitySamplesToSleeps(it) }
            .flatMapObservable { Observable.fromIterable(it).concatMap { sleep -> sleepRepository.createSleep(sleep).toObservable() } }!!
}

data class SleepEvent(val time: Date, val kind: SleepEventType)
enum class SleepEventType {
    START,
    END
}
