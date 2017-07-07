package com.haru2036.sleepchart.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.domain.entity.Sleep
import java.text.SimpleDateFormat
import java.util.*

class TimeChartView : RelativeLayout {
    var parentWidth: Int? = 1920
    val dayInMillsec: Long = 86400 * 1000
    //18時始まりにするためのオフセット
    val nightOffsetPx: Double by lazy { measureTimeToPx(Date(9 * 60 * 60 * 1000 + currentTimeZoneOffsetFromUtc().toLong())) }

    fun setSleeps(sleeps: List<Sleep>) {
        sleeps.map { layoutSingleItem(it) }
        invalidate()
    }

    @JvmOverloads
    public constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
    }

    fun layoutSingleItem(sleep: Sleep){
        val startTime = timeOfDay(sleep.start)
        val endTime = shiftToEpoch(sleep.end, startTime)
        val startPx = measureTimeToPx(startTime) - nightOffsetPx
        val endPx = measureTimeToPx(endTime) - nightOffsetPx

        val longPx = endPx - startPx

        val sleepView = Button(context)
        sleepView.setBackgroundColor(context.getColor(R.color.sleepColor))
        sleepView.text = SimpleDateFormat("MM/dd HH:mm ~", Locale.JAPAN).format(sleep.start)
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
        addView(sleepView)
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

}
