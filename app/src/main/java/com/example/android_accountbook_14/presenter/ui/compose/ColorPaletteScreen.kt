package com.example.android_accountbook_14.presenter.ui.compose

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.android_accountbook_14.application.constant.LabelColorConstant
import com.example.android_accountbook_14.util.ColorUtil
import com.google.android.material.composethemeadapter.MdcTheme

@Composable
fun ColorPaletteScreen(
    colorArgbLiveData: LiveData<Int>,
    colors: List<Color>,
    colorSelectEvent: (String) -> (Unit),
    colorToHexFunc: (Color)->(String)
){
    val selectColor = colorArgbLiveData.observeAsState(0)
    ColorPaletteGridView(
        selectColor.value,
        colors,
        colorSelectEvent,
        colorToHexFunc
    )
}

@Composable
private fun ColorPaletteGridView(
    defaultSelectColor: Int,
    colors: List<Color>,
    colorSelectEvent: (String) -> (Unit),
    colorToHexFunc: (Color)->(String)
){
    val selectedColorArgb = remember { mutableStateOf(0)}
    selectedColorArgb.value = defaultSelectColor

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary)
    ){
        LazyVerticalGrid(
            columns = GridCells.Fixed(10),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, top = 6.dp, bottom = 11.dp
            )
        ){
            colors.forEach {
                item {
                    ColorPaletteItemView(it, selectedColorArgb) { selectColor ->
                        selectedColorArgb.value = selectColor.toArgb()
                        colorSelectEvent(colorToHexFunc(selectColor))
                    }
                }
            }
        }
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.primaryVariant,
            thickness = 1.dp
        )
    }
}

@Composable
private fun ColorPaletteItemView(
    color: Color,
    selectState: MutableState<Int>,
    clickEvent: (Color)->(Unit)
){
    val animateSize by animateDpAsState(
        targetValue = if(selectState.value == color.toArgb()) 24.dp else 18.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    Box(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RectangleShape,
        ) {
            Image(
                painter = ColorPainter(color),
                contentDescription = color.toString(),
                modifier = Modifier
                    .size(animateSize.coerceAtLeast(0.dp))
                    .clickable { clickEvent(color) }
            )
        }
    }
}

@Preview
@Composable
private fun ColorPalettePreview(){
    val colors = LabelColorConstant.spendLabelColor.map { ColorUtil.convertToComposeColor(it) }

    MdcTheme {
        ColorPaletteGridView(0, colors, {}){""}
    }
}