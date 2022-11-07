package com.example.android_accountbook_14.di

import com.example.android_accountbook_14.domain.repository.LabelRepository
import com.example.android_accountbook_14.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object LabelUseCaseModule {

    @Provides
    fun provideFetchBundleUseCase(
        labelRepository: LabelRepository,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): FetchLabelBundleUseCase = FetchLabelBundleUseCase(labelRepository, dispatcher)

    @Provides
    fun provideInsertIncomeLabelUseCase(
        labelRepository: LabelRepository,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): InsertIncomeLabelUseCase = InsertIncomeLabelUseCase(labelRepository, dispatcher)

    @Provides
    fun provideUpdateIncomeLabelUseCase(
        labelRepository: LabelRepository,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): UpdateIncomeLabelUseCase = UpdateIncomeLabelUseCase(labelRepository, dispatcher)

    @Provides
    fun provideInsertSpendLabelUseCase(
        labelRepository: LabelRepository,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): InsertSpendLabelUseCase = InsertSpendLabelUseCase(labelRepository, dispatcher)

    @Provides
    fun provideUpdateSpendLabelUseCase(
        labelRepository: LabelRepository,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): UpdateSpendLabelUseCase = UpdateSpendLabelUseCase(labelRepository, dispatcher)

    @Provides
    fun provideInsertPaymentUseCase(
        labelRepository: LabelRepository,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): InsertPaymentUseCase = InsertPaymentUseCase(labelRepository, dispatcher)

    @Provides
    fun provideUpdatePaymentUseCase(
        labelRepository: LabelRepository,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): UpdatePaymentUseCase = UpdatePaymentUseCase(labelRepository, dispatcher)

    @Provides
    fun provideFilterLedgersUseCase(
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): FilterLedgersUseCase = FilterLedgersUseCase(dispatcher)
}