package com.example.android_accountbook_14.domain.model

import java.io.Serializable

data class ColorLabel(
    val id: Int,
    val title: String,
    val color: String
): Serializable