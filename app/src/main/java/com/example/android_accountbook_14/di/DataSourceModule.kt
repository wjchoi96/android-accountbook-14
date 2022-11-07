package com.example.android_accountbook_14.di

import com.example.android_accountbook_14.data.datasourceimpl.AccountBookDataBase
import com.example.android_accountbook_14.data.datasource.ColorLabelDao
import com.example.android_accountbook_14.data.datasource.LedgerDao
import com.example.android_accountbook_14.data.datasource.TextLabelDao
import com.example.android_accountbook_14.data.datasourceimpl.AccountBookRoomDataBase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//abstract class DataSourceModule {
//
//    @Binds
//    @Singleton
//    abstract fun provideColorLabelDao(impl: AccountBookDataBase): ColorLabelDao
//
//    @Binds
//    @Singleton
//    abstract fun provideTextLabelDao(impl: AccountBookDataBase): TextLabelDao
//
//    @Binds
//    @Singleton
//    abstract fun provideLedgerDao(impl: AccountBookDataBase): LedgerDao
//
//}

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideColorLabelDao(db: AccountBookRoomDataBase): ColorLabelDao = db.colorLabelDao()

    @Provides
    @Singleton
    fun provideTextLabelDao(db: AccountBookRoomDataBase): TextLabelDao = db.textLabelDao()

    @Provides
    @Singleton
    fun provideLedgerDao(db: AccountBookRoomDataBase): LedgerDao = db.ledgerDao()

}