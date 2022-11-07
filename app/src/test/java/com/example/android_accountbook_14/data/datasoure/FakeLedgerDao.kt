package com.example.android_accountbook_14.data.datasoure

import com.example.android_accountbook_14.data.entity.ColorLabelEntity
import com.example.android_accountbook_14.data.entity.LedgerEntity
import com.example.android_accountbook_14.data.entity.TextLabelEntity
import com.example.android_accountbook_14.data.datasource.LedgerDao
import timber.log.Timber

class FakeLedgerDao(
    private val ledgers: MutableList<LedgerEntity>,
    private val textLabels: MutableList<TextLabelEntity>,
    private val colorLabels: MutableList<ColorLabelEntity>,
): LedgerDao {

    override suspend fun insertLedger(
        year: Int,
        month: Int,
        day: Int,
        dayOfWeek: String,
        price: Long,
        tagId: Int,
        paymentId: Int?,
        content: String?
    ): Boolean {
        colorLabels.forEach {
            println("[${it.title} : ${it.id}]")
        }
        println("tagId = $tagId")
        ledgers.add(
            LedgerEntity(
                ledgers[ledgers.size-1].id.plus(1),
                year,
                month,
                day,
                dayOfWeek,
                price,
                colorLabels.first { it.id == tagId },
                textLabels.firstOrNull { it.id == paymentId },
                content
            )
        )
        return true
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
    ): Boolean {
        val idx = ledgers.indices.find { ledgers[it].id == id }
        return if(idx in 0 until ledgers.size){
            ledgers[idx!!] = LedgerEntity(
                ledgers[idx].id,
                year,
                month,
                day,
                dayOfWeek,
                price,
                colorLabels.first { it.id == tagId },
                textLabels.firstOrNull { it.id == paymentId },
                content
            )
            true
        }else{
            false
        }
    }

    override suspend fun fetchLedgers(year: Int, month: Int): List<LedgerEntity> {
        return ledgers.filter { it.year == year && it.month == month }.sortedBy { -it.day } // 최신순 정렬
    }

    override suspend fun removeLedges(removeIds: List<Int>): Boolean {
        removeIds.forEach { removeId ->
            if(!ledgers.removeIf { it.id == removeId }) {
                Timber.d("do remove Ledgers item not found[$removeId]")
                return false
            }else{
                Timber.d("do remove Ledgers item success[$removeId]")
            }
        }
        return true
    }
}