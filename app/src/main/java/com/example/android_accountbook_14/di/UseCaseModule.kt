package com.example.android_accountbook_14.di

import com.example.android_accountbook_14.domain.usecase.ConvertDayOfWeekUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideConvertDayOfWeekUseCase(): ConvertDayOfWeekUseCase = ConvertDayOfWeekUseCase()
}