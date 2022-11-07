package com.example.android_accountbook_14.presenter.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_accountbook_14.domain.model.ColorLabel
import com.example.android_accountbook_14.domain.model.TextLabel
import com.example.android_accountbook_14.presenter.viewmodel.SettingViewModel
import com.example.android_accountbook_14.util.ColorUtil
import com.google.android.material.composethemeadapter.MdcTheme
import timber.log.Timber

@Composable
fun SettingScreen(viewModel: SettingViewModel){
    val textLabels by viewModel.textLabels.observeAsState()
    val colorLabels by viewModel.colorLabels.observeAsState()

    Timber.d("debug $textLabels, $colorLabels")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colors.primary
            )
    ) {
        SettingListView(
            textLabels,
            colorLabels,
            viewModel.clickTextLabel,
            viewModel.clickColorLabel,
            viewModel.clickPlusType
        )
    }
}


@Composable
private fun SettingListView(
    labels: Map<String, List<TextLabel>>?,
    colorLabels: Map<String, List<ColorLabel>>?,
    labelClickEvent: (TextLabel) -> (Unit),
    colorLabelClickEvent: (ColorLabel) -> (Unit),
    clickPlusTypeEvent: (String)->(Unit)
){
    LazyColumn{
        labels?.forEach { (h, list) ->
            //stickyHeader
            item {
                ListHeaderView(h)
            }
            items(items = list){
                LabelItemView(it, labelClickEvent)
            }
            item {
                PlusItemView(h, clickPlusTypeEvent)
            }
        }
        colorLabels?.forEach { (h, list) ->
            //stickyHeader
            item {
                ListHeaderView(h)
            }
            items(items = list){
                ColorLabelItemView(it, colorLabelClickEvent)
            }
            item {
                PlusItemView(h, clickPlusTypeEvent)
            }
        }
        item {
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
@Composable
private fun ListHeaderView(title: String){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colors.primaryVariant,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Divider(
            color = MaterialTheme.colors.primaryVariant,
            thickness = 1.dp
        )
    }
}

@Composable
private fun LabelItemView(label: TextLabel, labelClickEvent: (TextLabel) -> (Unit),){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { labelClickEvent(label) }
    ) {
        Text(
            text = label.title,
            color = MaterialTheme.colors.onPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 11.dp, horizontal = 16.dp)
        )
        Divider(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colors.primaryVariant,
            thickness = 1.dp
        )
    }
}

@Composable
private fun ColorLabelItemView(colorLabel: ColorLabel, colorLabelClickEvent: (ColorLabel) -> (Unit),){
    val labelColor = ColorUtil.convertToComposeColor(colorLabel.color)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { colorLabelClickEvent(colorLabel) }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = colorLabel.title,
                color = MaterialTheme.colors.onPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 11.dp)
            )
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = labelColor,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = colorLabel.title,
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
        }
        Divider(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colors.primaryVariant,
            thickness = 1.dp
        )
    }
}

@Composable
fun PlusItemView(type: String, clickEvent: (String)->(Unit)){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                clickEvent(type)
            }
    ) {
        Row() {
            Text(
                text = "$type 추가하기",
                color = MaterialTheme.colors.onPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 11.dp)
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "",
                modifier = Modifier.align(Alignment.CenterVertically)
                    .padding(horizontal = 16.dp)
            )
        }
        Divider(
            color = MaterialTheme.colors.primaryVariant,
            thickness = 1.dp
        )
    }
}

@Composable
fun ColorLabelView(
    title: String,
    colorHex: String
){
    val labelColor = ColorUtil.convertToComposeColor(colorHex)
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = labelColor
    ) {
        Text(
            text = title,
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
}

@Preview
@Composable
private fun SettingViewPreview(){
    val map = mapOf(
        "결제수단" to listOf(
            TextLabel(0, "현대카드"),
            TextLabel(0, "카카오뱅크"),
            TextLabel(0, "신한카드"),
            TextLabel(0, "국민카드"),
            TextLabel(0, "토스뱅크")
        ),
    )
    val colorMap = mapOf(
        "지출 카테고리" to listOf(
            ColorLabel(0, "교통", "#FF48C2E9"),
            ColorLabel(0, "문화/여가", "#FFEAE07C"),
            ColorLabel(0, "미분류", "#FF9CCC65"),
            ColorLabel(0, "생활", "#FF48C2E9"),
            ColorLabel(0, "쇼핑/뷰티", "#FF9CCC65"),
            ColorLabel(0, "식비", "#FF9CCC65"),
            ColorLabel(0, "의료/건강", "#FFEAE07C")
        ),
        "수입 카테고리" to listOf(
            ColorLabel(0, "월급", "#FF48C2E9"),
            ColorLabel(0, "용돈", "#FFEAE07C"),
            ColorLabel(0, "기타", "#FF9CCC65"),
            ColorLabel(0, "보너스", "#FF48C2E9"),
            ColorLabel(0, "포인트", "#FF9CCC65")
        )
    )
    MdcTheme() {
        SettingListView(map, colorMap, {}, {}){

        }
    }
}