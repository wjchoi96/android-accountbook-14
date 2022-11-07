package com.example.android_accountbook_14.util

import java.text.DecimalFormat
import kotlin.math.abs

//https://caliou.tistory.com/73
fun Long.toCashString(): String {
    return DecimalFormat("###,###.####").format(this)
}

fun Long.toCashString2() : String{
    var cashText = abs(this).toString()
    var cashTextLength = cashText.length
    val size = (cashTextLength - 1) / 3

    val strArr : ArrayList<String> = ArrayList()
    for( i in 1..size ){ // 1 ~ size ( size 포함 ) loop
        val frontStr = cashText.slice(IntRange(cashTextLength-3,cashTextLength-1))
        cashText = cashText.slice(IntRange(0,cashTextLength-4))
        cashTextLength -= 3
        strArr.add(frontStr)
        strArr.add(",")
    }
    strArr.add(cashText)
    strArr.reverse()

    cashText = ""
    for( i in strArr ){
        cashText+=i
    }
    if(this < 0)
        cashText = "-$cashText"
    return cashText
}
