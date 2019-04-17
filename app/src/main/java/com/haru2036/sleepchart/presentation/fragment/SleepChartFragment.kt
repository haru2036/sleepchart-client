package com.haru2036.sleepchart.presentation.fragment

import android.Manifest
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Vibrator
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.RecyclerView
import android.support.design.widget.Snackbar
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.app.Constants
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.extensions.addTo
import com.haru2036.sleepchart.presentation.adapter.SleepChartAdapter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SleepChartFragment : Fragment(){
    private val chartRecyclerView: RecyclerView by lazy { view.findViewById<RecyclerView>(R.id.fragment_sleepchart_recyclerview) }
    private val fab: FloatingActionButton by lazy { view.findViewById<FloatingActionButton>(R.id.fab) }
    private val chartView: LinearLayout by lazy { view.findViewById<LinearLayout>(R.id.fragment_sleepchart_main_container) }

    private val disposables: CompositeDisposable = CompositeDisposable()

    @Inject
    lateinit var sleepUsecase: SleepUseCase

    companion object {
        @JvmStatic
        fun newInstance() = SleepChartFragment()

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        SleepChart.getAppComponent().plus(SleepModule()).inject(this)
        return inflater !!.inflate(R.layout.fragment_sleepchart, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fab.setOnClickListener { toggleSleep() }

        sleepUsecase.isSleeping()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe ({
                    setStateColors(it)
                },
                        {
                            Timber.e(it)
                        }).addTo(disposables)

        chartRecyclerView.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        fab.setOnLongClickListener {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(1000L)
            trackSleepTwice()
        }

        chartRecyclerView.adapter = SleepChartAdapter(context)
        showSleeps()

    }


    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    fun showSleeps() {
        sleepUsecase.findSleeps().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { sleeps ->
                    val adapter = chartRecyclerView.adapter as SleepChartAdapter
                    adapter.items = sleeps
                    adapter.notifyDataSetChanged()
                    scrollToLast()
                }.addTo(disposables)
    }

    private fun toggleSleep(){
        sleepUsecase.logSleepToggle(Calendar.getInstance().time)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    setStateColors(!it)
                    if(it){
                        Snackbar.make(view, R.string.good_morning, Snackbar.LENGTH_LONG).show()
                    }else{
                        Snackbar.make(view, R.string.good_night, Snackbar.LENGTH_LONG).show()
                    }
                    showSleeps()
                },
                {
                    Timber.e(it)
                }).addTo(disposables)

    }

    private fun trackSleepTwice() = sleepUsecase.trackSleepTwice()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ newId ->
                Snackbar.make(view, R.string.sleep_twice_saved, Snackbar.LENGTH_LONG).apply {
                    setAction(R.string.cancel, {
                        sleepUsecase.deleteSleep(newId)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({
                                    showSleeps()
                                }, {
                                    Timber.e(it)
                                }).addTo(disposables)
                        }
                    )
                    show()
                }

                showSleeps()
            }, {
                Timber.e(it)
            }).addTo(disposables)


    fun exportChart(){

        RxPermissions(activity).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .filter { it }
                .subscribe {

                    val bitmap = Bitmap.createBitmap(chartView.width, chartView.height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    chartView.draw(canvas)

                    val directory = File(Environment.getExternalStorageDirectory().canonicalPath + "/com.haru2036.sleepchart")
                    if(!directory.exists()){
                        directory.mkdirs()
                    }

                    val fileNameString = "sleep-" + SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
                    val fileOutputStream = FileOutputStream(File(directory, fileNameString + ".jpg"))

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fileOutputStream)
                    fileOutputStream.close()

                    val intent = Intent(Intent.ACTION_SEND)

                    intent.type = "image/jpeg"
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://$directory/$fileNameString.jpg"))
                    val chooser = Intent.createChooser(intent, getString(R.string.choose_app))
                    if(intent.resolveActivity(activity.packageManager) != null){
                        startActivity(chooser)
                    }
                    Toast.makeText(context, "Saved image to: file://$directory/$fileNameString.jpg", Toast.LENGTH_LONG).show()
                    Log.d("saved image:" , "file://$directory/$fileNameString.jpg")
                }.addTo(disposables)

    }

    private fun scrollToLast() = chartRecyclerView.smoothScrollToPosition(chartRecyclerView.adapter.itemCount)

    private fun setStateColors(isSleeping: Boolean) {
        if(isSleeping){
            fab.backgroundTintList = ColorStateList.valueOf(activity.getColor(R.color.colorAccent))
            fab.setImageDrawable(activity.getDrawable(R.drawable.ic_wb_sunny_white_24dp))
        }else{
            fab.backgroundTintList = ColorStateList.valueOf(activity.getColor(R.color.colorWakeAccent))
            fab.setImageDrawable(activity.getDrawable(R.drawable.ic_local_hotel_white_24dp))
        }
    }

}