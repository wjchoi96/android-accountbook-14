package com.example.android_accountbook_14.domain.usecase

import com.example.android_accountbook_14.data.repsoitory.FakeLedgerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
internal class FetchCalenderLedgerListUseCaseTest {

    private lateinit var useCase: FetchCalenderLedgerListUseCase

    @Before
    fun initUseCase(){
        useCase = FetchCalenderLedgerListUseCase(
            FakeLedgerRepository(),
            ConvertDayOfWeekUseCase(),
            Dispatchers.Unconfined
        )
    }

    @Test
    fun invoke_size_always_multipleOfSeven(){
        runBlocking {
            var size: Int?
            for(year in 1900..2100){
                for(month in 1..12){
                    size = useCase(year, month).getOrNull()?.size

                    assertThat(size).isNotNull
                    assertThat((size ?: 0 ) % 7).isEqualTo(0)
                }
            }
        }
    }
}