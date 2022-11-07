package com.example.android_accountbook_14.data.repsoitory

import com.example.android_accountbook_14.domain.model.ColorLabel
import com.example.android_accountbook_14.domain.model.LedgerModel
import com.example.android_accountbook_14.domain.model.TextLabel
import com.example.android_accountbook_14.domain.repository.LedgerRepository

class FakeLedgerRepository(
    private val textLabels: List<TextLabel> = emptyList(),
    private val colorLabels: List<ColorLabel> = emptyList()
): LedgerRepository {

    private val ledgerList = mutableListOf<LedgerModel>()

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
        val id = if(ledgerList.isEmpty()) 1 else ledgerList.last().id.plus(1)
        val colorLabel = colorLabels.firstOrNull { it.id == tagId } ?: return Result.success(false)
        ledgerList.add(
            LedgerModel(
                id,
                year,
                month,
                day,
                dayOfWeek,
                price,
                colorLabel.title,
                colorLabel.color,
                textLabels.firstOrNull{ it.id == paymentId }?.title,
                content
            )
        )
        return Result.success(true)
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
        TODO("Not yet implemented")
    }

    override suspend fun fetchLedgers(year: Int, month: Int): Result<List<LedgerModel>> {
        return Result.success(ledgerList)
    }

    override suspend fun removeLedgers(removeIds: List<Int>): Result<Boolean> {
        TODO("Not yet implemented")
    }
}