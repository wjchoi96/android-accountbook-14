package com.example.android_accountbook_14.domain.usecase

import com.example.android_accountbook_14.di.DefaultDispatcher
import com.example.android_accountbook_14.domain.model.LedgerListItemModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FilterLedgersUseCase @Inject constructor(
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        rawList: List<LedgerListItemModel>,
        containIncome: Boolean,
        containSpend: Boolean
    ): Result<List<LedgerListItemModel>> {
        return kotlin.runCatching {
            withContext(coroutineDispatcher){
                when {
                    containIncome && containSpend -> rawList
                    containIncome -> {
                        rawList.toMutableList().map {  raw ->
                            raw.copy(
                                dayLedgers = raw.dayLedgers.filter { it.price >= 0 }
                            )
                        }.filter { it.dayLedgers.isNotEmpty() }
                    }
                    containSpend -> {
                        rawList.toMutableList().map {  raw ->
                            raw.copy(
                                dayLedgers = raw.dayLedgers.filter { it.price < 0 }
                            )
                        }.filter { it.dayLedgers.isNotEmpty() }
                    }
                    else -> emptyList()
                }
            }
        }
    }
}