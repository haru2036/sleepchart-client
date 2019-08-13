package com.haru2036.sleepchart.infra.dao

import com.haru2036.sleepchart.di.OrmaHandler
import com.haru2036.sleepchart.domain.entity.UnfetchedItemRange
import java.util.*
import javax.inject.Inject


class PagingDao @Inject constructor(private val orma: OrmaHandler) {
    fun getUnfetchedSleepRange() = orma.db.selectFromUnfetchedItemRange()
            .orderByStartAsc()
            .executeAsObservable()

    fun updateUnfetchedSleepRange(newRanges: List<UnfetchedItemRange>) = orma.db.deleteFromUnfetchedItemRange()
            .startNotEq(Date(0))
            .executeAsSingle()
            .flatMapCompletable {
                orma.db.transactionAsCompletable {
                    orma.db.prepareInsertIntoUnfetchedItemRange().executeAllAsObservable(newRanges)
                }
            }
}