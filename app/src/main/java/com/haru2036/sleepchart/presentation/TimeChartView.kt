package com.haru2036.sleepchart.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.domain.entity.Sleep
import org.threeten.bp.Instant
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.text.SimpleDateFormat
import java.util.*


class TimeChartView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    val dayInSec: Int = 86400
    //18時始まりにするためのオフセット
    val nightOffsetHours: Int = 4
    var onSleepClickListener: ((Sleep) -> Unit)? = null
    var windowWidth = 0
    var sleeps: List<Sleep> = emptyList()
    set(value) {
        field = value
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        windowWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        sleeps.map{layoutSingleItem(it)}
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }


    fun clearAllSleeps(){
        removeAllViews()
        sleeps = emptyList()
    }

    fun layoutSingleItem(sleep: Sleep){
        //todo: Sleep等に使われている時刻をすべてJSR-310のやつにする
        val startTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(sleep.start.time), ZoneId.systemDefault()).toLocalTime().plusHours(nightOffsetHours.toLong())
        val endTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(sleep.end.time), ZoneId.systemDefault()).toLocalTime().plusHours(nightOffsetHours.toLong())
        val startPx = measureTimeToPx(startTime, windowWidth)
        val endPx = measureTimeToPx(endTime, windowWidth)

        val longPx = endPx - startPx

        val sleepView = Button(context)
        sleepView.setOnClickListener {
            onSleepClickListener?.invoke(sleep)
        }
        sleepView.setBackgroundColor(context.getColor(R.color.sleepColor))
        sleepView.text = SimpleDateFormat("MM/dd HH:mm ~", Locale.JAPAN).format(sleep.start)
        val layoutParams = if(sleepView.layoutParams == null){
            RelativeLayout.LayoutParams(longPx, ViewGroup.LayoutParams.MATCH_PARENT)
        }else{
            sleepView.layoutParams
        }
        if(layoutParams is RelativeLayout.LayoutParams){
            sleepView.layoutParams  = layoutParams.apply {
                leftMargin = startPx
                height = ViewGroup.LayoutParams.MATCH_PARENT
                width = longPx
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


    //todo なまえ
    fun measureTimeToPx(timeOfDay: LocalTime, parentWidth: Int) = if(parentWidth != 0) {
        (timeOfDay.toSecondOfDay() * (parentWidth.toDouble() / dayInSec.toDouble())).toInt()
    }else{
        0
    }

}
