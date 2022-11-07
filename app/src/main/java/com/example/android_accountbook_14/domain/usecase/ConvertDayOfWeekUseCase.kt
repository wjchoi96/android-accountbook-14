package com.example.android_accountbook_14.domain.usecase

import java.util.*
import javax.inject.Inject

class ConvertDayOfWeekUseCase @Inject constructor() {
    operator fun invoke(year: Int, month: Int, dayOfMonth: Int): Pair<Int, String> {
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = year
        cal[Calendar.MONTH] = month-1
        cal[Calendar.DAY_OF_MONTH] = dayOfMonth
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        return when(cal.get(Calendar.DAY_OF_WEEK)){
            1 -> 1 to "일"
            2 -> 2 to "월"
            3 -> 3 to "화"
            4 -> 4 to "수"
            5 -> 5 to "목"
            6 -> 6 to "금"
            7 -> 7 to "토"
            else -> throw Throwable("get day of week return unknown raw value not in(1..7)")
        }
    }
}