package com.haru2036.sleepchart.presentation.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.databinding.ActivityEditSleepBinding
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.presentation.viewmodel.EditSleepViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class EditSleepActivity : AppCompatActivity() {
    val binding by lazy { DataBindingUtil.setContentView<ActivityEditSleepBinding>(this, R.layout.activity_edit_sleep) }

    @Inject
    lateinit var editSleepViewModel: EditSleepViewModel

    @Inject
    lateinit var sleepUseCase: SleepUseCase

    companion object {
        fun createIntent(context: Context, sleepId: Long) = Intent(context, EditSleepActivity::class.java).apply {
            putExtra("sleepId", sleepId)
        }

        fun createIntent(context: Context) = Intent(context, EditSleepActivity::class.java).apply {
            putExtra("sleepId", -1L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SleepChart.getAppComponent().plus(SleepModule()).inject(this)
        val sleepId = intent.getLongExtra("sleepId", -2L)

        if (sleepId == -1L) {
            val newSleep = Sleep(0L, Calendar.getInstance(Locale.getDefault()).time, Calendar.getInstance(Locale.getDefault()).time)
            sleepUseCase.saveSleep(newSleep)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .map { sleepUseCase.getSleep(it) !! }
                    .subscribe({
                        binding.viewModel = editSleepViewModel
                        binding.viewModel.sleep = it
                    }, {
                        Timber.e(it)
                        Toast.makeText(this@EditSleepActivity, it.message, Toast.LENGTH_LONG).show()
                        finish()
                    })

        } else {
            val sleep = sleepUseCase.getSleep(sleepId)
            if (sleep == null) {
                Toast.makeText(this, "Sleep not found", Toast.LENGTH_LONG).show()
                finish()
            } else {
                binding.viewModel = editSleepViewModel
                binding.viewModel.sleep = sleep
            }
        }
    }
}