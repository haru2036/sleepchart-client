package com.haru2036.sleepchart.presentation.fragment

import android.Manifest
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.haru2036.sleepchart.R
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.usecase.GadgetBridgeUseCase
import com.haru2036.sleepchart.domain.usecase.GoogleFitUseCase
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.extensions.addTo
import com.haru2036.sleepchart.presentation.adapter.SleepChartAdapter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_sleepchart.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.NoSuchElementException

class SleepChartFragment : Fragment(){
    private val chartRecyclerView: RecyclerView by lazy { view.findViewById<RecyclerView>(R.id.fragment_sleepchart_recyclerview) }
    private val fab: FloatingActionButton by lazy { view.findViewById<FloatingActionButton>(R.id.fab) }
    private val chartView: LinearLayout by lazy { view.findViewById<LinearLayout>(R.id.fragment_sleepchart_main_container) }
    private val progressBar: ProgressBar by lazy { view.findViewById<ProgressBar>(R.id.fragment_sleepchart_progress)}

    private val disposables: CompositeDisposable = CompositeDisposable()
    private val GOOGLE_FIT_PERMISSION_REQUEST_CODE = 1

    @Inject
    lateinit var sleepUsecase: SleepUseCase

    @Inject
    lateinit var gadgetBridgeUseCase: GadgetBridgeUseCase

    @Inject
    lateinit var googleFitUseCase: GoogleFitUseCase

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
            orientation = RecyclerView.VERTICAL
        }

        fab.setOnLongClickListener {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(1000L)
            trackSleepTwice()
        }

        chartRecyclerView.adapter = SleepChartAdapter(context)
        if (activity.intent.getBooleanExtra("NEEDS_RESTORE", false)) {
            restoreLatestSleeps()
        }
        showSleeps()

    }

    private fun restoreLatestSleeps() {
        sleepUsecase.restoreLatestSleeps()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            showSleeps()
                        },
                        {
                            Timber.tag("sleepchart-error").e(it)
                        }
                ).addTo(disposables)
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

        fragment_sleepchart_swipe_to_refresh.setOnRefreshListener {
            fragment_sleepchart_swipe_to_refresh.isRefreshing = true
            sleepUsecase.fetchOlderSleeps()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val adapter = chartRecyclerView.adapter as SleepChartAdapter
                        adapter.addOlderSleeps(it)
                        adapter.notifyItemRangeInserted(0, adapter.sleepsToRowsCount(it))
                        chartRecyclerView.scrollToPosition(adapter.sleepsToRowsCount(it))

                        fragment_sleepchart_swipe_to_refresh.isRefreshing = false

                    }, { Timber.e(it) }).addTo(disposables)


        }
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

    fun importSleepsFromGadgetBridge() {

        RxPermissions(activity!!).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .filter { it }
                .flatMap { gadgetBridgeUseCase.syncActivity() }
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    Snackbar.make(view, R.string.message_import_imported, Snackbar.LENGTH_LONG).show()
                }
                .subscribe({
                    showSleeps()

                }, {
                    Log.e("Failed to import GB DB", it.toString())
                    Timber.e(it)
                    Snackbar.make(view, R.string.message_import_import_failed, Snackbar.LENGTH_LONG)
                            .show()
                }).addTo(disposables)

    }
    fun importSleepsFromGoogleFit() {
        val fitnessOptions = FitnessOptions.builder().apply {
            addDataType(DataType.TYPE_ACTIVITY_SAMPLES, FitnessOptions.ACCESS_READ)
            addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
        }.build()
        if(!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions)){
            GoogleSignIn.requestPermissions(activity, GOOGLE_FIT_PERMISSION_REQUEST_CODE, GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions)
        }else{
            requestSleepsFromGoogleFit()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                GOOGLE_FIT_PERMISSION_REQUEST_CODE -> requestSleepsFromGoogleFit()
            }
        }
    }

    private fun requestSleepsFromGoogleFit() {
        progressBar.visibility = View.VISIBLE
        RxPermissions(activity).request(Manifest.permission.ACCESS_FINE_LOCATION)
                .flatMapSingle { googleFitUseCase.importSleeps(context) }
                .singleOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val adapter = chartRecyclerView.adapter as SleepChartAdapter
                    val oldItemCount = adapter.itemCount
                    adapter.addNewerSleeps(it)
                    adapter.notifyItemRangeInserted(oldItemCount, oldItemCount + adapter.sleepsToRowsCount(it))
                    progressBar.visibility = View.GONE
                    Snackbar.make(view, R.string.message_import_imported, Snackbar.LENGTH_LONG).show()
                }, {
                    Timber.tag("sleepchart-error").e(it)
                    progressBar.visibility = View.GONE
                    when (it::class.java) {
                        NoSuchElementException::class.java ->
                            Snackbar.make(view, R.string.message_import_import_no_new_sleeps, Snackbar.LENGTH_LONG).show()
                        else ->
                            Snackbar.make(view, R.string.message_import_import_failed, Snackbar.LENGTH_LONG).show()
                    }
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
                    val exportFile = File(directory, fileNameString + ".jpg")
                    val fileOutputStream = FileOutputStream(exportFile)

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fileOutputStream)
                    fileOutputStream.close()

                    val intent = Intent(Intent.ACTION_SEND)

                    val uri = FileProvider.getUriForFile(
                            activity
                            , activity.getApplicationContext().getPackageName() + ".provider"
                            , exportFile)

                    intent.type = "image/jpeg"
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    val chooser = Intent.createChooser(intent, getString(R.string.choose_app))
                    if(intent.resolveActivity(activity.packageManager) != null){
                        startActivity(chooser)
                    }
                    Toast.makeText(context, "Saved image to:$uri", Toast.LENGTH_LONG).show()
                    Log.d("saved image:", "uri")
                }.addTo(disposables)

    }

    private fun scrollToLast() = chartRecyclerView.smoothScrollToPosition(chartRecyclerView.adapter!!.itemCount)

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
