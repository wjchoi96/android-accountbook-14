package com.example.android_accountbook_14.data.repsoitory

import com.example.android_accountbook_14.data.*
import com.example.android_accountbook_14.data.datasoure.StubColorLabelDao
import com.example.android_accountbook_14.data.datasoure.StubTextLabelDao
import com.example.android_accountbook_14.data.entity.ColorLabelEntity
import com.example.android_accountbook_14.data.entity.TextLabelEntity
import com.example.android_accountbook_14.data.datasource.ColorLabelDao
import com.example.android_accountbook_14.data.datasource.TextLabelDao
import com.example.android_accountbook_14.domain.repository.LabelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
internal class LabelRepositoryImplTest {

    private val textLabels: MutableList<TextLabelEntity> = getTextLabelEntities.toMutableList()
    private val colorLabels: MutableList<ColorLabelEntity> = getColorLabelEntities.toMutableList()

    private val textLabelDao: TextLabelDao = StubTextLabelDao(textLabels)
    private val colorLabelDao: ColorLabelDao = StubColorLabelDao(colorLabels)

    private lateinit var labelRepository: LabelRepository

    @Before
    fun initRepository(){
        labelRepository = LabelRepositoryImpl(
            textLabelDao,
            colorLabelDao,
            Dispatchers.Unconfined
        )
    }

    // 사실상 Label Repository 주요 기능
    @Test
    fun fetchLabels_requestAllLabelsBundle_equals(){
        //Given - repository 는 전역적으로 호출되어있다

        //When - fetch label bundle
        val labelBundle = runBlocking {
            labelRepository.fetchLabelsBundle().getOrNull()
        }

        //Then - label 의 종류에 따라 잘 분류되었는지 테스트
        assertThat(labelBundle?.paymentLabels).isNotNull.isEqualTo(textLabels.map { it.toDomain() })
        assertThat(labelBundle?.spendColorLabels).isNotNull.isEqualTo(colorLabels.filter { it.type < 0 }.map { it.toDomain() })
        assertThat(labelBundle?.incomeColorLabels).isNotNull.isEqualTo(colorLabels.filter { it.type > 0 }.map { it.toDomain() })
    }

    // 사실상 Label Repository 주요 기능
    @Test
    fun fetchLabels_emptyLabelsData_isEmpty(){
        //Given - List 가 비어있는 DataSource 들을 가진 Repository 생성
        val textLabelDao: TextLabelDao = StubTextLabelDao()
        val colorLabelDao: ColorLabelDao = StubColorLabelDao()
        val labelRepository = LabelRepositoryImpl(textLabelDao, colorLabelDao, Dispatchers.Unconfined)

        //When - fetch label bundle
        val labelBundle = runBlocking {
            labelRepository.fetchLabelsBundle().getOrNull()
        }

        //Then - 분류될 리스트가 없으니 빈 데이터들이 나온다
        assertThat(labelBundle?.paymentLabels).isNotNull.isEmpty()
        assertThat(labelBundle?.spendColorLabels).isNotNull.isEmpty()
        assertThat(labelBundle?.incomeColorLabels).isNotNull.isEmpty()
    }

    // 사실상 Label Repository 주요 기능
    @Test
    fun fetchLabels_emptyTextLabel_textLabelIsEmpty_colorLabelIsNotEmpty(){
        //Given - TextLabelList 가 비어있는 DataSource 들을 가진 Repository 생성
        val textLabelDao: TextLabelDao = StubTextLabelDao()
        val labelRepository = LabelRepositoryImpl(textLabelDao, colorLabelDao, Dispatchers.Unconfined)

        //When - fetch label bundle
        val labelBundle = runBlocking {
            labelRepository.fetchLabelsBundle().getOrNull()
        }

        //Then - TextLabel 은 분류될 데이터가 없으니 비어서 나오고, Income, Spend 는 정상적으로 나온다
        assertThat(labelBundle?.paymentLabels).isNotNull.isEmpty()
        assertThat(labelBundle?.spendColorLabels).isNotNull.isEqualTo(colorLabels.filter { it.type < 0 }.map { it.toDomain() })
        assertThat(labelBundle?.incomeColorLabels).isNotNull.isEqualTo(colorLabels.filter { it.type > 0 }.map { it.toDomain() })
    }

    // 사실상 Label Repository 주요 기능
    @Test
    fun fetchLabels_emptyColorLabel_colorLabelIsEmpty_textLabelIsNotEmpty(){
        //Given - ColorLabelList 가 비어있는 DataSource 들을 가진 Repository 생성
        val colorLabelDao: ColorLabelDao = StubColorLabelDao()
        val labelRepository = LabelRepositoryImpl(textLabelDao, colorLabelDao, Dispatchers.Unconfined)

        //When - fetch label bundle
        val labelBundle = runBlocking {
            labelRepository.fetchLabelsBundle().getOrNull()
        }

        //Then - Income, Spend 는 분류될 데이터가 없으니 비어서 나오고, TextLabel 은 정상적으로 나온다
        assertThat(labelBundle?.paymentLabels).isNotNull.isEqualTo(textLabels.map { it.toDomain() })
        assertThat(labelBundle?.spendColorLabels).isNotNull.isEmpty()
        assertThat(labelBundle?.incomeColorLabels).isNotNull.isEmpty()
    }

