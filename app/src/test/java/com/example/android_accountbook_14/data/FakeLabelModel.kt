package com.example.android_accountbook_14.data

import com.example.android_accountbook_14.data.entity.ColorLabelEntity
import com.example.android_accountbook_14.data.entity.TextLabelEntity

val getTextLabelEntity = TextLabelEntity(
    0, "text_label"
)

val getTextLabelEntities = listOf(
    getTextLabelEntity, getTextLabelEntity, getTextLabelEntity, getTextLabelEntity, getTextLabelEntity
)

val getIncomeLabelEntity = ColorLabelEntity(
    0, "color_label", "#FFFFFF", 1
)

val getSpendLabelEntity = ColorLabelEntity(
    1, "color_label", "#FFFFFF", -1
)

val getSpendLabelEntities = listOf(
     getSpendLabelEntity, getSpendLabelEntity
)

val getIncomeLabelEntities = listOf(
    getIncomeLabelEntity, getIncomeLabelEntity
)

val getColorLabelEntities = listOf(
    getIncomeLabelEntity, getSpendLabelEntity, getIncomeLabelEntity, getSpendLabelEntity
)
