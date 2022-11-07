package com.example.android_accountbook_14.di

import android.app.Application
import android.content.Context
import com.example.android_accountbook_14.data.datasourceimpl.AccountBookDataBase
import com.example.android_accountbook_14.data.datasourceimpl.AccountBookDataBaseHelper
import com.example.android_accountbook_14.data.datasourceimpl.AccountBookRoomDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * App
 * DataSource
 * Repository
 * UseCase
 *
 * 흐름으로 의존성이 주입된다
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @ApplicationContext
    @Provides
    @Singleton
    fun provideApplicationContext(application: Application) = application

//    @Provides
//    @Singleton
//    fun provideDBHelper(@ApplicationContext context: Context) = AccountBookDataBaseHelper(context)
//
//    @Provides
//    @Singleton
//    fun provideAccountDataBase(dbHelper: AccountBookDataBaseHelper): AccountBookDataBase = AccountBookDataBase(dbHelper)

    @Provides
    @Singleton
    fun provideAccountBoomRoomDataBase(@ApplicationContext application: Application): AccountBookRoomDataBase =
        AccountBookRoomDataBase.getDatabase(application)
}