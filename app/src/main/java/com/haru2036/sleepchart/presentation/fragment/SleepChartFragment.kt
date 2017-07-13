package com.haru2036.sleepchart.presentation.fragment

import android.app.Fragment
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.presentation.adapter.SleepChartAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SleepChartFragment : Fragment(){
    val chartRecyclerView: RecyclerView by lazy { view.findViewById(R.id.fragment_sleepchart_recyclerview) as RecyclerView }
    val fab: FloatingActionButton by lazy { view.findViewById(R.id.fab) as FloatingActionButton}

    @Inject
    lateinit var sleepUsecase: SleepUseCase

    companion object {
        @JvmStatic
        fun newInstance() = SleepChartFragment()

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        SleepChart.getAppComponent().plus(SleepModule()).inject(this)
        return inflater !!.inflate(R.layout.fragment_sleepchart, container, false)
    }

    override fun onStart() {
        super.onStart()
        fab.setOnClickListener { toggleSleep() }
        sleepUsecase.isSleeping()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe ({
                    setStateColors(it)
                },
                {

                })
        chartRecyclerView.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        chartRecyclerView.adapter = SleepChartAdapter(context)
        showSleeps()

    }

    override fun onResume() {
        super.onResume()
    }
    fun showSleeps(){
        sleepUsecase.findSleeps().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { sleeps ->
                    val adapter = chartRecyclerView.adapter as SleepChartAdapter
                    adapter.items = sleeps
                    adapter.notifyDataSetChanged()
                }
    }

    fun toggleSleep(){
        sleepUsecase.logSleepToggle(Calendar.getInstance().time)
                .delay(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    setStateColors(!it)
                    if(it){
                        Toast.makeText(activity, "Good morning", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(activity, "Good night", Toast.LENGTH_LONG).show()
                    }
                    showSleeps()
                },
                {
                    Timber.e(it)
                })

    }

    fun setStateColors(isSleeping: Boolean){
        if(isSleeping){
            activity.theme.applyStyle(R.style.AppTheme, true)
            fab.backgroundTintList = ColorStateList.valueOf(activity.getColor(R.color.colorAccent))
            fab.setImageDrawable(activity.getDrawable(R.drawable.ic_wb_sunny_white_24dp))
        }else{
            activity.theme.applyStyle(R.style.AppWakeTheme, true)
            activity.setTheme(R.style.AppWakeTheme)
            fab.backgroundTintList = ColorStateList.valueOf(activity.getColor(R.color.colorWakeAccent))
            fab.setImageDrawable(activity.getDrawable(R.drawable.ic_local_hotel_white_24dp))
        }
    }


}