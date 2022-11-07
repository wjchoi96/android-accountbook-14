package com.example.android_accountbook_14.domain.model

data class LabelsBundle(
    val paymentLabels: List<TextLabel>,
    val spendColorLabels: List<ColorLabel>,
    val incomeColorLabels: List<ColorLabel>
) {
}