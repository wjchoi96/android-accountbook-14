package com.example.android_accountbook_14.domain.usecase

import com.example.android_accountbook_14.di.DefaultDispatcher
import com.example.android_accountbook_14.domain.model.LedgerMonthChartModel
import com.example.android_accountbook_14.domain.repository.LedgerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchLedgerMonthChartDataUseCase @Inject constructor(
    private val ledgerRepository: LedgerRepository,
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(year: Int, month: Int): Result<LedgerMonthChartModel> {
        return kotlin.runCatching {
            withContext(coroutineDispatcher){
                ledgerRepository.fetchLedgers(year, month).getOrThrow().let { list ->
                    val chartItems = mutableListOf<LedgerMonthChartModel.LedgerChartItemModel>()
                    val spendMap = mutableMapOf<String, Long>()
                    val colorMap = mutableMapOf<String, String>()
                    list.filter { i -> i.price < 0 }.forEach {
                        spendMap[it.tag] = spendMap.getOrDefault(it.tag, 0) + it.price
                        colorMap[it.tag] = it.tagColorHex
                    }
                    spendMap.keys.forEach {
                        chartItems.add(LedgerMonthChartModel.LedgerChartItemModel(
                            spendMap[it]!!, it, colorMap[it]!!
                        ))
                    }
                    LedgerMonthChartModel(chartItems)
                }
            }
        }
    }
}