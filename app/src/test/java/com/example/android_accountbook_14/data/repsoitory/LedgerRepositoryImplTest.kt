package com.example.android_accountbook_14.data.repsoitory

import com.example.android_accountbook_14.data.datasourceimpl.DefaultDaoDataConstant
import com.example.android_accountbook_14.data.datasoure.FakeLedgerDao
import com.example.android_accountbook_14.data.entity.ColorLabelEntity
import com.example.android_accountbook_14.data.entity.LedgerEntity
import com.example.android_accountbook_14.data.entity.TextLabelEntity
import com.example.android_accountbook_14.data.datasource.LedgerDao
import com.example.android_accountbook_14.domain.repository.LedgerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

@ExperimentalCoroutinesApi
internal class LedgerRepositoryImplTest{

    private val textLabels = DefaultDaoDataConstant.defaultPayment.mapIndexed{ i, it -> TextLabelEntity(i, it) }.toMutableList()
    private val colorLabels = (
            DefaultDaoDataConstant.defaultSpendLabels
                .mapIndexed { i, it -> ColorLabelEntity(i, it.first, it.second, it.third)  } +
                    DefaultDaoDataConstant.defaultIncomeLabels
                        .mapIndexed { i, it -> ColorLabelEntity(i + DefaultDaoDataConstant.defaultSpendLabels.size+1, it.first, it.second, it.third)  })
        .toMutableList()

    private val ledgers = mutableListOf(
        LedgerEntity(5, 2022, 7, 28, "목", -2000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "식사"),
        LedgerEntity(6, 2022, 7, 27, "수", -3000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "식사"),
        LedgerEntity(7, 2022, 7, 27, "수", -4000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "식사"),

        LedgerEntity(8, 2022, 7, 29, "금", 1000000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "월급"),
        LedgerEntity(9, 2022, 7, 30, "토", 600000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "보너스!"),
        LedgerEntity(10, 2022, 7, 29, "금", 50000, colorLabels[Random.nextInt(colorLabels.size)], textLabels[Random.nextInt(textLabels.size)], "일당"),
    )

    private lateinit var ledgerRepository: LedgerRepository

    @Before
    fun initRepository(){
        val ledgerDao: LedgerDao = FakeLedgerDao(
            ledgers, textLabels, colorLabels
        )
        ledgerRepository = LedgerRepositoryImpl(
            ledgerDao,
            Dispatchers.Unconfined
        )
    }

    @Test
    fun fetchLedgers2022Year7Month_equals_true(){
        val year = 2022
        val month = 7
        val result = runBlocking {
            ledgerRepository.fetchLedgers(year, month).getOrNull()
        }
        assertThat(result).isNotNull.isEqualTo(ledgers.filter { it.year == year && it.month == month }.map{ it.toDomain() }.sortedBy { -it.day })
    }

    @Test
    fun removeLedger_noneContainData_returnFalse(){
        val noneContainDataId = -1
        val result = runBlocking {
            ledgerRepository.removeLedgers(listOf(noneContainDataId)).getOrNull()
        }
        assertThat(result).isNotNull.isFalse
    }

    @Test
    fun removeLedger_containData_returnTrue(){
        val containData = ledgers.first()
        val result = runBlocking {
            ledgerRepository.removeLedgers(listOf(containData.id)).getOrNull()
        }
        assertThat(result).isNotNull.isTrue

        val list = runBlocking {
            ledgerRepository.fetchLedgers(containData.year, containData.month).getOrNull()
        }
        assertThat(list).doesNotContain(containData.toDomain())
    }

