package com.haru2036.sleepchart.domain.usecase

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.SessionReadRequest
import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.infra.repository.SleepRepository
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GoogleFitUseCase @Inject constructor(private val sleepRepository: SleepRepository){
    fun importSleeps(context: Context) =
        sleepRepository.findSleeps()
                .toList()
                .map { it.apply { add(0, Sleep(id = 0, start = Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, -1) }.time, end = Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, -1) }.time)) } }
                .map{ Pair(it.last().end, Calendar.getInstance().time)}
                .flatMap { requestToGoogleFit(context, it) }
                .flatMapObservable { sleeps ->
                    sleepRepository.createSleeps(sleeps).map { sleeps }
                }

    private fun requestToGoogleFit(context: Context, range: Pair<Date, Date>): Single<List<Sleep>>{
        val readRequest = SessionReadRequest.Builder().apply {
            read(DataType.TYPE_ACTIVITY_SEGMENT)
            setTimeInterval(range.first.time, range.second.time, TimeUnit.MILLISECONDS)
            readSessionsFromAllApps()
            enableServerQueries()
        }.build()
        return Single.create{emitter: SingleEmitter<List<Sleep>> ->
            Fitness.getSessionsClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
                    .readSession(readRequest)
                    .addOnSuccessListener {
                        emitter.onSuccess(it.sessions.filter { it.activity == "sleep" }.map{
                            Sleep(id = 0, start = Date(it.getStartTime(TimeUnit.MILLISECONDS)), end = Date(it.getEndTime(TimeUnit.MILLISECONDS)))
                        })
                    }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }
}