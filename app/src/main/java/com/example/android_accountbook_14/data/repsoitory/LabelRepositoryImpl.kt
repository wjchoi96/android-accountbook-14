package com.example.android_accountbook_14.data.repsoitory

import com.example.android_accountbook_14.di.IODispatcher
import com.example.android_accountbook_14.data.datasource.ColorLabelDao
import com.example.android_accountbook_14.data.datasource.TextLabelDao
import com.example.android_accountbook_14.domain.model.ColorLabel
import com.example.android_accountbook_14.domain.model.LabelsBundle
import com.example.android_accountbook_14.domain.model.TextLabel
import com.example.android_accountbook_14.domain.repository.LabelRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class LabelRepositoryImpl @Inject constructor(
    private val textLabelDao: TextLabelDao,
    private val colorLabelDao: ColorLabelDao,
    @IODispatcher private val coroutineDispatcher: CoroutineDispatcher
): LabelRepository {

    override suspend fun fetchLabelsBundle(): Result<LabelsBundle> {
        Timber.d("fetch labels bundle")
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                val textLabelsDeffer = async { textLabelDao.fetchTextLabels().map { it.toDomain() } }
                val colorLabelsDeffer = async { colorLabelDao.fetchColorLabels() }
                val colorLabels = colorLabelsDeffer.await()

                LabelsBundle(
                    textLabelsDeffer.await(),
                    colorLabels
                        .filter{ it.type < 0 }
                        .map { it.toDomain() },
                    colorLabels
                        .filter{ it.type > 0 }
                        .map { it.toDomain() }
                )
            }
        }
    }

    override suspend fun insertPaymentLabel(label: TextLabel): Result<Boolean> {
        return kotlin.runCatching {
            val res = withContext(coroutineDispatcher){
                textLabelDao.insertTextLabel(label.title)
            }
            res >= 0L
        }
    }

    override suspend fun insertSpendLabel(label: ColorLabel): Result<Boolean> {
        return kotlin.runCatching {
            val res = withContext(coroutineDispatcher){
                colorLabelDao.insertColorLabel(label.title, label.color, -1)
            }
            res >= 0L
        }
    }

    override suspend fun insertIncomeLabel(label: ColorLabel): Result<Boolean> {
        return kotlin.runCatching {
            val res = withContext(coroutineDispatcher){
                colorLabelDao.insertColorLabel(label.title, label.color, 1)
            }
            res >= 0L
        }
    }

    override suspend fun updatePaymentLabel(label: TextLabel): Result<Boolean> {
        return runCatching {
            val res = withContext(coroutineDispatcher){
                textLabelDao.updateTextLabel(label.id, label.title)
            }
            res > 0
        }
    }

    override suspend fun updateSpendLabel(label: ColorLabel): Result<Boolean> {
        return runCatching {
            val res = withContext(coroutineDispatcher){
                colorLabelDao.updateColorLabel(label.id, label.title, label.color, -1)
            }
            res > 0
        }
    }

    override suspend fun updateIncomeLabel(label: ColorLabel): Result<Boolean> {
        return runCatching {
            val res = withContext(coroutineDispatcher){
                colorLabelDao.updateColorLabel(label.id, label.title, label.color, 1)
            }
            res > 0
        }
    }
}