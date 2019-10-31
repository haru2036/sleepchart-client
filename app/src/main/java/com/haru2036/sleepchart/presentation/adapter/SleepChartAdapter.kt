package com.haru2036.sleepchart.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.presentation.TimeChartView
import com.haru2036.sleepchart.presentation.activity.SleepDetailActivity
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZoneId
import java.util.*


class SleepChartAdapter(val context: Context) : RecyclerView.Adapter<SleepChartAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(inflater.inflate(R.layout.item_sleepchart_day, parent, false) as TimeChartView)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder?.itemView as TimeChartView
        view.clearAllSleeps()
        view.onSleepClickListener = {sleep: Sleep -> SleepDetailActivity.start(context, sleep)}
        view.sleeps = sleepsOfDays.toList()[position].second
        Calendar.getInstance().apply {
            time = view.sleeps.first().end

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

    private val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }
    private val nightOffset = 4L //前日の20~23時を当日扱いにするためのオフセット
    var items: List<Sleep> = emptyList()
    set(value){
        sleepsOfDays.clear()
        field = value
        sleepsOfDays = aggregateSleepOfDays(value)
    }

    //日毎にまとめる
    private fun aggregateSleepOfDays(sleeps: List<Sleep>): MutableMap<Int, MutableList<Sleep>> {
        val sleepsPerDays = mutableMapOf<Int, MutableList<Sleep>>()
        sleeps.forEach {
            val day = DateTimeUtils.toInstant(it.start).atZone(ZoneId.systemDefault()).plusHours(nightOffset).dayOfYear
            if (!sleepsPerDays.containsKey(day)) {
                sleepsPerDays[day] = mutableListOf(it)
            } else {
                val sleeps = sleepsPerDays.get(day)
                sleeps!!.add(it)
                sleepsPerDays[day] = sleeps
            }

        }
        return sleepsPerDays
    }

    fun sleepsToRowsCount(sleeps: List<Sleep>) = aggregateSleepOfDays(sleeps).size

    fun addOlderSleeps(sleeps: List<Sleep>) {
        items = sleeps + items
    }

    fun addNewerSleeps(sleeps: List<Sleep>) {
        items = items + sleeps
    }

    private var sleepsOfDays: MutableMap<Int, MutableList<Sleep>> = mutableMapOf()

    override fun getItemCount(): Int = sleepsOfDays.size

    class ViewHolder constructor(row: TimeChartView): RecyclerView.ViewHolder(row){

    }
}

