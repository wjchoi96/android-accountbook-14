package com.example.android_accountbook_14.data.datasourceimpl

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.annotation.NonNull
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.android_accountbook_14.data.datasource.ColorLabelDao
import com.example.android_accountbook_14.data.datasource.LedgerDao
import com.example.android_accountbook_14.data.datasource.TextLabelDao
import com.example.android_accountbook_14.data.entity.ColorLabelEntity
import com.example.android_accountbook_14.data.entity.LedgerEntity
import com.example.android_accountbook_14.data.entity.TextLabelEntity
import timber.log.Timber


@Database(
    entities = [TextLabelEntity::class, ColorLabelEntity::class, LedgerEntity::class],
    version = 1,
    exportSchema = true, // Room 의 Schema 구조를 폴더로 Export 할 수 있습니다. 데이터베이스의 버전 히스토리를 기록할 수 있다는 점에서 true로 설정하는 것이 좋습니다
//    autoMigrations = [
//        AutoMigration(
//            from = 1,
//            to = 2
//        )
//    ]
)
abstract class AccountBookRoomDataBase: RoomDatabase() {
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AccountBookRoomDataBase? = null

        fun getDatabase(context: Context): AccountBookRoomDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AccountBookRoomDataBase::class.java,
                    "account_book_database_room1.db"
                )
                    .addCallback(CallBack())
//                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun textLabelDao(): TextLabelDao

    abstract fun colorLabelDao(): ColorLabelDao

    abstract fun ledgerDao(): LedgerDao

    @DeleteTable.Entries(
        DeleteTable(tableName = TextLabelEntity.TABLE_NAME),
        DeleteTable(tableName = ColorLabelEntity.TABLE_NAME),
        DeleteTable(tableName = LedgerEntity.TABLE_NAME),
    )
    class MyAutoMigration : AutoMigrationSpec

    class CallBack : RoomDatabase.Callback() {
        override fun onCreate(@NonNull db: SupportSQLiteDatabase) {
            Timber.d("onCreate room dataBase")
            super.onCreate(db)
//            db.beginTransaction()

            // default label data
            DefaultDaoDataConstant.defaultPayment.forEach {
                val textLabelValue = ContentValues().apply {
                    put(TextLabelEntity.COLUMN_NAME_TITLE, it)
                }
                val res = db.insert(TextLabelEntity.TABLE_NAME, SQLiteDatabase.CONFLICT_NONE, textLabelValue)
                Timber.d("insert payment $it => res[$res]")
            }
            DefaultDaoDataConstant.defaultIncomeLabels.forEach {
                val colorLabelValue = ContentValues().apply {
                    put(ColorLabelEntity.COLUMN_NAME_TITLE, it.first)
                    put(ColorLabelEntity.COLUMN_NAME_COLOR, it.second)
                    put(ColorLabelEntity.COLUMN_NAME_TYPE, it.third)
                }
                db.insert(ColorLabelEntity.TABLE_NAME, SQLiteDatabase.CONFLICT_NONE, colorLabelValue)
                Timber.d("insert income ${it.first}, ${it.third}")
            }
            DefaultDaoDataConstant.defaultSpendLabels.forEach {
                val colorLabelValue = ContentValues().apply {
                    put(ColorLabelEntity.COLUMN_NAME_TITLE, it.first)
                    put(ColorLabelEntity.COLUMN_NAME_COLOR, it.second)
                    put(ColorLabelEntity.COLUMN_NAME_TYPE, it.third)
                }
                db.insert(ColorLabelEntity.TABLE_NAME, SQLiteDatabase.CONFLICT_NONE, colorLabelValue)
                Timber.d("insert spend ${it.first}, ${it.third}")
            }

            //dummy data for test
            DefaultDaoDataConstant.fakeLedgerInsertData.forEach {
                val ledgerValue = ContentValues().apply {
                    put(LedgerEntity.COLUMN_NAME_YEAR, it.year)
                    put(LedgerEntity.COLUMN_NAME_MONTH, it.month)
                    put(LedgerEntity.COLUMN_NAME_DAY, it.day)
                    put(LedgerEntity.COLUMN_NAME_DAY_OF_WEEK, it.dayOfWeek)
                    put(LedgerEntity.COLUMN_NAME_PRICE, it.price)
                    put(LedgerEntity.COLUMN_NAME_COLOR_TAG_ID, it.tagId)
                    if(it.paymentId != null) put(LedgerEntity.COLUMN_NAME_TEXT_TAG_ID, it.paymentId)
                    if(it.content != null) put(LedgerEntity.COLUMN_NAME_CONTENT, it.content)
                }
                db.insert(LedgerEntity.TABLE_NAME, SQLiteDatabase.CONFLICT_NONE, ledgerValue)
                Timber.d("insert ledger ${it.year}/${it.month}/${it.day} => paymentId[${it.paymentId}]")
            }

//            db.endTransaction()
        }
    }
}
