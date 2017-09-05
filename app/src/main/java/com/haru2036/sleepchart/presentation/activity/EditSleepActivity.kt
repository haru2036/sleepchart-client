package com.haru2036.sleepchart.presentation.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.databinding.ActivityEditSleepBinding
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.presentation.viewmodel.EditSleepViewModel
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SleepChart.getAppComponent().plus(SleepModule()).inject(this)
        binding.viewModel = editSleepViewModel
        val sleep = sleepUseCase.getSleep(intent.getLongExtra("sleepId", -1L))
        if(sleep == null){
            Toast.makeText(this, "Sleep not found", Toast.LENGTH_LONG).show()
            finish()
        }else{
            binding.viewModel.sleep = sleep
        }
    }
}