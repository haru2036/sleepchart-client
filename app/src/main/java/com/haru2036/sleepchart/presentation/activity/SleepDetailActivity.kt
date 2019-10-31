package com.haru2036.sleepchart.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.presentation.presenter.SleepDetailPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sleep_detail.*
import timber.log.Timber
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
    @Inject
    lateinit var presenter: SleepDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_sleep_detail)
        SleepChart.getAppComponent().plus(SleepModule()).inject(this)
        compositeDisposable.add(presenter.loadSleep(intent.getLongExtra("sleep_id",-1))
                .singleOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //todo: フォーマットは暫定対応
                    activity_sleep_detail_timechart.sleeps = listOf(it)
                    activity_sleep_detail_start_time.text = it.start.toLocaleString()
                    activity_sleep_detail_end_time.text = it.end.toLocaleString()
                    it.rating?.let { activity_sleep_detail_rating_bar.rating = it.toFloat()
                    }
                },{
                    Timber.e(it)
                    Toast.makeText(this, "その睡眠は存在しません", Toast.LENGTH_LONG).show()
                    finish()
                }))
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}