package com.haru2036.sleepchart.presentation.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.presentation.fragment.SleepChartFragment
import rx.subscriptions.CompositeSubscription

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.name


    val subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null){
            fragmentManager.beginTransaction()
                    .add(R.id.activity_main_fragment_container, SleepChartFragment.newInstance())
                    .commit()
        }
//        val listView = findViewById(R.id.activity_main_list) as ListView
//        SleepChart.getAppComponent().plus(SleepModule(SleepChart.getRetrofit())).inject(this)
//        subscriptions.add(sleepUsecase.fetchSleeps()
//                .delay(500, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    Log.d(it.toString(), it.toString())
//                    listView.adapter = MainSleepAdapter(this, it)
//                }, {
//                    Log.e(it.toString(), it.toString())
//                })
//        )
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.unsubscribe()
    }
}
