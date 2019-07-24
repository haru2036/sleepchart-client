package com.haru2036.sleepchart.domain.entity

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table
import com.squareup.moshi.Json
import java.util.*

@Table
data class Sleep(
        @PrimaryKey(autoincrement = true) @Column
        var id: Long,
        @Column @Setter("start") @Json(name = "csStart")
        var start: Date,
        @Column @Setter("end") @Json(name = "csEnd")
        var end: Date
)
