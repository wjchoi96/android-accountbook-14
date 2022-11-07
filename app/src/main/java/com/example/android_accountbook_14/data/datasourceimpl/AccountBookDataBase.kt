package com.example.android_accountbook_14.data.datasourceimpl

import android.database.Cursor
import com.example.android_accountbook_14.data.datasource.ColorLabelDao
import com.example.android_accountbook_14.data.datasource.LedgerDao
import com.example.android_accountbook_14.data.datasource.TextLabelDao
import com.example.android_accountbook_14.data.entity.ColorLabelEntity
import com.example.android_accountbook_14.data.entity.LedgerEntity
import com.example.android_accountbook_14.data.entity.LedgerWithLabelEntity
import com.example.android_accountbook_14.data.entity.TextLabelEntity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Account Book DataBase Helper 클래스와 코드 분리의 이유는,
 * Dao 기능을 중점으로 수행하는 클래스와, 디비 조작을 중점으로 하는 클래스로 구분을 하고 싶었기 때문이다
 *
 * 인터페이스로 구분까지 할 필요는 없다고 판단. 사실상 하나로 합쳐도 무방한 클래스 분리이지만, 코드의 가독성을 위해
 * 분리 시도
 */
@Singleton
class AccountBookDataBase @Inject constructor(
    private val dbHelper: AccountBookDataBaseHelper
): LedgerDao, TextLabelDao, ColorLabelDao {

    init {
        Timber.d("db helper name ${dbHelper.databaseName}")
    }

    /**
     * text label
     */
    override suspend fun insertTextLabel(title: String): Long{
        val newTextLabelId = dbHelper.insertTextLabel(dbHelper.writableDatabase, title)
        Timber.d("db helper insert text $newTextLabelId")
        return newTextLabelId
    }

    override suspend fun fetchTextLabels(): List<TextLabelEntity>{
        val cursor = dbHelper.fetchTextLabels(dbHelper.readableDatabase)
        val list = mutableListOf<TextLabelEntity>()
        cursor.use {
            if (!cursor.moveToFirst())
                return@use
            do {
                val item = parseTextLabel(cursor, TextLabelEntity.COLUMN_NAME_ID) ?: continue
                list.add(item)
            }while(cursor.moveToNext())
        }
        return list
    }

    override suspend fun updateTextLabel(id: Int, title: String): Int {
        return dbHelper.updateTextLabel(
            dbHelper.writableDatabase,
            id,
            title
        )
    }

    /**
     * color label
     */
    override suspend fun insertColorLabel(
        title: String,
        colorHex: String,
        type: Int
    ): Long{
        val newColorLabelId = dbHelper.insertColorLabel(
            dbHelper.writableDatabase, title, colorHex, type
        )
        Timber.d("db helper insert colorLabel $newColorLabelId")
        return newColorLabelId
    }

    override suspend fun fetchColorLabels(): List<ColorLabelEntity>{
        val cursor = dbHelper.fetchColorLabels(dbHelper.readableDatabase)
        val list = mutableListOf<ColorLabelEntity>()
        cursor.use {
            if (!cursor.moveToFirst())
                return@use
            do {
                val item = parseColorLabel(cursor, ColorLabelEntity.COLUMN_NAME_ID) ?: continue
                list.add(item)
            }while(cursor.moveToNext())
        }
        return list
    }

    override suspend fun updateColorLabel(
        id: Int,
        title: String,
        colorHex: String,
        type: Int
    ): Int {
        return dbHelper.updateColorLabel(
            dbHelper.writableDatabase, id, title, colorHex, type
        )
    }

    /**
     * ledger
     */
    override suspend fun insertLedger(
        year: Int,
        month: Int,
        day: Int,
        dayOfWeek: String,
        price: Long,
        tagId: Int,
        paymentId: Int?,
        content: String?
    ): Long{
        val newLedgerId = dbHelper.insertLedgerQuery(
            dbHelper.writableDatabase, year, month, day, dayOfWeek, price, tagId, paymentId, content
        )
        Timber.d("db helper insert ledger $newLedgerId")
        return 0L
    }

    override suspend fun fetchLedgers(year: Int, month: Int): List<LedgerWithLabelEntity>{
        val cursor = dbHelper.fetchLedgers(dbHelper.readableDatabase, year, month)
        val list = mutableListOf<LedgerWithLabelEntity>()
        cursor.use {
            if(!cursor.moveToFirst())
                return@use
            do {
                val item = parseLedger(cursor) ?: continue
                list.add(item)
            }while (cursor.moveToNext())
        }
        Timber.d("db helper select res size => ${list.size}")
        return list
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
    ): Int {
        return dbHelper.updateLedger(
            dbHelper.writableDatabase,
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
    }

    override suspend fun removeLedges(removeIds: List<Int>): Int {
        return dbHelper.removeLedgers(
            dbHelper.writableDatabase, removeIds
        )
    }



    private fun parseLedger(cursor: Cursor): LedgerWithLabelEntity? {
        return try {
//            LedgerEntity(
//                cursor.getInt(cursor.getColumnIndex(LedgerEntity.COLUMN_NAME_ID)),
//                cursor.getInt(cursor.getColumnIndex(LedgerEntity.COLUMN_NAME_YEAR)),
//                cursor.getInt(cursor.getColumnIndex(LedgerEntity.COLUMN_NAME_MONTH)),
//                cursor.getInt(cursor.getColumnIndex(LedgerEntity.COLUMN_NAME_DAY)),
//                cursor.getString(cursor.getColumnIndex(LedgerEntity.COLUMN_NAME_DAY_OF_WEEK)),
//                cursor.getLong(cursor.getColumnIndex(LedgerEntity.COLUMN_NAME_PRICE)),
//
//                cursor.getInt(cursor.getColumnIndex(LedgerEntity.COLUMN_NAME_PRICE)),
//                cursor.getString(cursor.getColumnIndex(ColorLabelEntity.COLUMN_NAME_TITLE)),
//                cursor.getString(cursor.getColumnIndex(ColorLabelEntity.COLUMN_NAME_COLOR)),
//                cursor.getInt(cursor.getColumnIndex(ColorLabelEntity.COLUMN_NAME_TYPE)),
//
//                if( cursor.getColumnIndex(LedgerEntity.COLUMN_NAME_TEXT_TAG_ID) >= 0) {
//                    cursor.getInt(cursor.getColumnIndex(LedgerEntity.COLUMN_NAME_TEXT_TAG_ID))
//                }else
//                    null,
//
//                if( cursor.getColumnIndex(TextLabelEntity.COLUMN_NAME_TITLE) >= 0) {
//                    cursor.getString(cursor.getColumnIndex(TextLabelEntity.COLUMN_NAME_TITLE))
//                }else
//                    null,
//
//                if( cursor.getColumnIndex(LedgerEntity.COLUMN_NAME_CONTENT) >= 0)
//                    cursor.getString(cursor.getColumnIndex(LedgerEntity.COLUMN_NAME_CONTENT))
//                else
//                    null
//            )
            null
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    private fun parseTextLabel(cursor: Cursor, idKey: String): TextLabelEntity? {
        return try {
            TextLabelEntity(
                cursor.getInt(cursor.getColumnIndex(idKey)),
                cursor.getString(cursor.getColumnIndex(TextLabelEntity.COLUMN_NAME_TITLE)),
            )
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
    private fun parseColorLabel(cursor: Cursor, idKey: String): ColorLabelEntity? {
        return try {
            ColorLabelEntity(
                cursor.getInt(cursor.getColumnIndex(idKey)),
                cursor.getString(cursor.getColumnIndex(ColorLabelEntity.COLUMN_NAME_TITLE)),
                cursor.getString(cursor.getColumnIndex(ColorLabelEntity.COLUMN_NAME_COLOR)),
                cursor.getInt(cursor.getColumnIndex(ColorLabelEntity.COLUMN_NAME_TYPE))
            )
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}