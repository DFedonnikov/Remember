package com.gnest.remember.presentation.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import com.gnest.remember.R
import org.joda.time.DateTime
import org.joda.time.MutableDateTime

class DateTimePicker(private val context: Context) {

    private val lastChosenTimeState = MutableDateTime()
    private val timePastError by lazy { context.getString(R.string.time_past_error) }

    private val datePicker by lazy {
        val now = DateTime()
        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth -> onDateChosen(year, month, dayOfMonth) },
                now.year, now.monthOfYear - 1, now.dayOfMonth).apply {
            datePicker.minDate = now.millis
        }
    }

    private fun onDateChosen(year: Int, month: Int, dayOfMonth: Int) {
        lastChosenTimeState.year = year
        lastChosenTimeState.monthOfYear = month + 1
        lastChosenTimeState.dayOfMonth = dayOfMonth
        timePicker.show()
    }

    private val timePicker: TimePickerDialog by lazy {
        val now = DateTime()
        TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            lastChosenTimeState.hourOfDay = hourOfDay
            lastChosenTimeState.minuteOfHour = minute
            when {
                lastChosenTimeState.isAfterNow -> onDateTimeSetListener?.invoke(lastChosenTimeState.toDateTime())
                else -> {
                    showPastTimeError()
                    show()
                }
            }

        }, now.hourOfDay, now.minuteOfHour + 1, true)
    }

    private fun showPastTimeError() {
        Toast.makeText(context, timePastError, Toast.LENGTH_SHORT).show()
    }

    var onDateTimeSetListener: ((DateTime) -> Unit)? = null

    fun show() = datePicker.show()

    fun updateDate(date: DateTime) {
        lastChosenTimeState.setDateTime(date.year, date.monthOfYear, date.dayOfMonth, date.hourOfDay, date.minuteOfHour, date.secondOfMinute, date.millisOfSecond)
    }

    fun reset() {
        val now = DateTime()
        lastChosenTimeState.setDate(now)
        datePicker.datePicker.updateDate(now.year, now.monthOfYear - 1, now.dayOfMonth)
        timePicker.updateTime(now.hourOfDay, now.minuteOfHour)
    }
}