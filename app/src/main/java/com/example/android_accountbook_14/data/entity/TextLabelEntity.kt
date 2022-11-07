package com.example.android_accountbook_14.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.android_accountbook_14.domain.model.TextLabel

@Entity(
    tableName = TextLabelEntity.TABLE_NAME,
    indices = [Index(value = [TextLabelEntity.COLUMN_NAME_TITLE], unique = true)]
)
data class TextLabelEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_NAME_ID) val id: Int,
    @ColumnInfo(name = COLUMN_NAME_TITLE) val title: String
) {
    fun toDomain(): TextLabel = TextLabel(
        id = id,
        title = title
    )
    companion object {
        const val TABLE_NAME = "text_label_table"

        const val COLUMN_NAME_ID = "text_label_id"
        const val COLUMN_NAME_TITLE = "text_label_title"
    }
}