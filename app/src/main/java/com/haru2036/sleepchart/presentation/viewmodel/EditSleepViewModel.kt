package com.haru2036.sleepchart.presentation.viewmodel

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import javax.inject.Inject

class EditSleepViewModel @Inject constructor(private val sleepUseCase: SleepUseCase){

    lateinit var sleep: Sleep

    lateinit var context: Context


    fun setStart(){
        pickDate(sleep.start)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    sleep.start = it
                }, {

                })
    }

    fun setEnd(){
        pickDate(sleep.end)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    sleep.end = it
                }, {

                })
    }

    fun pickDate(date: Date): Single<Date>{
        //Pick date then pick time
        return Single.create<Date> { emitter ->
            DatePickerDialog(context, DatePickerDialog.OnDateSetListener { datePicker, y, moy, d ->
                TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { timePicker, h, moh ->
                    val newDate = Calendar.getInstance().apply {
                        set(Calendar.YEAR, y)
                        set(Calendar.MONTH, moy)
                        set(Calendar.DAY_OF_MONTH, d)
                        set(Calendar.HOUR_OF_DAY, h)
                        set(Calendar.MINUTE, moh)
                    }
                    emitter.onSuccess(newDate.time)
                }, date.hours, date.minutes, true)
            }, date.year, date.month, date.day)
        }
    }
}