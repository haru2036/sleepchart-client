package com.haru2036.sleepchart.infra.api.response

import java.util.*


data class SleepResponse(  val csId: Long?
                         , val csStart: Date
                         , val csEnd: Date
                         , val csResult: Int?)
