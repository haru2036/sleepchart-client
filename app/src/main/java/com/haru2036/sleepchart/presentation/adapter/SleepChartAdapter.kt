package com.haru2036.sleepchart.presentation.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.presentation.TimeChartView
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZoneId


class SleepChartAdapter(val context: Context) : RecyclerView.Adapter<SleepChartAdapter.ViewHolder>() {
    private val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }
    private val nightOffset = 11L
    var onItemClickListener: TimeChartView.OnItemClickListener? = null
    var items: List<Sleep> = emptyList()
    set(value){
        sleepsOfDays.clear()
        field = value
        value.forEach {
            val day = DateTimeUtils.toInstant(it.start).atZone(ZoneId.systemDefault()).minusHours(nightOffset).dayOfYear
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
            ViewHolder(inflater.inflate(R.layout.item_sleepchart_day, parent, false) as TimeChartView)

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val view = holder?.itemView as TimeChartView
        view.onItemClickListener = onItemClickListener
        view.clearAllSleeps()
        view.sleeps = sleepsOfDays.toList().get(position).second

    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    class ViewHolder constructor(row: TimeChartView): RecyclerView.ViewHolder(row){

    }
}

