package com.example.android_accountbook_14.presenter.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.domain.model.LedgerMonthChartModel
import com.example.android_accountbook_14.util.toCashString

@Composable
fun LedgerMonthTableChartScreen(
    tableChartLiveData: LiveData<List<Pair<Float, LedgerMonthChartModel.LedgerChartItemModel>>>
){
    val tableChartData by tableChartLiveData.observeAsState(emptyList())
    LedgerMonthTableChart(tableChartData)
}

@Composable
private fun LedgerMonthTableChart(
    tableChartData: List<Pair<Float, LedgerMonthChartModel.LedgerChartItemModel>>
){
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn{
            tableChartData.forEachIndexed { i, it ->
                item {
                    LedgerMonthChartRow(
                        it.second.tag,
                        it.second.tagColorHex,
                        it.second.spend,
                        it.first
                    )
                    if(i != tableChartData.size-1){
                        Divider(
                            thickness = 1.dp,
                            color = colorResource(id = R.color.purple_A79FCB).copy(alpha = 0.4f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
            item {
                Divider(
                    color = MaterialTheme.colors.primaryVariant,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun LedgerMonthChartRow(
    tag: String,
    tagColorHex: String,
    spend: Long,
    percent: Float
){
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        ColorLabelView(
            title = tag,
            colorHex = tagColorHex
        )

        Text(
            text = (-spend).toCashString(),
            color = colorResource(id = R.color.purple_524D90),
            fontSize = 14.sp,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        )

        Text(
            text = String.format("%.1f", percent) + "%",
            color = colorResource(id = R.color.purple_524D90),
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}