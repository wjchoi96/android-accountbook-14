package com.example.android_accountbook_14.domain.model

import timber.log.Timber

data class LedgerMonthChartModel(
    val ledgerChardItems: List<LedgerChartItemModel>
) {
    val spend: Long
        get() = ledgerChardItems.sumOf { -it.spend }
    val percents: List<Pair<Float, LedgerChartItemModel>>
        get() {
            val list = mutableListOf<Pair<Float, LedgerChartItemModel>>()
            ledgerChardItems.forEach {
                val percent = (-it.spend.toDouble()/spend*100).toFloat()
                Timber.d("percent[total($spend)] ${it.tag} => $percent")
                Timber.d("${-it.spend.toDouble()/spend}, ${-it.spend.toDouble()/spend*100}")
                list.add(percent to it)
            }
            return list
        }
    val colors: List<String>
        get() {
            return ledgerChardItems.map{ it.tagColorHex }
        }

    data class LedgerChartItemModel(
        val spend: Long,
        val tag: String,
        val tagColorHex: String
    )
}
