package com.haru2036.sleepchart.domain.entity

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table
import java.util.*

@Table
data class SleepSession(
    @PrimaryKey(autoincrement = true) @Column
    var id: Long,
    @Column @Setter("start")
    var start: Date
)