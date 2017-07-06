package com.haru2036.sleepchart.presentation.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.RecyclerView
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.presentation.adapter.SleepChartAdapter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SleepChartFragment : Fragment(){
    val chartRecyclerView: RecyclerView by lazy { view.findViewById(R.id.fragment_sleepchart_recyclerview) as RecyclerView }

    @Inject
    lateinit var sleepUsecase: SleepUseCase

    companion object {
        @JvmStatic
        fun newInstance() = SleepChartFragment()

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater !!.inflate(R.layout.fragment_sleepchart, container, false)
    }

    override fun onResume() {
        super.onResume()
        //ダミーデータ

        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val sleep1 = Sleep(dateFormat.parse("2017-07-05 20:25"), dateFormat.parse("2017-07-06 07:30"))
        val sleep2 = Sleep(dateFormat.parse("2017-07-06 22:25"), dateFormat.parse("2017-07-07 08:00"))
        val now = Calendar.getInstance().time
        val future = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 6) }.time

        chartRecyclerView.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        chartRecyclerView.adapter = SleepChartAdapter(context, listOf(Pair(sleep1.start, listOf(sleep1)), Pair(sleep2.start, listOf(sleep2)), Pair(now, listOf(Sleep(now, future)))))

    }


}