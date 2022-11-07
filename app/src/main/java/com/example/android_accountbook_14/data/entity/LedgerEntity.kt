package com.example.android_accountbook_14.data.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.example.android_accountbook_14.domain.model.LedgerModel

@Entity(
    tableName = LedgerEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = TextLabelEntity::class,
            parentColumns = [TextLabelEntity.COLUMN_NAME_ID],
            childColumns = [LedgerEntity.COLUMN_NAME_TEXT_TAG_ID],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = ColorLabelEntity::class,
            parentColumns = [ColorLabelEntity.COLUMN_NAME_ID],
            childColumns = [LedgerEntity.COLUMN_NAME_COLOR_TAG_ID],
            onDelete = CASCADE
        )
    ]
)
data class LedgerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_NAME_ID) val id: Int,
    @ColumnInfo(name = COLUMN_NAME_YEAR) val year: Int,
    @ColumnInfo(name = COLUMN_NAME_MONTH) val month: Int,
    @ColumnInfo(name = COLUMN_NAME_DAY) val day: Int,
    @ColumnInfo(name = COLUMN_NAME_DAY_OF_WEEK) val dayOfWeek: String,
    @ColumnInfo(name = COLUMN_NAME_PRICE) val price: Long,
    @ColumnInfo(name = COLUMN_NAME_COLOR_TAG_ID) val colorLabelId: Int,
    @ColumnInfo(name = COLUMN_NAME_TEXT_TAG_ID) val paymentId: Int?,
    @ColumnInfo(name = COLUMN_NAME_CONTENT) val content: String?
) {
    companion object {
        const val TABLE_NAME = "ledger_table"

        const val COLUMN_NAME_ID = "ledger_id"
        const val COLUMN_NAME_YEAR = "ledger_year"
        const val COLUMN_NAME_MONTH = "ledger_month"
        const val COLUMN_NAME_DAY = "ledger_day"
        const val COLUMN_NAME_DAY_OF_WEEK = "ledger_day_of_week"
        const val COLUMN_NAME_PRICE = "ledger_price"
        const val COLUMN_NAME_COLOR_TAG_ID = "ledger_color_tag_id"
        const val COLUMN_NAME_TEXT_TAG_ID = "ledger_text_tag_id"
        const val COLUMN_NAME_CONTENT = "ledger_content"
    }
}

data class LedgerWithLabelEntity(
    @Embedded
    val ledger: LedgerEntity,
    @Relation(
        parentColumn = LedgerEntity.COLUMN_NAME_COLOR_TAG_ID,
        entityColumn = ColorLabelEntity.COLUMN_NAME_ID
    )
    val colorLabel: ColorLabelEntity,
    @Relation(
        parentColumn = LedgerEntity.COLUMN_NAME_TEXT_TAG_ID,
        entityColumn = TextLabelEntity.COLUMN_NAME_ID
    )
    val textLabel: TextLabelEntity
){
    fun toDomain(): LedgerModel = LedgerModel(
        id = ledger.id,
        year = ledger.year,
        month = ledger.month,
        day =  ledger.day,
        dayOfWeek = ledger.dayOfWeek,
        price = ledger.price,
        tag = colorLabel.title,
        tagColorHex = colorLabel.color,
        payment = textLabel.title,
        content = ledger.content
    )
}
