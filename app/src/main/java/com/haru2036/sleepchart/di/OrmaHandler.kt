package com.haru2036.sleepchart.di

import android.content.Context
import com.haru2036.sleepchart.domain.entity.OrmaDatabase

/**
 * Created by haru2036 on 2017/07/11.
 */
class OrmaHandler {
    val db: OrmaDatabase

    constructor(context: Context) {
        db = OrmaDatabase.builder(context).build()
    }
}