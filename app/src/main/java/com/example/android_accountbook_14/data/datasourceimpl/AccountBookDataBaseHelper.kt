package com.example.android_accountbook_14.data.datasourceimpl

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.android_accountbook_14.data.entity.ColorLabelEntity
import com.example.android_accountbook_14.data.entity.LedgerEntity
import com.example.android_accountbook_14.data.entity.TextLabelEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountBookDataBaseHelper @Inject constructor(
    @ApplicationContext context: Context
): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "account_book_database.db"
        private const val DATABASE_VERSION = 49

        private const val SQL_CREATE_COLOR_LABEL_TABLE = "CREATE TABLE if not exists ${ColorLabelEntity.TABLE_NAME} (" +
                "${ColorLabelEntity.COLUMN_NAME_ID} INTEGER PRIMARY KEY autoincrement NOT NULL," +
                "${ColorLabelEntity.COLUMN_NAME_TITLE} TEXT NOT NULL," +
                "${ColorLabelEntity.COLUMN_NAME_COLOR} TEXT NOT NULL," +
                "${ColorLabelEntity.COLUMN_NAME_TYPE} INTEGER NOT NULL," +
                "unique(${ColorLabelEntity.COLUMN_NAME_TITLE}, ${ColorLabelEntity.COLUMN_NAME_TYPE}))"


        private const val SQL_CREATE_TEXT_LABEL_TABLE = "CREATE TABLE if not exists ${TextLabelEntity.TABLE_NAME} (" +
                "${TextLabelEntity.COLUMN_NAME_ID} INTEGER PRIMARY KEY autoincrement NOT NULL," +
                "${TextLabelEntity.COLUMN_NAME_TITLE} TEXT NOT NULL unique)"

        private const val SQL_CREATE_LEDGER_TABLE = "CREATE TABLE if not exists ${LedgerEntity.TABLE_NAME} (" +
                "${LedgerEntity.COLUMN_NAME_ID} INTEGER PRIMARY KEY autoincrement NOT NULL," +
                "${LedgerEntity.COLUMN_NAME_YEAR} INTEGER NOT NULL," +
                "${LedgerEntity.COLUMN_NAME_MONTH} INTEGER NOT NULL," +
                "${LedgerEntity.COLUMN_NAME_DAY} INTEGER NOT NULL," +
                "${LedgerEntity.COLUMN_NAME_DAY_OF_WEEK} TEXT NOT NULL," +
                "${LedgerEntity.COLUMN_NAME_PRICE} INTEGER NOT NULL," +
                "${LedgerEntity.COLUMN_NAME_COLOR_TAG_ID} INTEGER NOT NULL," +
                "${LedgerEntity.COLUMN_NAME_TEXT_TAG_ID} INTEGER," +
                "${LedgerEntity.COLUMN_NAME_CONTENT} TEXT," +

                "constraint ${ColorLabelEntity.COLUMN_NAME_ID} foreign key (${LedgerEntity.COLUMN_NAME_COLOR_TAG_ID}) " +
                "references ${ColorLabelEntity.TABLE_NAME}(${LedgerEntity.COLUMN_NAME_COLOR_TAG_ID})," +

                "constraint ${TextLabelEntity.COLUMN_NAME_ID} foreign key (${LedgerEntity.COLUMN_NAME_TEXT_TAG_ID}) " +
                "references ${TextLabelEntity.TABLE_NAME}(${LedgerEntity.COLUMN_NAME_TEXT_TAG_ID}))"

        private const val SQL_DELETE_ENTRIES_LEDGER = "DROP TABLE IF EXISTS ${LedgerEntity.TABLE_NAME}"
        private const val SQL_DELETE_ENTRIES_COLOR_LABEL = "DROP TABLE IF EXISTS ${ColorLabelEntity.TABLE_NAME}"
        private const val SQL_DELETE_ENTRIES_TEXT_LABEL = "DROP TABLE IF EXISTS ${TextLabelEntity.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Timber.d("db helper onCreate")
        db?.let {
            it.execSQL(SQL_CREATE_COLOR_LABEL_TABLE)
            it.execSQL(SQL_CREATE_TEXT_LABEL_TABLE)
            it.execSQL(SQL_CREATE_LEDGER_TABLE)
        }
        db?.let { database ->
            try {
                database.beginTransaction()

                DefaultDaoDataConstant.defaultPayment.forEach {
                    insertTextLabel(database, it)
                    Timber.d("insert payment $it")
                }
                DefaultDaoDataConstant.defaultIncomeLabels.forEach {
                    insertColorLabel(database, it.first, it.second, it.third)
                    Timber.d("insert income ${it.first}, ${it.third}")
                }
                DefaultDaoDataConstant.defaultSpendLabels.forEach {
                    insertColorLabel(database, it.first, it.second, it.third)
                    Timber.d("insert spend ${it.first}, ${it.third}")
                }

                // dummy data code for debug
                DefaultDaoDataConstant.fakeLedgerInsertData.forEach {
                    insertLedgerQuery(
                        database,
                        it.year,
                        it.month,
                        it.day,
                        it.dayOfWeek,
                        it.price,
                        it.tagId,
                        it.paymentId,
                        it.content
                    )
                    Timber.d("insert ledger ${it.year}/${it.month}/${it.day}")
                }

                database.setTransactionSuccessful()
            }catch (e: Exception){
                e.printStackTrace()
            }finally {
                database.endTransaction()
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, old: Int, new: Int) {
        db?.let {
            it.execSQL(SQL_DELETE_ENTRIES_LEDGER)
            it.execSQL(SQL_DELETE_ENTRIES_COLOR_LABEL)
            it.execSQL(SQL_DELETE_ENTRIES_TEXT_LABEL)
        }
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, old: Int, new: Int) {
        onUpgrade(db, old, new)
    }

    /**
     * text label
     */
    fun insertTextLabel(db: SQLiteDatabase, title: String): Long{
        val textLabelValue = ContentValues().apply {
            put(TextLabelEntity.COLUMN_NAME_TITLE, title)
        }
        return db.insert(TextLabelEntity.TABLE_NAME, null, textLabelValue)
    }

    fun fetchTextLabels(db: SQLiteDatabase): Cursor {
        return db.rawQuery("SELECT * FROM ${TextLabelEntity.TABLE_NAME}", arrayOf())
    }

    fun updateTextLabel(
        db: SQLiteDatabase,
        id: Int,
        title: String
    ): Int {
        val values = ContentValues().apply {
            put(TextLabelEntity.COLUMN_NAME_TITLE, title)
        }
        val selection = "${TextLabelEntity.COLUMN_NAME_ID} LIKE ?"
        val selectionArgs = arrayOf("$id")
        val count = db.update(
            TextLabelEntity.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
        return count
    }

    /**
     * color label
     */
    fun insertColorLabel(
        db: SQLiteDatabase,
        title: String,
        colorHex: String,
        type: Int
    ): Long{
        val colorLabelValue = ContentValues().apply {
            put(ColorLabelEntity.COLUMN_NAME_TITLE, title)
            put(ColorLabelEntity.COLUMN_NAME_COLOR, colorHex)
            put(ColorLabelEntity.COLUMN_NAME_TYPE, type)
        }
        return db.insert(ColorLabelEntity.TABLE_NAME, null, colorLabelValue)
    }

    fun fetchColorLabels(db: SQLiteDatabase): Cursor {
        return db.rawQuery("SELECT * FROM ${ColorLabelEntity.TABLE_NAME}", arrayOf())
    }

    fun updateColorLabel(
        db: SQLiteDatabase,
        id: Int,
        title: String,
        colorHex: String,
        type: Int
    ): Int {
        val values = ContentValues().apply {
            put(ColorLabelEntity.COLUMN_NAME_TITLE, title)
            put(ColorLabelEntity.COLUMN_NAME_COLOR, colorHex)
        }
        val selection = "${ColorLabelEntity.COLUMN_NAME_ID} LIKE ? AND ${ColorLabelEntity.COLUMN_NAME_TYPE} LIKE ?"
        val selectionArgs = arrayOf("$id", "$type")
        val count = db.update(
            ColorLabelEntity.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
        return count
    }

    /**
     * ledgers
     */
    fun insertLedgerQuery(
        db: SQLiteDatabase,
        year: Int,
        month: Int,
        day: Int,
        dayOfWeek: String,
        price: Long,
        colorLabelId: Int,
        textLabelId: Int?,
        content: String?
    ): Long {
        val ledgerValue = ContentValues().apply {
            put(LedgerEntity.COLUMN_NAME_YEAR, year)
            put(LedgerEntity.COLUMN_NAME_MONTH, month)
            put(LedgerEntity.COLUMN_NAME_DAY, day)
            put(LedgerEntity.COLUMN_NAME_DAY_OF_WEEK, dayOfWeek)
            put(LedgerEntity.COLUMN_NAME_PRICE, price)
            put(LedgerEntity.COLUMN_NAME_COLOR_TAG_ID, colorLabelId)
            if(textLabelId != null) put(LedgerEntity.COLUMN_NAME_TEXT_TAG_ID, textLabelId)
            if(content != null) put(LedgerEntity.COLUMN_NAME_CONTENT, content)
        }
        return db.insert(LedgerEntity.TABLE_NAME, null, ledgerValue)
    }

    fun fetchLedgers(db: SQLiteDatabase, year: Int, month: Int): Cursor{
        return db.rawQuery(selectLedgerRawQuery(year, month), arrayOf())
    }

    fun updateLedger(
        db: SQLiteDatabase,
        id: Int,
        year: Int,
        month: Int,
        day: Int,
        dayOfWeek: String,
        price: Long,
        colorLabelId: Int,
        textLabelId: Int?,
        content: String?
    ): Int {
        val values = ContentValues().apply {
            put(LedgerEntity.COLUMN_NAME_YEAR, year)
            put(LedgerEntity.COLUMN_NAME_MONTH, month)
            put(LedgerEntity.COLUMN_NAME_DAY, day)
            put(LedgerEntity.COLUMN_NAME_DAY_OF_WEEK, dayOfWeek)
            put(LedgerEntity.COLUMN_NAME_PRICE, price)
            put(LedgerEntity.COLUMN_NAME_COLOR_TAG_ID, colorLabelId)
            if(textLabelId != null) put(LedgerEntity.COLUMN_NAME_TEXT_TAG_ID, textLabelId)
            if(!content.isNullOrBlank()) put(LedgerEntity.COLUMN_NAME_CONTENT, content)
        }
        val selection = "${LedgerEntity.COLUMN_NAME_ID} LIKE ?"
        val selectionArgs = arrayOf("$id")
        val count = db.update(
            LedgerEntity.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
        return count
    }

    fun removeLedgers(
        db: SQLiteDatabase,
        removeIds: List<Int>
    ): Int {
        val res = try {
            db.beginTransaction()
            var transactionResult = 0
            removeIds.forEach {
                val selection = "${LedgerEntity.COLUMN_NAME_ID} LIKE ?"
                val selectionArgs = arrayOf("$it")
                val deleteRow = db.delete(
                    LedgerEntity.TABLE_NAME,
                    selection,
                    selectionArgs
                )
                if(deleteRow >= 0)
                    transactionResult++
            }
            db.setTransactionSuccessful()
            transactionResult
        }catch (e: Exception){
            e.printStackTrace()
            -1
        }finally {
            db.endTransaction()
        }
        return res
    }

    private fun selectLedgerRawQuery(year: Int, month: Int): String{
        return "SELECT * " +
                "FROM ${LedgerEntity.TABLE_NAME} " +
                "LEFT JOIN ${ColorLabelEntity.TABLE_NAME} ON ${LedgerEntity.COLUMN_NAME_COLOR_TAG_ID} = ${ColorLabelEntity.COLUMN_NAME_ID} " +
                "LEFT JOIN ${TextLabelEntity.TABLE_NAME} ON ${LedgerEntity.COLUMN_NAME_TEXT_TAG_ID} = ${TextLabelEntity.COLUMN_NAME_ID} " +
                "WHERE ${LedgerEntity.COLUMN_NAME_YEAR} = $year AND ${LedgerEntity.COLUMN_NAME_MONTH} = $month " +
                "ORDER BY ${LedgerEntity.COLUMN_NAME_DAY} DESC"
    }
}