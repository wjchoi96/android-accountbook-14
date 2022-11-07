package com.example.android_accountbook_14.domain.repository

import com.example.android_accountbook_14.domain.model.ColorLabel
import com.example.android_accountbook_14.domain.model.LabelsBundle
import com.example.android_accountbook_14.domain.model.TextLabel

interface LabelRepository {
    suspend fun fetchLabelsBundle(): Result<LabelsBundle>

    suspend fun insertPaymentLabel(label: TextLabel): Result<Boolean>
    suspend fun insertSpendLabel(label: ColorLabel): Result<Boolean>
    suspend fun insertIncomeLabel(label: ColorLabel): Result<Boolean>

    suspend fun updatePaymentLabel(label: TextLabel): Result<Boolean>
    suspend fun updateSpendLabel(label: ColorLabel): Result<Boolean>
    suspend fun updateIncomeLabel(label: ColorLabel): Result<Boolean>
}