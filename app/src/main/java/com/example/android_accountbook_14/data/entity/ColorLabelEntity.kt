package com.example.android_accountbook_14.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.android_accountbook_14.domain.model.ColorLabel

@Entity(
    tableName = ColorLabelEntity.TABLE_NAME,
    indices = [Index(value = [ColorLabelEntity.COLUMN_NAME_TITLE, ColorLabelEntity.COLUMN_NAME_TYPE], unique = true)]
)
data class ColorLabelEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_NAME_ID) val id: Int,
    @ColumnInfo(name = COLUMN_NAME_TITLE) val title: String,
    @ColumnInfo(name = COLUMN_NAME_COLOR) val color: String,
    @ColumnInfo(name = COLUMN_NAME_TYPE) val type: Int // 단순하게 소비라면 마이너스 행위니까 음수, 수입이면 양수로 처리
) {
    fun toDomain(): ColorLabel = ColorLabel(
        id = id,
        title = title,
        color = color
    )

    companion object {
        const val TABLE_NAME = "color_label_table"

        const val COLUMN_NAME_ID = "color_label_id"
        const val COLUMN_NAME_TITLE = "color_label_title"
        const val COLUMN_NAME_COLOR = "color_label_color"
        const val COLUMN_NAME_TYPE = "color_label_type"
    }
}