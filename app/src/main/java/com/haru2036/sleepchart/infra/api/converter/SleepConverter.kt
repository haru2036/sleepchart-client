package com.haru2036.sleepchart.infra.api.converter

import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.infra.api.response.SleepResponse
import com.squareup.moshi.Moshi
import java.text.SimpleDateFormat

/**
 * Created by haru2036 on 2016/11/28.
 */
object SleepConverter {
    fun convert(result: SleepResponse) = Sleep(id = 0, start = result.csStart, end = result.csEnd)
}
