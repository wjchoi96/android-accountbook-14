package com.example.android_accountbook_14.data.datasource

import androidx.room.Dao
import androidx.room.Query
import com.example.android_accountbook_14.data.entity.ColorLabelEntity

@Dao
interface ColorLabelDao {
    @Query("select * from ${ColorLabelEntity.TABLE_NAME}")
    suspend fun fetchColorLabels(): List<ColorLabelEntity>

    @Query("update ${ColorLabelEntity.TABLE_NAME} set " +
            "${ColorLabelEntity.COLUMN_NAME_TITLE} = :title, ${ColorLabelEntity.COLUMN_NAME_COLOR} = :colorHex " +
            "where ${ColorLabelEntity.COLUMN_NAME_ID} = :id and ${ColorLabelEntity.COLUMN_NAME_TYPE} = :type")
    suspend fun updateColorLabel(
        id: Int,
        title: String,
        colorHex: String,
        type: Int
    ): Int

    @Query("insert into ${ColorLabelEntity.TABLE_NAME} " +
            "(${ColorLabelEntity.COLUMN_NAME_TITLE}, ${ColorLabelEntity.COLUMN_NAME_COLOR}, ${ColorLabelEntity.COLUMN_NAME_TYPE}) " +
            "values (:title, :colorHex, :type)")
    suspend fun insertColorLabel(
        title: String,
        colorHex: String,
        type: Int
    ): Long
}