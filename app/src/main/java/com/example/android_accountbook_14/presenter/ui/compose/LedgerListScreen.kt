package com.example.android_accountbook_14.presenter.ui.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.domain.model.LedgerListItemModel
import com.example.android_accountbook_14.domain.model.LedgerModel
import com.example.android_accountbook_14.util.toCashString
import com.google.android.material.composethemeadapter.MdcTheme

private fun getDummyData(): List<LedgerListItemModel>{
    val list = mutableListOf<LedgerListItemModel>()
    repeat(10){
        list.add(
            LedgerListItemModel(
                2002,
                12,
                1,
                "금",
                listOf(
                    LedgerModel(
                        1,
                        2002,
                        12,
                        1,
                        "수",
                        -1000,
                        "교통",
                        "#94D3CC",
                        "교통카드",
                        "버스비"
                    ),
                    LedgerModel(
                        2,
                        2002,
                        12,
                        2,
                        "수",
                        10000,
                        "용돈",
                        "#4CA1DE",
                        "카뱅",
                        "용돈받음!"
                    )
                )
            )
        )
    }
    return list
}

@Composable
fun LedgerListScreen(
    ledgerList: LiveData<List<LedgerListItemModel>>,
    selectLedgers: SnapshotStateList<LedgerModel>,
    itemClickListener: (Pair<Int, Int>, Int) -> (Unit) = {_,_ ->},
    itemLongClickListener: (Pair<Int, Int>, Int) -> (Unit) = {_,_ ->},
    colorHexToColor: (String)->(Color) = { Color.Blue }
){
    val list = ledgerList.observeAsState()
    LedgerListView(
        list = list.value ?: emptyList(),
        selectLedgers = selectLedgers,
        itemClickListener = itemClickListener,
        itemLongClickListener = itemLongClickListener,
        colorHexToColor = colorHexToColor
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LedgerListView(
    list: List<LedgerListItemModel>?,
    selectLedgers: SnapshotStateList<LedgerModel>,
    itemClickListener: (Pair<Int, Int>, Int) -> (Unit) = {_,_ ->},
    itemLongClickListener: (Pair<Int, Int>, Int) -> (Unit) = {_,_ ->},
    colorHexToColor: (String)->(Color) = { Color.Blue }
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.primary)
    ) {
        LazyColumn {
            list?.forEachIndexed { pIdx, it ->
                stickyHeader {
                    LedgerHeader(
                        it.date,
                        it.income,
                        it.spend
                    )
                }
                itemsIndexed(it.dayLedgers){ idx, item ->
                    LedgerItemView(
                        itemClickListener = itemClickListener,
                        itemLongClickListener = itemLongClickListener,
                        index = pIdx to idx,
                        id = item.id,
                        tag = item.tag,
                        tagColor = colorHexToColor(item.tagColorHex),
                        payment = item.payment,
                        price = item.price,
                        content = item.content,
                        isSelect = selectLedgers.find { it.id == item.id } != null
                    )
                }
                item {
                    Divider(
                        color = MaterialTheme.colors.primaryVariant,
                        thickness = 1.dp
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(68.dp))
            }
        }
    }
}

@Composable
private fun LedgerHeader(
    date: String,
    income: Long,
    spend: Long
){
    Box(modifier = Modifier
        .background(color = MaterialTheme.colors.primary)){
        Column(
            modifier = Modifier
                .padding(top = 24.dp, bottom = 8.dp)
        ){
            Row {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterVertically),
                    text = date,
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.primaryVariant,
                    textAlign = TextAlign.Start
                )

                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Bottom),
                    text = "수입 ${income.toCashString()} 지출 ${spend.toCashString()}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colors.primaryVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LedgerItemView(
    itemClickListener: (Pair<Int, Int>, Int) -> (Unit) = {_,_ ->},
    itemLongClickListener: (Pair<Int, Int>, Int) -> (Unit) = {_,_ ->},
    index: Pair<Int, Int>,
    id: Int,
    tag: String,
    tagColor: Color,
    payment: String?,
    price: Long,
    content: String?,
    isSelect: Boolean
){
    val backgroundColor = if(isSelect)
        colorResource(id = R.color.white)
    else
        colorResource(id = R.color.white_F7F6F3)
    Row(
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    itemClickListener(index, id)
                },
                onLongClick = {
                    itemLongClickListener(index, id)
                }
            )
            .background(color = backgroundColor)
    ) {
        if(isSelect){
            Checkbox(
                checked = true,
                onCheckedChange = {},
                colors = CheckboxDefaults.colors(
                    checkedColor = colorResource(id = R.color.red_E75B3F),
                    checkmarkColor = colorResource(id = R.color.white)
                )
            )
        }

        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colors.primaryVariant.copy(
                    alpha = 0.4f
                ),
                thickness = 1.dp
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = tagColor,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = tag,
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .widthIn(min = 50.dp, max = 150.dp),
                        textAlign = TextAlign.Center
                    )
                }
                if(!payment.isNullOrBlank()){
                    Text(
                        text = payment,
                        color = MaterialTheme.colors.primaryVariant,
                        fontSize = 10.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    )
                }
            }

            Row(
                modifier = Modifier.padding(
                    start = 16.dp, end = 16.dp, bottom = 8.dp
                )
            ) {
                Text(
                    text = content ?: "???",
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .weight(1f)
                )

                Text(
                    text = price.toCashString() + "원",
                    color = if(price<0) colorResource(R.color.red_E75B3F) else colorResource(id = R.color.blue_4EAABA),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun rememberSaveableLedgerListItemState(
    checkModeState: Boolean = false,
    checkedOn: Boolean = false
): LedgerItemState {
    return rememberSaveable(
        saver = LedgerItemStateSaver
    ) { LedgerItemState(
        checkModeState,
        checkedOn
    )
    }
}
val LedgerItemStateSaver = run {
    val checkModeKey = "CheckMode"
    val checkOnKey = "CheckOn"
    mapSaver(
        save = { mapOf(checkModeKey to it.checkModeState, checkOnKey to it.checkedOn) },
        restore = { LedgerItemState(it[checkModeKey] as Boolean, it[checkOnKey] as Boolean) }
    )
}

@Stable
class LedgerItemState(
    checkModeState: Boolean,
    checkedOn: Boolean
) {
    var checkModeState by mutableStateOf(checkModeState)
    var checkedOn by mutableStateOf(checkedOn)
}


@Preview
@Composable
private fun LedgerListPreview(){
    MdcTheme() {
//        LedgerListView(
//            getDummyData()
//        )
    }
}