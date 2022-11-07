package com.example.android_accountbook_14.di

import com.example.android_accountbook_14.domain.repository.LedgerRepository
import com.example.android_accountbook_14.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object LedgerUseCaseModule {

    @Provides
    fun provideFetchLedgerListItemsUseCase(
        ledgerRepository: LedgerRepository,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): FetchLedgerListItemsUseCase = FetchLedgerListItemsUseCase(ledgerRepository, dispatcher)

    @Provides
    fun provideInsertLedgerUseCase(
        ledgerRepository: LedgerRepository,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): InsertLedgerUserCase = InsertLedgerUserCase(ledgerRepository, dispatcher)

    @Provides
    fun provideUpdateLedgerUseCase(
        ledgerRepository: LedgerRepository,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): UpdateLedgerUseCase = UpdateLedgerUseCase(ledgerRepository, dispatcher)

    @Provides
    fun provideRemoveLedgersUseCase(
        ledgerRepository: LedgerRepository,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): RemoveLedgesUseCase = RemoveLedgesUseCase(ledgerRepository, dispatcher)


    @Provides
    fun provideFetchCalenderListUseCase(
        ledgerRepository: LedgerRepository,
        convertDayOfWeekUseCase: ConvertDayOfWeekUseCase,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): FetchCalenderLedgerListUseCase = FetchCalenderLedgerListUseCase(
        ledgerRepository,
        convertDayOfWeekUseCase,
        dispatcher
    )

    @Provides
    fun provideFetchLedgerMonthChartDataUseCase(
        ledgerRepository: LedgerRepository,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): FetchLedgerMonthChartDataUseCase = FetchLedgerMonthChartDataUseCase(ledgerRepository, dispatcher)
}