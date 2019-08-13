package com.haru2036.sleepchart.infra.repository

import com.haru2036.sleepchart.domain.entity.UnfetchedItemRange
import com.haru2036.sleepchart.infra.dao.PagingDao
import javax.inject.Inject

class PagingRepository @Inject constructor(private val pagingDao: PagingDao) {
    fun getUnfetchedSleepRange() = pagingDao.getUnfetchedSleepRange()

    fun updateUnfetchedSleepRange(newRanges: List<UnfetchedItemRange>) = pagingDao.updateUnfetchedSleepRange(newRanges)
}