    @Test
    fun insertLedger_nullableData_isNull_returnTrue(){
        val insertData = ledgers.first().copy(
            payment = null,
            content = null
        )
        val result = runBlocking {
            ledgerRepository.insertLedger(
                insertData.year,
                insertData.month,
                insertData.day,
                insertData.dayOfWeek,
                insertData.price,
                insertData.colorTag.id,
                insertData.payment?.id,
                insertData.content
            ).getOrNull()
        }
        assertThat(result).isNotNull.isTrue

        val insertDataToDomain = insertData.toDomain()
        val list = runBlocking {
            ledgerRepository.fetchLedgers(insertData.year, insertData.month).getOrNull()
        }
        assertThat(list).extracting("year", "month", "day", "dayOfWeek", "price", "tag", "tagColorHex", "payment", "content")
            .contains(tuple(insertDataToDomain.year, insertDataToDomain.month, insertDataToDomain.day, insertDataToDomain.dayOfWeek,
                insertDataToDomain.price, insertDataToDomain.tag, insertDataToDomain.tagColorHex, insertDataToDomain.payment, insertDataToDomain.content))
    }

    @Test
    fun insertLedger_nullableData_notNull_returnTrue(){
        val insertData = ledgers.first().copy(content = "new insert content")
        val result = runBlocking {
            ledgerRepository.insertLedger(
                insertData.year,
                insertData.month,
                insertData.day,
                insertData.dayOfWeek,
                insertData.price,
                insertData.colorTag.id,
                insertData.payment?.id,
                insertData.content
            ).getOrNull()
        }
        assertThat(result).isNotNull.isTrue

        val insertDataToDomain = insertData.toDomain()
        val list = runBlocking {
            ledgerRepository.fetchLedgers(insertData.year, insertData.month).getOrNull()
        }
        assertThat(list).extracting("year", "month", "day", "dayOfWeek", "price", "tag", "tagColorHex", "payment", "content")
            .contains(tuple(insertDataToDomain.year, insertDataToDomain.month, insertDataToDomain.day, insertDataToDomain.dayOfWeek,
                insertDataToDomain.price, insertDataToDomain.tag, insertDataToDomain.tagColorHex, insertDataToDomain.payment, insertDataToDomain.content))
    }

    @Test
    fun remove_notContainData_returnFalse(){
        val noneContainData = ledgers.first().copy(id = -1)
        val result = runBlocking {
            ledgerRepository.removeLedgers(listOf(noneContainData.id)).getOrNull()
        }
        assertThat(result).isNotNull.isFalse
    }

    @Test
    fun remove_containData_returnTrue(){
        val containData = ledgers.first()
        val result = runBlocking {
            ledgerRepository.removeLedgers(listOf(containData.id)).getOrNull()
        }
        assertThat(result).isNotNull.isTrue

        val listFromDao = runBlocking {
            ledgerRepository.fetchLedgers(containData.year, containData.month).getOrNull()
        }
        assertThat(listFromDao).isNotNull.doesNotContain(containData.toDomain())
    }

    @Test
    fun update_containData_returnTrue(){
        val updateData = ledgers.first().copy(content = "update data")
        val result = runBlocking {
            ledgerRepository.updateLedger(
                updateData.id,
                updateData.year,
                updateData.month,
                updateData.day,
                updateData.dayOfWeek,
                updateData.price,
                updateData.colorTag.id,
                updateData.payment?.id,
                updateData.content
            ).getOrNull()
        }
        assertThat(result).isNotNull.isTrue

        val listFromDao = runBlocking {
            ledgerRepository.fetchLedgers(updateData.year, updateData.month).getOrNull()?.first{ it.id == updateData.id }
        }
        assertThat(listFromDao).isNotNull.isEqualTo(updateData.toDomain())
    }

    @Test
    fun update_notContainData_returnFalse(){
        val updateData = ledgers.first().copy(id = -1, content = "update data")
        val result = runBlocking {
            ledgerRepository.updateLedger(
                updateData.id,
                updateData.year,
                updateData.month,
                updateData.day,
                updateData.dayOfWeek,
                updateData.price,
                updateData.colorTag.id,
                updateData.payment?.id,
                updateData.content
            ).getOrNull()
        }
        assertThat(result).isNotNull.isFalse
    }




}
