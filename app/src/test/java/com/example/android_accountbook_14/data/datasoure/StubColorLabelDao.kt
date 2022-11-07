package com.example.android_accountbook_14.data.datasoure

import com.example.android_accountbook_14.data.entity.ColorLabelEntity
import com.example.android_accountbook_14.data.datasource.ColorLabelDao

class StubColorLabelDao(
    private val colorLabels: MutableList<ColorLabelEntity> = mutableListOf()
): ColorLabelDao {

    override suspend fun fetchColorLabels(): List<ColorLabelEntity> {
        return colorLabels
    }

    // 실제로 업데이트가 에 대한 유효성 판단은 실제 DataSource 구현체가 담당할 일
    override suspend fun updateColorLabel(id: Int, title: String, colorHex: String, type: Int): Boolean {
        return true
    }

    // 실제로 Insert 에 대한 유효성 판단은 실제 DataSource 구현체가 담당할 일
    override suspend fun insertColorLabel(title: String, colorHex: String, type: Int): Boolean {
        return true
    }
}