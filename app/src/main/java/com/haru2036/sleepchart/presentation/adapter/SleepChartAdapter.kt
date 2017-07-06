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
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by haru2036 on 2017/07/05.
 */

class SleepChartAdapter(val context: Context, val items: List<Pair<Date, List<Sleep>>>) : RecyclerView.Adapter<SleepChartAdapter.ViewHolder>() {
    val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }
    var parentWidth: Int? = 1920
    val dayInMillsec: Long = 86400 * 1000
    //18時始まりにするためのオフセット
    val nightOffsetPx: Double by lazy { measureTimeToPx(Date(9 * 60 * 60 * 1000 + currentTimeZoneOffsetFromUtc().toLong())) }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(inflater.inflate(R.layout.item_sleepchart_day, parent, false) as RelativeLayout)

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val day = items.get(position)
        day.second.map {
            val startTime = timeOfDay(it.start)
            val endTime = shiftToEpoch(it.end, startTime)
            val startPx = measureTimeToPx(startTime) - nightOffsetPx
            val endPx = measureTimeToPx(endTime) - nightOffsetPx

            val longPx = endPx - startPx

            holder?.itemView?.let{ relativeLayout ->
                if(relativeLayout is RelativeLayout){
                    val sleepView = Button(context)
                    sleepView.setBackgroundColor(context.getColor(R.color.sleepColor))
                    sleepView.text = SimpleDateFormat("MM/dd HH:mm ~", Locale.JAPAN).format(it.start)
                    val layoutParams = if(sleepView.layoutParams == null){
                        RelativeLayout.LayoutParams(longPx.toInt(), ViewGroup.LayoutParams.MATCH_PARENT)
                    }else{
                        sleepView.layoutParams
                    }
                    if(layoutParams is RelativeLayout.LayoutParams){
                        sleepView.layoutParams  = layoutParams.apply {
                            leftMargin = startPx.toInt()
                            height = ViewGroup.LayoutParams.MATCH_PARENT
                            width = longPx.toInt()
                        }
                    }
                    relativeLayout.addView(sleepView)
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        //todo 画面幅入れる
    }

    fun currentTimeZoneOffsetFromUtc(): Int{
        val timeZone = TimeZone.getDefault()
        val calendar = GregorianCalendar.getInstance(timeZone)
        val offset = timeZone.getOffset(calendar.timeInMillis)

        return offset
    }

    fun timeOfDay(date: Date): Date{
        val timeOnly = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            time = date
            set(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.MONTH, 1)
            set(Calendar.YEAR, 1970)
            time = Date(time.time + currentTimeZoneOffsetFromUtc().toLong())
        }.time
        return timeOnly
    }

    fun shiftToEpoch(endDate: Date, base: Date): Date{
        val endTime = timeOfDay(endDate)
        return if(endTime < base){
            //日付またいだときの処理
            Calendar.getInstance().apply {
                time = endTime
                set(Calendar.DAY_OF_YEAR, 2)
            }.time
        }else{
            endTime
        }
    }

    //todo なまえ
    fun measureTimeToPx(timeOfDay: Date) = if(parentWidth != 0 && timeOfDay.time != 0L) {
        timeOfDay.time * ((parentWidth!!.toDouble()) / dayInMillsec.toDouble())
    }else{
        throw IllegalStateException("width must not be 0")
    }

    class ViewHolder constructor(row: RelativeLayout): RecyclerView.ViewHolder(row){

    }
}

