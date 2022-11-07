package com.example.android_accountbook_14.data.repsoitory

import com.example.android_accountbook_14.di.IODispatcher
import com.example.android_accountbook_14.data.datasource.LedgerDao
import com.example.android_accountbook_14.domain.model.LedgerModel
import com.example.android_accountbook_14.domain.repository.LedgerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LedgerRepositoryImpl @Inject constructor(
    private val ledgerDao: LedgerDao,
    @IODispatcher private val coroutineDispatcher: CoroutineDispatcher
): LedgerRepository {

    override suspend fun insertLedger(
        year: Int,
        month: Int,
        day: Int,
        dayOfWeek: String,
        price: Long,
        tagId: Int,
        paymentId: Int?,
        content: String?
    ): Result<Boolean> {
        return withContext(coroutineDispatcher){
            kotlin.runCatching {
                val res = ledgerDao.insertLedger(
                    year,
                    month,
                    day,
                    dayOfWeek,
                    price,
                    tagId,
                    paymentId,
                    content
                )
                res >= 0L
            }
        }
    }

    override suspend fun updateLedger(
        id: Int,
        year: Int,
        month: Int,
        day: Int,
        dayOfWeek: String,
        price: Long,
        tagId: Int,
        paymentId: Int?,
        content: String?
    ): Result<Boolean> {
        return withContext(coroutineDispatcher){
            kotlin.runCatching {
                val res = ledgerDao.updateLedger(
                    id,
                    year,
                    month,
                    day,
                    dayOfWeek,
                    price,
                    tagId,
                    paymentId,
                    content
                )
                res >= 0L
            }
        }
    }

    override suspend fun fetchLedgers(year: Int, month: Int): Result<List<LedgerModel>> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                ledgerDao.fetchLedgers(year, month).map { it.toDomain() }
            }
        }
    }

    override suspend fun removeLedgers(removeIds: List<Int>): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                val res = ledgerDao.removeLedges(removeIds)
                res > 0
            }
        }
    }
}