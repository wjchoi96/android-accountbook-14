package com.example.android_accountbook_14.data.datasourceimpl

object DefaultDaoDataConstant {
    val defaultPayment = mutableListOf(
        "국민카드",
        "카카오뱅크 체크카드"
    )
    val defaultIncomeLabels = mutableListOf(
        Triple("미분류", "#EBC374", 1),
        Triple("월급", "#9BD182", 1),
        Triple("용돈", "#A3CB7A", 1),
        Triple("기타", "#B5CC7A", 1)
    )

    val defaultSpendLabels = mutableListOf(
        Triple("미분류", "#4CA1DE", -1),
        Triple("교통", "#4A6CC3", -1),
        Triple("문화/여가", "#2E86C7", -1),
        Triple("생활", "#48C2E9", -1),
        Triple("쇼핑/뷰티", "#6ED5EB", -1),
        Triple("식비", "#9FE7C8", -1),
        Triple("의료/건강", "#94D3CC", -1)
    )


    // dummy data for debug
    data class FakeLedgerInsertModel(
        val year: Int,
        val month: Int,
        val day: Int,
        val dayOfWeek: String,
        val price: Long,
        val tagId: Int,
        val paymentId: Int?,
        val content: String?
    )
    val fakeLedgerInsertData = mutableListOf(
        FakeLedgerInsertModel( 2022, 8, 1, "월", -1000, 5, 1, "식사"),
        FakeLedgerInsertModel( 2022, 8, 2, "화", -1100, 6, 2, "식사"),
        FakeLedgerInsertModel( 2022, 8, 4, "목", 1000, 4, 1, "보너스"),
        FakeLedgerInsertModel( 2022, 8, 11, "목", -10000, 7, 1, "식사"),
        FakeLedgerInsertModel( 2022, 8, 12, "금", -12000, 8, 1, "식사"),
        FakeLedgerInsertModel( 2022, 8, 12, "금", 2000, 1, 1, "보너스"),
        FakeLedgerInsertModel( 2022, 8, 12, "금", -3000, 9, 1, "식사"),
        FakeLedgerInsertModel( 2022, 8, 15, "월", -4000, 10, 2, "식사"),

        FakeLedgerInsertModel( 2022, 8, 21, "일", 1000000, 2, 2, "월급"),
        FakeLedgerInsertModel( 2022, 8, 22, "월", 600000, 3, 1, "보너스!"),
        FakeLedgerInsertModel( 2022, 8, 22, "월", -50000, 11, 2, "식사"),
        FakeLedgerInsertModel( 2022, 8, 28, "일", -100000, 10, 1, "식사"),
        FakeLedgerInsertModel( 2022, 8, 28, "일", 50000, 1, 2, "일당"),
        FakeLedgerInsertModel( 2022, 8, 29, "월", -100000, 5, 1, "식사"),
        FakeLedgerInsertModel( 2022, 8, 28, "일", -50000, 6, 2, "식사"),
        FakeLedgerInsertModel( 2022, 8, 29, "월", 1000000, 1, 1, "월급"),
        FakeLedgerInsertModel( 2022, 8, 27, "토", 2000, 4, 2, "개꿀"),
    )
}