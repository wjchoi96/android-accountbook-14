package com.example.android_accountbook_14.domain.repository

import com.example.android_accountbook_14.domain.model.LedgerModel

interface LedgerRepository {

    suspend fun insertLedger(
        year: Int,
        month: Int,
        day: Int,
        dayOfWeek: String,
        price: Long,
        tagId: Int,
        paymentId: Int?,
        content: String?
    ): Result<Boolean>

    suspend fun updateLedger(
        id: Int,
        year: Int,
        month: Int,
        day: Int,
        dayOfWeek: String,
        price: Long,
        tagId: Int,
        paymentId: Int?,
        content: String?
    ): Result<Boolean>

    suspend fun fetchLedgers(year: Int, month: Int): Result<List<LedgerModel>>

    suspend fun removeLedgers(removeIds: List<Int>): Result<Boolean>
}