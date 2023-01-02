package com.gnest.remember.core.ui.lists

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.theme.AppColors

@Composable
fun ColorSelector(
    modifier: Modifier = Modifier,
    items: List<ColorSelectorItem>,
    onItemClick: (ColorSelectorItem) -> Unit
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        itemsIndexed(items) { index, item ->
            val itemPadding = if (index == items.lastIndex) 0.dp else 16.dp
            ColorSelectorItem(
                modifier = Modifier.padding(end = itemPadding),
                item = item,
                onClick = onItemClick
            )
        }
    }
}

@Composable
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true, widthDp = 360)
private fun ColorSelectorPreview() {
    val items = ColorSelectorDefaults.colorSelectorList
    ColorSelector(items = items) { clickedItem ->
        items.forEach {
            it.isSelected.value = it == clickedItem
        }
    }
}

@Composable
private fun ColorSelectorItem(
    item: ColorSelectorItem,
    modifier: Modifier = Modifier,
    onClick: (ColorSelectorItem) -> Unit
) {
    val isSelected by item.isSelected
    val selectedBorderSize by animateDpAsState(targetValue = if (isSelected) 2.dp else 0.dp)
    val selectedBorderColor by animateColorAsState(targetValue = if (isSelected) item.selectedColor else Color.Transparent)
    Box(
        modifier = modifier
            .border(selectedBorderSize, selectedBorderColor, CircleShape)
            .clip(CircleShape)
            .clickable(onClick = { onClick(item) })
            .size(32.dp),

        contentAlignment = Alignment.Center

    ) {
        val size by animateDpAsState(targetValue = if (isSelected) 22.dp else 32.dp)
        val borderSize = if (item.hasBorder) 1.dp else 0.dp
        val borderColor = if (item.hasBorder) item.selectedColor else Color.Transparent
        Box(
            modifier = Modifier
                .border(borderSize, borderColor, CircleShape)
                .clip(CircleShape)
                .size(size)
                .background(item.color)

        )
    }
}

@Composable
@Preview
private fun ColorElementPreview() {
    val item = ColorSelectorDefaults.aeroBlueItem
    ColorSelectorItem(
        item = item,
    ) { item.isSelected.value = !item.isSelected.value }
}

interface ColorSelectorItem {

    val color: Color
    val selectedColor: Color
    val hasBorder: Boolean get() = false
    val isSelected: MutableState<Boolean>

}

object ColorSelectorDefaults {

    val whiteItem
        get() = object : ColorSelectorItem {
            override val color: Color = AppColors.White
            override val selectedColor: Color = AppColors.FrenchGray
            override val hasBorder: Boolean = true
            override val isSelected: MutableState<Boolean> = mutableStateOf(false)
        }
    val roseItem
        get() = object : ColorSelectorItem {
            override val color: Color = AppColors.Rose
            override val selectedColor: Color = AppColors.Espresso
            override val isSelected: MutableState<Boolean> = mutableStateOf(false)
        }
    val pictonBlueItem
        get() = object : ColorSelectorItem {
            override val color: Color = AppColors.PictonBlue
            override val selectedColor: Color = AppColors.Stratos
            override val isSelected: MutableState<Boolean> = mutableStateOf(false)
        }
    val aeroBlueItem
        get() = object : ColorSelectorItem {
            override val color: Color = AppColors.AeroBlue
            override val selectedColor: Color = AppColors.Amazon
            override val isSelected: MutableState<Boolean> = mutableStateOf(false)
        }
    val capeHoneyItem
        get() = object : ColorSelectorItem {
            override val color: Color = AppColors.CapeHoney
            override val selectedColor: Color = AppColors.AntiqueBronze
            override val isSelected: MutableState<Boolean> = mutableStateOf(false)
        }
    val whiteLilacItem
        get() = object : ColorSelectorItem {
            override val color: Color = AppColors.WhiteLilac
            override val selectedColor: Color = AppColors.Jakarta
            override val isSelected: MutableState<Boolean> = mutableStateOf(false)
        }
    val athensGrayItem
        get() = object : ColorSelectorItem {
            override val color: Color = AppColors.AthensGray
            override val selectedColor: Color = AppColors.FrenchGray
            override val isSelected: MutableState<Boolean> = mutableStateOf(false)
        }

    val colorSelectorList
        get() = listOf(
            whiteItem, roseItem, pictonBlueItem,
            aeroBlueItem, capeHoneyItem, whiteLilacItem, athensGrayItem
        )
}