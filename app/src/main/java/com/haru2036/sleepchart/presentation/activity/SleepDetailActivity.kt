package com.haru2036.sleepchart.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.databinding.ActivitySleepDetailBinding
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.presentation.viewmodel.SleepDetailViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SleepDetailActivity : AppCompatActivity(){
    companion object {
        fun start(context: Context, sleep: Sleep) {
            val intent = Intent(context, SleepDetailActivity::class.java).apply {
                putExtra("sleep_id", sleep.id)
            }
            context.startActivity(intent)
        }
    }


    val compositeDisposable = CompositeDisposable()

    val binding: ActivitySleepDetailBinding by lazy { DataBindingUtil.setContentView<ActivitySleepDetailBinding>(this, R.layout.activity_sleep_detail) }

    @Inject
    lateinit var sleepDetailViewModel: SleepDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep_detail)
        SleepChart.getAppComponent().plus(SleepModule()).inject(this)
        binding.viewmodel = sleepDetailViewModel
        sleepDetailViewModel.loadSleep(intent.getLongExtra("sleep_id", -1))

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}