    // 사실상 Label Repository 주요 기능
    @Test
    fun fetchLabels_emptyIncomeLabel_incomeLabelIsEmpty_otherLabelIsNotEmpty(){
        //Given - Income Label 이 없는 DataSource 를 가진 Repository 생성
        val colorLabelDao: ColorLabelDao = StubColorLabelDao(getSpendLabelEntities.toMutableList())
        val labelRepository = LabelRepositoryImpl(textLabelDao, colorLabelDao, Dispatchers.Unconfined)

        //When - fetch label bundle
        val labelBundle = runBlocking {
            labelRepository.fetchLabelsBundle().getOrNull()
        }

        //Then - Income Label 은 분류될 데이터가 없으니 비어서 나오고, 나머지는 정상적으로 나온다
        assertThat(labelBundle?.paymentLabels).isNotNull.isEqualTo(textLabels.map { it.toDomain() })
        assertThat(labelBundle?.spendColorLabels).isNotNull.isEqualTo(colorLabels.filter { it.type < 0 }.map { it.toDomain() })
        assertThat(labelBundle?.incomeColorLabels).isNotNull.isEmpty()
    }

    // 사실상 Label Repository 주요 기능
    @Test
    fun fetchLabels_emptySpendLabel_spendLabelIsEmpty_otherLabelIsNotEmpty(){
        //Given - Spend Label 이 없는 DataSource 를 가진 Repository 생성
        val colorLabelDao: ColorLabelDao = StubColorLabelDao(getIncomeLabelEntities.toMutableList())
        val labelRepository = LabelRepositoryImpl(textLabelDao, colorLabelDao, Dispatchers.Unconfined)

        //When - fetch label bundle
        val labelBundle = runBlocking {
            labelRepository.fetchLabelsBundle().getOrNull()
        }

        //Then - Spend Label 은 분류될 데이터가 없으니 비어서 나오고, 나머지는 정상적으로 나온다
        assertThat(labelBundle?.paymentLabels).isNotNull.isEqualTo(textLabels.map { it.toDomain() })
        assertThat(labelBundle?.spendColorLabels).isNotNull.isEmpty()
        assertThat(labelBundle?.incomeColorLabels).isNotNull.isEqualTo(colorLabels.filter { it.type > 0 }.map { it.toDomain() })
    }

    // insert 에 대한 유효성 체크는 DataSource 가 해야하는 역할이니, Repository 는 적절한 DataSource 에게 호출을 하였는지에 대한 테스트만 진행한다
    @Test
    fun insertTextLabel_inputData_returnTure(){
        //Given - get insert data
        val insertData = getTextLabelEntity.toDomain()

        //When - do insert
        val result = runBlocking {
            labelRepository.insertPaymentLabel(insertData).getOrNull()
        }

        //Then - return true
        assertThat(result).isNotNull.isTrue
    }

    // update 에 대한 유효성 체크는 DataSource 가 해야하는 역할이니, Repository 는 적절한 DataSource 에게 호출을 하였는지에 대한 테스트만 진행한다
    @Test
    fun updateTextLabel_data_returnTrue(){
        //Given - get update data
        val updateItem = getTextLabelEntity.toDomain()

        //When - do update
        val result = runBlocking {
            labelRepository.updatePaymentLabel(updateItem).getOrNull()
        }

        //Then - return true
        assertThat(result).isNotNull.isTrue
    }


    // insert 에 대한 유효성 체크는 DataSource 가 해야하는 역할이니, Repository 는 적절한 DataSource 에게 호출을 하였는지에 대한 테스트만 진행한다
    @Test
    fun insertSpendLabel_data_returnTrue(){
        //Given - get insert data
        val insertItem = getSpendLabelEntity.toDomain()

        //When - do insert
        val result = runBlocking {
            labelRepository.insertSpendLabel(insertItem).getOrNull()
        }

        //Then - return true
        assertThat(result).isNotNull.isTrue
    }

    // update 에 대한 유효성 체크는 DataSource 가 해야하는 역할이니, Repository 는 적절한 DataSource 에게 호출을 하였는지에 대한 테스트만 진행한다
    @Test
    fun updateSpendLabel_data_returnTrue(){
        //Given - get update data
        val updateItem = getSpendLabelEntity.toDomain()

        //When - do update
        val result = runBlocking {
            labelRepository.updateSpendLabel(updateItem).getOrNull()
        }

        //Then - return true
        assertThat(result).isNotNull.isTrue
    }

    // update 에 대한 유효성 체크는 DataSource 가 해야하는 역할이니, Repository 는 적절한 DataSource 에게 호출을 하였는지에 대한 테스트만 진행한다
    @Test
    fun updateIncomeLabel_data_returnTrue(){
        //Given - get update data
        val updateItem = getIncomeLabelEntity.toDomain()

        //When - do update
        val result = runBlocking {
            labelRepository.updateIncomeLabel(updateItem).getOrNull()
        }

        //Then - return true
        assertThat(result).isNotNull.isTrue
    }

}