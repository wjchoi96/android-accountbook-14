//package com.example.android_accountbook_14.data.datasourceimpl
//
//import com.example.android_accountbook_14.data.entity.ColorLabelEntity
//import com.example.android_accountbook_14.data.entity.LedgerEntity
//import com.example.android_accountbook_14.data.entity.TextLabelEntity
//import com.example.android_accountbook_14.data.datasource.ColorLabelDao
//import com.example.android_accountbook_14.data.datasource.LedgerDao
//import com.example.android_accountbook_14.data.datasource.TextLabelDao
//import kotlinx.coroutines.delay
//import timber.log.Timber
//import javax.inject.Inject
//import javax.inject.Singleton
//import kotlin.random.Random
//
///**
// *  처음에 DataBase 연동하기 전에 비즈니스 로직 + UI 작업을 우선 진행하기 위해 사용한 FakeDatabase
// *  프로젝트 중후반부에 Data 구조가 잡혔고, 그에 맞춰서 데이터 베이스 연동을 진행 한 수, ColorLabelDao, TextLabelDao, LedgerDao 들의 구현체를
// *  FakeDataBase 에서 AccountBookDataBase 로 변경해 주었다
// *
// *  테스트 코드 공부 후 확인해보니, data 를 전달받지 않고 직접 생성하는 부분을 제외하면 테스트 코드를 위한
// *  FakeDataSource 의 역할을 할 수 있는 코드였다.
// */
//
//@Singleton
//class FakeDataBase @Inject constructor(): ColorLabelDao, TextLabelDao, LedgerDao {
//    private val textLabels = DefaultDaoDataConstant.defaultPayment.mapIndexed{ i, it -> TextLabelEntity(i, it) }.toMutableList()
//    private val colorLabels = (
//            DefaultDaoDataConstant.defaultSpendLabels
//                .mapIndexed { i, it -> ColorLabelEntity(i, it.first, it.second, it.third)  } +
//                    DefaultDaoDataConstant.defaultIncomeLabels
//                        .mapIndexed { i, it -> ColorLabelEntity(i + DefaultDaoDataConstant.defaultSpendLabels.size+1, it.first, it.second, it.third)  })
//        .toMutableList()
//
//
//    private val ledgers = mutableListOf(
//        LedgerEntity(0, 2022, 7, 29, "금", -1000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "식사"),
//        LedgerEntity(1, 2022, 7, 29, "금", -1100, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "식사"),
//        LedgerEntity(2, 2022, 7, 29, "금", -1000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "식사"),
//        LedgerEntity(3, 2022, 7, 28, "목", -10000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "식사"),
//        LedgerEntity(4, 2022, 7, 27, "수", -12000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "식사"),
//        LedgerEntity(5, 2022, 7, 28, "목", -2000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "식사"),
//        LedgerEntity(6, 2022, 7, 27, "수", -3000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "식사"),
//        LedgerEntity(7, 2022, 7, 27, "수", -4000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "식사"),
//
//        LedgerEntity(8, 2022, 7, 29, "금", 1000000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "월급"),
//        LedgerEntity(9, 2022, 7, 30, "토", 600000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "보너스!"),
//        LedgerEntity(10, 2022, 7, 29, "금", 50000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "일당"),
//        LedgerEntity(11, 2022, 7, 29, "금", 100000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "보너스"),
//        LedgerEntity(12, 2022, 7, 28, "목", 50000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "일당"),
//        LedgerEntity(13, 2022, 7, 29, "금", 100000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "보너스"),
//        LedgerEntity(14, 2022, 7, 28, "목", 50000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "일당"),
//        LedgerEntity(15, 2022, 7, 29, "금", 1000000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "월급"),
//        LedgerEntity(16, 2022, 7, 27, "수", 2000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "개꿀"),
//    )
//
//    override suspend fun fetchTextLabels(): List<TextLabelEntity> {
//        delay(500)
//        return textLabels
//    }
//
//    override suspend fun fetchColorLabels(): List<ColorLabelEntity> {
//        delay(500)
//        return colorLabels
//    }
//
//    override suspend fun updateTextLabel(id: Int, title: String): Boolean {
//        textLabels.indexOfLast { it.id == id }.let {
//            if(it<0) return false
//            textLabels[it] = textLabels[it].copy(title = title)
//        }
//        return true
//    }
//
//    override suspend fun updateColorLabel(
//        id: Int,
//        title: String,
//        colorHex: String,
//        type: Int
//    ): Boolean {
//        colorLabels.indexOfLast { it.id == id && it.type == type }.let {
//            if(it<0) return false
//            colorLabels[it] = colorLabels[it].copy(title = title, color = colorHex)
//        }
//        return true
//    }
//
//    override suspend fun insertTextLabel(title: String): Boolean {
//        if(textLabels.find { it.title == title } != null)
//            return false
//        textLabels.add(
//            TextLabelEntity(
//                id = textLabels[textLabels.size-1].id + 1,
//                title
//            )
//        )
//        return true
//    }
//
//    override suspend fun insertColorLabel(
//        title: String,
//        colorHex: String,
//        type: Int
//    ): Boolean {
//        if(colorLabels.find { it.title == title && it.type == type } != null)
//            return false
//        colorLabels.add(
//            ColorLabelEntity(
//                id = colorLabels[colorLabels.size-1].id + 1,
//                title,
//                colorHex,
//                type
//            )
//        )
//        return true
//    }
//
//    override suspend fun insertLedger(
//        year: Int,
//        month: Int,
//        day: Int,
//        dayOfWeek: String,
//        price: Long,
//        tagId: Int,
//        paymentId: Int?,
//        content: String?
//    ): Boolean {
//        ledgers.add(
//            LedgerEntity(
//                ledgers[ledgers.size-1].id.plus(1),
//                year,
//                month,
//                day,
//                dayOfWeek,
//                price,
//                colorLabels.first { it.id == tagId },
//                textLabels.firstOrNull { it.id == paymentId },
//                content
//            )
//        )
//        return true
//    }
//
//    override suspend fun updateLedger(
//        id: Int,
//        year: Int,
//        month: Int,
//        day: Int,
//        dayOfWeek: String,
//        price: Long,
//        tagId: Int,
//        paymentId: Int?,
//        content: String?
//    ): Boolean {
//        val idx = ledgers.indices.find { ledgers[it].id == id }
//        return if(idx in 0 until ledgers.size){
//            ledgers[idx!!] = LedgerEntity(
//                ledgers[idx].id,
//                year,
//                month,
//                day,
//                dayOfWeek,
//                price,
//                colorLabels.first { it.id == tagId },
//                textLabels.firstOrNull { it.id == paymentId },
//                content
//            )
//            true
//        }else{
//            false
//        }
//    }
//
//    override suspend fun fetchLedgers(year: Int, month: Int): List<LedgerEntity> {
//        delay(500)
//        return ledgers.filter { it.year == year && it.month == month }.sortedBy { -it.day } // 최신순 정렬
//    }
//
//    override suspend fun removeLedges(removeIds: List<Int>): Boolean {
//        removeIds.forEach { removeId ->
//            if(!ledgers.removeIf { it.id == removeId }) {
//                Timber.d("do remove Ledgers item not found[$removeId]")
//                return false
//            }else{
//                Timber.d("do remove Ledgers item success[$removeId]")
//            }
//        }
//        return true
//    }
//}