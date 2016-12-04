package com.haru2036.sleepchart.infra.api.converter

import com.haru2036.sleepchart.domain.entity.Sleep
import com.haru2036.sleepchart.infra.api.SleepResponse

/**
 * Created by haru2036 on 2016/11/28.
 */
object SleepConverter {
    fun convert(result: SleepResponse) = Sleep(result.start, result.end)
}
