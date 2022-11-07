package com.example.android_accountbook_14.domain.usecase

import com.example.android_accountbook_14.di.DefaultDispatcher
import com.example.android_accountbook_14.domain.model.TextLabel
import com.example.android_accountbook_14.domain.repository.LabelRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdatePaymentUseCase @Inject constructor(
    private val labelRepository: LabelRepository,
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(newLabel: TextLabel): Result<Boolean>{
        return withContext(coroutineDispatcher){
            labelRepository.updatePaymentLabel(
                newLabel
            )
        }
    }
}