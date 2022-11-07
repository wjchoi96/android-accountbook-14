package com.example.android_accountbook_14.data.datasource

import androidx.room.Dao
import androidx.room.Query
import com.example.android_accountbook_14.data.entity.TextLabelEntity

@Dao
interface TextLabelDao {
    @Query("select * from ${TextLabelEntity.TABLE_NAME}")
    suspend fun fetchTextLabels(): List<TextLabelEntity>

    @Query("update ${TextLabelEntity.TABLE_NAME} set " +
            "${TextLabelEntity.COLUMN_NAME_TITLE} = :title " +
            "where ${TextLabelEntity.COLUMN_NAME_ID} = :id")
    suspend fun updateTextLabel(id: Int, title: String): Int

    @Query("insert into ${TextLabelEntity.TABLE_NAME} (${TextLabelEntity.COLUMN_NAME_TITLE}) values (:title)")
    suspend fun insertTextLabel(title: String): Long
}