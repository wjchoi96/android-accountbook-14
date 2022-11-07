package com.example.android_accountbook_14.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

object ColorUtil {

    fun convertToArgb(color: String): Int = Color(android.graphics.Color.parseColor(color)).toArgb()
    fun convertToArgb(color: Color): Int = color.toArgb()

    fun convertToComposeColor(color: String): Color = Color(android.graphics.Color.parseColor(color))

    fun convertToHexString(color: Color): String = "#${Integer.toHexString(color.toArgb())}"
}