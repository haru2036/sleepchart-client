package com.haru2036.sleepchart.domain.usecase

import android.util.Log
import com.haru2036.sleepchart.domain.entity.GadgetBridgeActivitySample
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.infra.repository.GadgetBridgeRepository
import com.haru2036.sleepchart.infra.repository.SleepRepository
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject


class GadgetBridgeUseCase @Inject constructor(private val gadgetBridgeRepository: GadgetBridgeRepository,
                                              private val sleepRepository: SleepRepository) {
    //    private val sleepActivityStartType = listOf(112, 122, 121, 123, 26, 25, 80, 89, 90)
    private val sleepActivityStartType = listOf(112, 122, 121, 123)

    fun syncActivity() = gadgetBridgeRepository.syncActivity().map { list ->
        val smoothedActivitySamples = (30..(list.size - 1)).step(30)
                .map { list.slice(it - 29..it) }
                .map { sublist: List<GadgetBridgeActivitySample> ->
                    Log.d("sublist", sublist.toString())
                    sublist.groupingBy { it.activityType }
                            .eachCount()
                            .maxBy { it.value }
                            ?.let {
                                return@let GadgetBridgeActivitySample(sublist.first().time, it.key)
                            }
                }.filterNotNull()

        (1..(smoothedActivitySamples.size - 1))
                .map { index -> Pair(smoothedActivitySamples[index - 1], smoothedActivitySamples[index]) }
                .map { currentItems ->
                    if (!sleepActivityStartType.contains(currentItems.first.activityType) && sleepActivityStartType.contains(currentItems.second.activityType)) {
                        return@map SleepEvent(currentItems.first.time, SleepEventType.START)
                    } else if (sleepActivityStartType.contains(currentItems.first.activityType) && !sleepActivityStartType.contains(currentItems.second.activityType)) {
                        return@map SleepEvent(currentItems.second.time, SleepEventType.END)
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
    }.flatMapObservable { Observable.fromIterable(it).concatMap { sleep -> sleepRepository.createSleep(sleep).toObservable() } }
}

data class SleepEvent(val time: Date, val kind: SleepEventType)
enum class SleepEventType {
    START,
    END
}
