package com.example.android_accountbook_14.domain.usecase

import com.example.android_accountbook_14.di.DefaultDispatcher
import com.example.android_accountbook_14.domain.model.LedgerListItemModel
import com.example.android_accountbook_14.domain.model.LedgerModel
import com.example.android_accountbook_14.domain.repository.LedgerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class FetchCalenderLedgerListUseCase @Inject constructor(
    private val ledgerRepository: LedgerRepository,
    private val convertDayOfWeekUseCase: ConvertDayOfWeekUseCase,
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(year: Int, month: Int): Result<List<LedgerListItemModel>> {
        return kotlin.runCatching {
            withContext(coroutineDispatcher){
                // 1일부터 마지막 날까지의 day item 을 생성
                val calendarList = getCalenderContainer(year, month)
                val lastDay = getLastDayOnMonth(year, month)
                // 저장소에 저장된 ledgers 를 불러와 각 날짜에 맞게 데이터 삽입
                val res = ledgerRepository.fetchLedgers(
                    year, month
                )
                val currentCursorLedgers = mutableListOf<LedgerModel>()
                res.getOrThrow().run {
                    //최신순 정렬이 되어있다
                    for(day in 1..lastDay){
                        currentCursorLedgers.addAll(this.filter { it.month == month && it.day == day }) // 해당 날짜의 list 가져온다
                        if(currentCursorLedgers.isNotEmpty()) { // 해당 날짜의 item 들이 있다면, 적용시켜준다
                            val idx = calendarList.indices.firstOrNull { calendarList[it].month == month && calendarList[it].day == day }
                            if(idx == null) {
                                currentCursorLedgers.clear()
                                continue
                            }
                            calendarList[idx] = calendarList[idx].copy(dayLedgers = currentCursorLedgers.toMutableList())
                            currentCursorLedgers.clear()
                        }
                    }
                }
                Timber.d("calender list [${calendarList.size}]")
                var log = ""
                calendarList.forEach {
                    log += "[${it.year}/${it.month}/${it.day} ${it.dayOfWeek}] has ${it.dayLedgers.size}. income[${it.income}], spend[${it.spend}]\n"
                }
                Timber.d("calendar log\n$log")
                calendarList
            }
        }

    }

    private fun getLastDayOnMonth(year: Int, month: Int): Int {
        return Calendar.getInstance().apply {
            set(year, month, 0)
        }.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun getCalenderContainer(year: Int, month: Int): MutableList<LedgerListItemModel>{
        val calendarList = mutableListOf<LedgerListItemModel>()

        val firstDayOfWeek = convertDayOfWeekUseCase(year, month, 1)
        val needPrevMonthDay = firstDayOfWeek.first - 1

        val prevYear = if(month == 1) year-1 else year
        val prevMonth = if(month == 1) 12 else month-1
        val prevMonthLastDayOfWeek = getLastDayOnMonth(prevYear, prevMonth)
        var prevMonthDay = prevMonthLastDayOfWeek+1-needPrevMonthDay
        for(i in 0 until needPrevMonthDay){
            calendarList.add(
                LedgerListItemModel(
                    prevYear,
                    prevMonth,
                    prevMonthDay,
                    convertDayOfWeekUseCase(prevYear, prevMonth, prevMonthDay).second,
                    listOf()
                )
            )
            prevMonthDay++
        }

        val lastDay = getLastDayOnMonth(year, month)
        Timber.d("calendar [${year}/${month} has $lastDay day]")
        for(day in 1..lastDay){
            calendarList.add(
                LedgerListItemModel(
                    year,
                    month,
                    day,
                    convertDayOfWeekUseCase(year, month, day).second,
                    listOf()
                )
            )
        }

        val lastDayOfWeek = convertDayOfWeekUseCase(year, month, lastDay)
        val needNextMothDay = 7 - lastDayOfWeek.first
        val nextYear = if(month == 12) year+1 else year
        val nextMonth = if(month == 12) 1 else month+1
        for(day in 1 until needNextMothDay+1){
            calendarList.add(
                LedgerListItemModel(
                    nextYear,
                    nextMonth,
                    day,
                    convertDayOfWeekUseCase(nextYear, nextMonth, day).second,
                    listOf()
                )
            )
        }

        var log1 = ""
        calendarList.forEach {
            log1 += "[${it.year}/${it.month}/${it.day} ${it.dayOfWeek}]\n"
        }
        Timber.d("calender base log\n$log1")
        return calendarList
    }

}