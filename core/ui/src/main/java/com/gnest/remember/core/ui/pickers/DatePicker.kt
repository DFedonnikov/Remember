package com.gnest.remember.core.ui.pickers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.chargemap.compose.numberpicker.ListItemPicker
import com.gnest.remember.core.designsystem.theme.RememberTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Stable
data class DateUiState(
    val selectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val years: List<Int>,
    val months: List<Month>,
    val days: List<Int>
)

@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    dateUiState: DateUiState,
    onDateChanged: (LocalDate) -> Unit
) {
    var selectedDate by remember(
        dateUiState.selectedDate.year,
        dateUiState.selectedDate.month,
        dateUiState.selectedDate.dayOfMonth
    ) { mutableStateOf(dateUiState.selectedDate) }
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        ListItemPicker(
            modifier = Modifier.weight(1f),
            value = selectedDate.dayOfMonth,
            onValueChange = {
                val date = LocalDate(selectedDate.year, selectedDate.month, it)
                selectedDate = date
                onDateChanged(date)
            },
            dividersColor = Color.Transparent,
            textStyle = pickerStyle,
            list = dateUiState.days
        )
        ListItemPicker(
            modifier = Modifier.weight(1f),
            value = selectedDate.month,
            label = { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()) },
            onValueChange = {
                val date = LocalDate(selectedDate.year, it, selectedDate.dayOfMonth)
                selectedDate = date
                onDateChanged(date)
            },
            dividersColor = Color.Transparent,
            textStyle = pickerStyle,
            list = dateUiState.months
        )

        ListItemPicker(
            modifier = Modifier.weight(1f),
            value = selectedDate.year,
            onValueChange = {
                val date = LocalDate(it, selectedDate.month, selectedDate.dayOfMonth)
                selectedDate = date
                onDateChanged(date)
            },
            dividersColor = Color.Transparent,
            textStyle = pickerStyle,
            list = dateUiState.years
        )
    }
}

@Composable
@Preview(widthDp = 360, showBackground = true)
private fun DatePickerPreview() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    var selectedDate by remember { mutableStateOf(now) }
    val months = remember(selectedDate) {
        when {
            now.month >= selectedDate.month && now.year == selectedDate.year -> Month.values().takeLast(12 - selectedDate.monthNumber + 1)
            else -> Month.values().toList()
        }
    }
    val days = remember(selectedDate) {
        val minimalDay = when {
            now.month == selectedDate.month && now.year == selectedDate.year -> now.dayOfMonth
            else -> 1
        }
        (minimalDay..selectedDate.month.maxLength()).toList()
    }
    RememberTheme {
        DatePicker(
            dateUiState = DateUiState(
                selectedDate = selectedDate,
                years = List(100) { now.year + it },
                months = months,
                days = days

            )
        ) {
            selectedDate = it.coerceAtLeast(now)
        }
    }
}