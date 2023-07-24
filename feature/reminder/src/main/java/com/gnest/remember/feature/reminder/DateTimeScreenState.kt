package com.gnest.remember.feature.reminder

import com.gnest.remember.core.ui.pickers.DateUiState
import kotlinx.datetime.LocalTime

internal data class DateScreenState(val dateUiState: DateUiState)

internal data class TimeScreenState(val selectedTime: LocalTime, val isBackEnabled: Boolean)