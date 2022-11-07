package com.example.android_accountbook_14.domain.model

import java.io.Serializable

data class LedgerModel(
    val id: Int,
    val year: Int,
    val month: Int,
    val day: Int,
    val dayOfWeek: String,
    val price: Long,
    val tag: String,
    val tagColorHex: String,
    val payment: String?,
    val content: String?
): Serializable