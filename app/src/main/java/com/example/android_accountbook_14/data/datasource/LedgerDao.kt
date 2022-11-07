package com.example.android_accountbook_14.data.datasource

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.android_accountbook_14.data.entity.ColorLabelEntity
import com.example.android_accountbook_14.data.entity.LedgerEntity
import com.example.android_accountbook_14.data.entity.LedgerWithLabelEntity
import com.example.android_accountbook_14.data.entity.TextLabelEntity

@Dao
interface LedgerDao {

    @Query("insert into ${LedgerEntity.TABLE_NAME} " +
            "(" +
            "${LedgerEntity.COLUMN_NAME_YEAR}, " +
            "${LedgerEntity.COLUMN_NAME_MONTH}, " +
            "${LedgerEntity.COLUMN_NAME_DAY}, " +
            "${LedgerEntity.COLUMN_NAME_DAY_OF_WEEK}, " +
            "${LedgerEntity.COLUMN_NAME_PRICE}, " +
            "${LedgerEntity.COLUMN_NAME_COLOR_TAG_ID}, " +
            "${LedgerEntity.COLUMN_NAME_TEXT_TAG_ID}, " +
            "${LedgerEntity.COLUMN_NAME_CONTENT} " +
            ") values (" +
            ":year, :month, :day, :dayOfWeek, :price, :tagId, :paymentId, :content" +
            ")")
    suspend fun insertLedger(
        year: Int,
        month: Int,
        day: Int,
        dayOfWeek: String,
        price: Long,
        tagId: Int,
        paymentId: Int?,
        content: String?
    ): Long

    @Query("update ${LedgerEntity.TABLE_NAME} set " +
            "${LedgerEntity.COLUMN_NAME_YEAR} = :year, " +
            "${LedgerEntity.COLUMN_NAME_MONTH} = :month, " +
            "${LedgerEntity.COLUMN_NAME_DAY} = :day, " +
            "${LedgerEntity.COLUMN_NAME_DAY_OF_WEEK} = :dayOfWeek, " +
            "${LedgerEntity.COLUMN_NAME_PRICE} = :price, " +
            "${LedgerEntity.COLUMN_NAME_COLOR_TAG_ID} = :tagId, " +
            "${LedgerEntity.COLUMN_NAME_TEXT_TAG_ID} = :paymentId, " +
            "${LedgerEntity.COLUMN_NAME_CONTENT} = :content " +
            "where ${LedgerEntity.COLUMN_NAME_ID} = :id"
    )
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
    ): Int

    @Transaction
    @Query("SELECT * " +
                "FROM ${LedgerEntity.TABLE_NAME} " +
                "LEFT JOIN ${ColorLabelEntity.TABLE_NAME} ON ${LedgerEntity.COLUMN_NAME_COLOR_TAG_ID} = ${ColorLabelEntity.COLUMN_NAME_ID} " +
                "LEFT JOIN ${TextLabelEntity.TABLE_NAME} ON ${LedgerEntity.COLUMN_NAME_TEXT_TAG_ID} = ${TextLabelEntity.COLUMN_NAME_ID} " +
                "WHERE ${LedgerEntity.COLUMN_NAME_YEAR} = :year AND ${LedgerEntity.COLUMN_NAME_MONTH} = :month " +
                "ORDER BY ${LedgerEntity.COLUMN_NAME_DAY} DESC"
    )
    suspend fun fetchLedgers(year: Int, month: Int): List<LedgerWithLabelEntity>


    @Query("DELETE FROM ${LedgerEntity.TABLE_NAME} WHERE ${LedgerEntity.COLUMN_NAME_ID} in (:removeIds)")
    suspend fun removeLedges(removeIds: List<Int>): Int
}