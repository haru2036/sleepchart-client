package com.haru2036.sleepchart.presentation.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.presentation.TimeChartView
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by haru2036 on 2017/07/05.
 */

class SleepChartAdapter(val context: Context) : RecyclerView.Adapter<SleepChartAdapter.ViewHolder>() {
    val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }
    var items: List<Sleep> = emptyList()
    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(inflater.inflate(R.layout.item_sleepchart_day, parent, false) as TimeChartView)

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val view = holder?.itemView as TimeChartView
        view.clearAllSleeps()
        view.sleeps = listOf(items[position])

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    class ViewHolder constructor(row: TimeChartView): RecyclerView.ViewHolder(row){

    }
}

