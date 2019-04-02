package com.haru2036.sleepchart.presentation.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.presentation.TimeChartView
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZoneId
import java.util.*


class SleepChartAdapter(val context: Context) : RecyclerView.Adapter<SleepChartAdapter.ViewHolder>() {
    private val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }
    private val nightOffset = 4L //前日の20~23時を当日扱いにするためのオフセット
    var items: List<Sleep> = emptyList()
    set(value){
        sleepsOfDays.clear()
        field = value
        value.forEach {
            val day = DateTimeUtils.toInstant(it.start).atZone(ZoneId.systemDefault()).plusHours(nightOffset).dayOfYear
            if(!sleepsOfDays.containsKey(day)){
                sleepsOfDays.put(day, mutableListOf(it))
            }else{
                val sleeps = sleepsOfDays.get(day)
                sleeps !!.add(it)
                sleepsOfDays.put(day, sleeps)
            }
        }
    }

    private var sleepsOfDays: MutableMap<Int, MutableList<Sleep>> = mutableMapOf()

    override fun getItemCount(): Int = sleepsOfDays.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(inflater.inflate(R.layout.item_sleepchart_day, parent, false) as FrameLayout)

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val view = holder?.itemView as FrameLayout
        val timeChartView = view.findViewById<TimeChartView>(R.id.view_time_chart)
        timeChartView.clearAllSleeps()
        timeChartView.sleeps = sleepsOfDays.toList()[position].second
        Calendar.getInstance().apply {
            time = timeChartView.sleeps.first().end

            val backgroundColor = context.getColor(
                when (get(Calendar.DAY_OF_WEEK)){
                    Calendar.SUNDAY -> R.color.sundayBackground
                    Calendar.SATURDAY -> R.color.saturdayBackground
                    else -> R.color.normalBackground
                }
            )
            view.setBackgroundColor(backgroundColor)
            view.invalidate()
        }
    }


    class ViewHolder constructor(row: FrameLayout) : RecyclerView.ViewHolder(row) {

    }
}

