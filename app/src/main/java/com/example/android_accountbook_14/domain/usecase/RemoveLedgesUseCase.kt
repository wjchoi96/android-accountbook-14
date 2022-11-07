package com.example.android_accountbook_14.domain.usecase

import com.example.android_accountbook_14.di.DefaultDispatcher
import com.example.android_accountbook_14.domain.repository.LedgerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoveLedgesUseCase @Inject constructor(
    private val ledgerRepository: LedgerRepository,
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(removeIds: List<Int>): Result<Boolean>{
        return withContext(coroutineDispatcher){
            ledgerRepository.removeLedgers(
                removeIds
            )
        }
    }
}