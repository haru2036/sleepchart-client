package com.haru2036.sleepchart.infra.dao

import android.database.sqlite.SQLiteDatabase
import com.haru2036.sleepchart.domain.entity.GadgetBridgeActivitySample
import io.reactivex.Single


class GadgetBridgeDao() {
    fun getActivitySamples(): Single<List<GadgetBridgeActivitySample>> {
        return Single.create {
            val activitySamples = mutableListOf<GadgetBridgeActivitySample>()
            SQLiteDatabase.openDatabase("/storage/emulated/0/Android/data/nodomain.freeyourgadget.gadgetbridge/files/gadgetbridge", null, SQLiteDatabase.OPEN_READONLY).use {
                val cursor = it.query("MI_BAND_ACTIVITY_SAMPLE", arrayOf("TIMESTAMP", "RAW_KIND"), "", emptyArray(), "", "", "")
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    activitySamples.add(GadgetBridgeActivitySample(cursor.getLong(0), cursor.getInt(1)))
                    cursor.moveToNext()
                }
            }
            it.onSuccess(activitySamples)
        }
    }
}