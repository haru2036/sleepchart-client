package com.haru2036.sleepchart.presentation.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SleepDetailViewModel @Inject constructor(private val usecase: SleepUseCase) : ViewModel() {
    var sleep: Sleep? = null

    private val disposables = CompositeDisposable()

    var formattedSleepStart = ObservableField<String>()
    var formattedSleepEnd = ObservableField<String>()
    var sleepRating = BehaviorSubject.create<Float>()

    fun loadSleep(id: Long) {
        disposables.add(usecase.getSleepById(id)
                .singleOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //todo: フォーマットは暫定対応
                    sleep = it
                    formattedSleepStart.set(SimpleDateFormat("MM/dd\nHH:mm", Locale.getDefault()).format(it.start))
                    formattedSleepEnd.set(SimpleDateFormat("MM/dd\nHH:mm", Locale.getDefault()).format(it.end))
                    it.rating?.let { sleepRating.onNext(it.toFloat()) }
                },{
                    Timber.e(it)
                })
        )

    }

    fun saveSleep() {
        sleep?.let {
            it.rating = sleepRating.value?.toInt()
            disposables.add(
                    usecase.updateSleep(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
            )
        }
    }
    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}