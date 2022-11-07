package com.example.android_accountbook_14.domain.usecase

import com.example.android_accountbook_14.di.DefaultDispatcher
import com.example.android_accountbook_14.domain.model.LedgerListItemModel
import com.example.android_accountbook_14.domain.model.LedgerModel
import com.example.android_accountbook_14.domain.repository.LedgerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class FetchLedgerListItemsUseCase @Inject constructor(
    private val ledgerRepository: LedgerRepository,
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(year: Int, month: Int): Result<List<LedgerListItemModel>>{
        return kotlin.runCatching {
            withContext(coroutineDispatcher){
                val list = mutableListOf<LedgerListItemModel>()
                val res = ledgerRepository.fetchLedgers(
                    year, month
                )
                var cursor: Int? = null
                val currentCursorLedgers = mutableListOf<LedgerModel>()
                Timber.d("fetch ledgers[${res.getOrNull()?.size}] => \n${res.getOrNull().toString()}")
                res.getOrThrow().run {
                    //최신순 정렬이 되어있다
                    forEach { v ->
                        // 현재 커서가 가리키는 날과, 현재 loop 중인 item 의 날짜가 다르다면(다음 날짜로 넘어간것) 커서의 Day 를 적용시키고 다음 커서로 넘어갈 절차를 가진다
                        if(cursor != null && list[cursor!!].day != v.day){
                            list[cursor!!] = list[cursor!!].copy(dayLedgers = currentCursorLedgers.toMutableList())
                            currentCursorLedgers.clear()
                            cursor = null
                        }
                        if(cursor == null){ // cursor 가 null 이면, 현재 day 의 item 을 하나 생성해서 가리킨다
                            list.add(LedgerListItemModel(v.year, v.month, v.day, v.dayOfWeek, listOf()))
                            cursor = list.size-1
                        }
                        currentCursorLedgers.add(v)
                    }
                    // loop 종료후 저장이 안된 Day 가 있다면 적용시켜준다
                    if(currentCursorLedgers.isNotEmpty() && cursor != null){
                        list[cursor!!] = list[cursor!!].copy(dayLedgers = currentCursorLedgers.toMutableList())
                        currentCursorLedgers.clear()
                        cursor = null
                    }
                }
                list
            }
        }
    }
}