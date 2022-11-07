package com.example.android_accountbook_14.di

import com.example.android_accountbook_14.data.repsoitory.LabelRepositoryImpl
import com.example.android_accountbook_14.data.repsoitory.LedgerRepositoryImpl
import com.example.android_accountbook_14.domain.repository.LabelRepository
import com.example.android_accountbook_14.domain.repository.LedgerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideLabelRepository(impl: LabelRepositoryImpl): LabelRepository

    @Binds
    abstract fun provideLedgerRepository(impl: LedgerRepositoryImpl): LedgerRepository
}