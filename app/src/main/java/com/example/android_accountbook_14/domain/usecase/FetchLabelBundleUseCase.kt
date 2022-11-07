package com.example.android_accountbook_14.domain.usecase

import com.example.android_accountbook_14.di.DefaultDispatcher
import com.example.android_accountbook_14.domain.model.ColorLabel
import com.example.android_accountbook_14.domain.model.TextLabel
import com.example.android_accountbook_14.domain.repository.LabelRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchLabelBundleUseCase @Inject constructor(
    private val labelRepository: LabelRepository,
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        spendKey: String,
        incomeKey: String
    ): Result<Pair<Map<String, List<TextLabel>>, Map<String, List<ColorLabel>>>> {
        val bundleRes = withContext(CoroutineScope(coroutineDispatcher).coroutineContext) {
            labelRepository.fetchLabelsBundle()
        }
        return kotlin.runCatching {
            bundleRes.getOrThrow().let {
                val textLabels = mapOf("결제수단" to it.paymentLabels)
                val colorLabels = mapOf(
                    spendKey to it.spendColorLabels,
                    incomeKey to it.incomeColorLabels
                )
                textLabels to colorLabels
            }
        }
    }

}