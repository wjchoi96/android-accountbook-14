package com.example.android_accountbook_14.domain.model

data class LedgerListItemModel(
    val year: Int,
    val month: Int,
    val day: Int,
    val dayOfWeek: String,
    val dayLedgers: List<LedgerModel>
){
    val date: String
        get() = "${month}월 ${day}일 $dayOfWeek"
    val income: Long
        get() = dayLedgers.filter { it.price > 0 }.sumOf { it.price }
    val spend: Long
        get() = dayLedgers.filter { it.price < 0 }.sumOf { -it.price }
}

