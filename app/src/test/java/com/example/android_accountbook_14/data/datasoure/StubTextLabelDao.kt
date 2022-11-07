package com.example.android_accountbook_14.data.datasoure

import com.example.android_accountbook_14.data.entity.TextLabelEntity
import com.example.android_accountbook_14.data.datasource.TextLabelDao

class StubTextLabelDao(
    private val textLabels: MutableList<TextLabelEntity> = mutableListOf()
): TextLabelDao {

    override suspend fun fetchTextLabels(): List<TextLabelEntity> {
        return textLabels
    }

    // 실제로 업데이트가 에 대한 유효성 판단은 실제 DataSource 구현체가 담당할 일
    override suspend fun updateTextLabel(id: Int, title: String): Boolean {
        return true
    }

    // 실제로 Insert 에 대한 유효성 판단은 실제 DataSource 구현체가 담당할 일
    override suspend fun insertTextLabel(title: String): Boolean {
        return true
    }
}