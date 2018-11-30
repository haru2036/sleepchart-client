package com.haru2036.sleepchart.infra.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.haru2036.sleepchart.domain.entity.GadgetBridgeActivitySample
import io.reactivex.Single
import java.util.*


class GadgetBridgeDao() {
    fun getActivitySamples(): Single<List<GadgetBridgeActivitySample>> {
        return Single.create { it ->
            val activitySamples = mutableListOf<GadgetBridgeActivitySample>()
            SQLiteDatabase.openDatabase("/storage/emulated/0/Android/data/nodomain.freeyourgadget.gadgetbridge/files/Gadgetbridge", null, SQLiteDatabase.OPEN_READONLY).use {
                val cursor = it.query("MI_BAND_ACTIVITY_SAMPLE", arrayOf("TIMESTAMP", "RAW_KIND"), "", emptyArray(), "", "", "")
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    activitySamples.add(GadgetBridgeActivitySample(Date(cursor.getLong(0) * 1000), cursor.getInt(1)))
                    cursor.moveToNext()
                }
                cursor.close()
                it.close()
            }
            it.onSuccess(activitySamples)
        }
    }
}