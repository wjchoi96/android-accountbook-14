package com.example.android_accountbook_14.domain.usecase

import com.example.android_accountbook_14.di.DefaultDispatcher
import com.example.android_accountbook_14.domain.model.ColorLabel
import com.example.android_accountbook_14.domain.repository.LabelRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InsertIncomeLabelUseCase @Inject constructor(
    private val labelRepository: LabelRepository,
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(title: String, color: String): Result<Boolean>{
        return withContext(coroutineDispatcher){
            labelRepository.insertIncomeLabel(
                ColorLabel(0, title, color)
            )
        }
    }
}