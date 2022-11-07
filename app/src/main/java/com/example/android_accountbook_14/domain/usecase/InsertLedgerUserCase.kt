package com.example.android_accountbook_14.domain.usecase

import com.example.android_accountbook_14.di.DefaultDispatcher
import com.example.android_accountbook_14.domain.repository.LedgerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InsertLedgerUserCase @Inject constructor(
    private val ledgerRepository: LedgerRepository,
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        year: Int,
        month: Int,
        day: Int,
        dayOfWeek: String,
        price: Long,
        tagId: Int,
        paymentId: Int?,
        content: String?
    ): Result<Boolean>{
        return withContext(coroutineDispatcher){
            ledgerRepository.insertLedger(
                year, month, day, dayOfWeek, price, tagId, paymentId, content
            )
        }
    }
}