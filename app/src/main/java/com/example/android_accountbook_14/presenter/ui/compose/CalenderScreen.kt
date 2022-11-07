package com.example.android_accountbook_14.presenter.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.domain.model.LedgerListItemModel
import com.example.android_accountbook_14.util.toCashString
import timber.log.Timber


@Composable
fun CalenderScreen(
    calenderItemsLiveData: LiveData<List<LedgerListItemModel>>,
    currentMonth: Int,
    currentDay: Int,
    showMonthLiveData: LiveData<Int>
){
    val calenderItems by calenderItemsLiveData.observeAsState(emptyList())
    val showMonth by showMonthLiveData.observeAsState(0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white_F7F6F3))
    ) {
        CalenderGridView(
            calenderItems,
            currentMonth = currentMonth,
            currentDay = currentDay,
            itemSize = if(calenderItems.size >= 36) 65f else 75f,
            showMonth = showMonth
        )

        Spacer(modifier = Modifier.height(16.dp))

        CalendarInfoView(
            calenderItems.sumOf { it.income },
            calenderItems.sumOf { it.spend }
        )
    }

}

@Composable
private fun CalenderGridView(
    calenderItems: List<LedgerListItemModel> = emptyList(),
    currentMonth: Int = 0,
    currentDay: Int = 0,
    itemSize: Float = 60f,
    showMonth: Int
){
    Timber.d("CalenderGridView month[$currentMonth], day[$currentDay]")
    Column {
        Divider(
            color = MaterialTheme.colors.primaryVariant,
            thickness = 0.5.dp
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(7)
        ){
            calenderItems.forEach {
                item {
                    Timber.d("CalenderItem month[${it.month}], day[${it.day}]")
                    val dayTextColor: Color = if(it.month == showMonth)
                        colorResource(id = R.color.purple_524D90)
                    else
                        colorResource(id = R.color.purple_524D90).copy(alpha = 0.4f)

                    val color = if(it.month == currentMonth && it.day == currentDay)
                        colorResource(id = R.color.white)
                    else
                        colorResource(id = R.color.white_F7F6F3)

                    CalenderGridItemView(
                        it,
                        backgroundColor = color,
                        dayColor = dayTextColor,
                        itemSize = itemSize
                    )
                }
            }
        }
        Divider(
            color = MaterialTheme.colors.primaryVariant,
            thickness = 0.5.dp
        )
    }
}

@Composable
private fun CalenderGridItemView(
    item: LedgerListItemModel,
    backgroundColor: Color = colorResource(id = R.color.white_F7F6F3),
    dayColor: Color,
    itemSize: Float = 60f
){
    Column(
        modifier = Modifier
            .size(itemSize.dp)
            .border(
                0.5.dp,
                MaterialTheme.colors.primaryVariant.copy(alpha = 0.4f)
            )
            .background(color = backgroundColor)
            .padding(4.dp),
        verticalArrangement = Arrangement.Bottom,
    ) {
        if(item.income != 0L){
            Text(
                text = "${item.income.toCashString()}.",
                color = colorResource(id = R.color.blue_4EAABA),
                fontSize = 8.sp
            )
        }
        if(item.spend != 0L){
            Text(
                text = "-${item.spend.toCashString()}",
                color = colorResource(id = R.color.red_E75B3F),
                fontSize = 8.sp
            )
        }
        if(item.income != 0L || item.spend != 0L){
            Text(
                text = (item.income - item.spend).toCashString(),
                color = colorResource(id = R.color.purple_524D90),
                fontSize = 8.sp
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.End)
                .fillMaxHeight(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = "${item.day}",
                color = dayColor,//colorResource(id = R.color.purple_524D90),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
private fun CalendarInfoView(
    income: Long = 0,
    spend: Long = 0
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.Bottom
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ){
            Row(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.ledger_income),
                    color = colorResource(id = R.color.purple_524D90),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = income.toCashString(),
                    color = colorResource(id = R.color.blue_4EAABA),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(
                color = MaterialTheme.colors.primaryVariant.copy(alpha = 0.4f),
                thickness = 1.dp
            )

            Row(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.ledger_spend),
                    color = colorResource(id = R.color.purple_524D90),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = (-spend).toCashString(),
                    color = colorResource(id = R.color.red_E75B3F),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(
                color = MaterialTheme.colors.primaryVariant.copy(alpha = 0.4f),
                thickness = 1.dp
            )

            Row(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.ledger_total_sum),
                    color = colorResource(id = R.color.purple_524D90),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = (income-spend).toCashString(),
                    color = colorResource(id = R.color.purple_524D90),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Divider(
            color = MaterialTheme.colors.primaryVariant,
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(29.dp))
    }
}