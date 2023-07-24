package com.gnest.remember.core.ui.pickers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import com.gnest.remember.core.designsystem.theme.RememberTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    time: LocalTime = getTimeNow(),
    onTimeChanged: (LocalTime) -> Unit
) {
    var selectedTime: Hours by remember(time.hour, time.minute) { mutableStateOf(FullHours(time.hour, time.minute)) }
    HoursNumberPicker(
        modifier = modifier,
        value = selectedTime,
        dividersColor = Color.Transparent,
        textStyle = pickerStyle,
        onValueChange = {
            selectedTime = it
            onTimeChanged(LocalTime(it.hours, it.minutes))
        }
    )
}

private fun getTimeNow() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time

@Composable
@Preview(widthDp = 360)
private fun TimePickerPreview() {
    var selectedTime by remember { mutableStateOf(getTimeNow()) }
    RememberTheme {
        TimePicker(time = selectedTime) { selectedTime = it }
    }
}