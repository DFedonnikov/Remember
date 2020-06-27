package com.gnest.remember.data

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.gnest.remember.domain.CalendarData
import java.util.*

private const val EVENT_ID_PREFIX = "CalendarEventMemo#"

interface CalendarProvider {
    fun isEventExists(id: Int): Boolean
    fun updateCalendarEvent(data: CalendarData): Long
    fun createCalendarEvent(data: CalendarData): Long
    fun removeCalendarEvent(id: Int): Boolean
}

class CalendarProviderImpl(context: Context) : CalendarProvider {

    private val contentResolver by lazy { context.contentResolver }
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    override fun isEventExists(id: Int): Boolean = getEventId(id) != -1L

    private fun getEventId(id: Int) = sharedPreferences.getLong(EVENT_ID_PREFIX + id, -1)

    override fun updateCalendarEvent(data: CalendarData): Long {
        val contentValues = createContentValues(data)
        val eventId = getEventId(data.id).takeIf { it > -1 } ?: return 0
        val updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
        val rows = contentResolver.update(updateUri, contentValues, null, null)
        return when {
            rows > 0 -> eventId
            else -> 0
        }
    }

    private fun createContentValues(data: CalendarData): ContentValues = ContentValues().apply {
        val endDate = data.date.plusHours(1).millis
        put(CalendarContract.Events.DTSTART, data.date.millis)
        put(CalendarContract.Events.DTEND, endDate)
        put(CalendarContract.Events.TITLE, data.title)
        put(CalendarContract.Events.DESCRIPTION, data.description)
        put(CalendarContract.Events.CALENDAR_ID, getCalendarId())
        put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
    }

    @SuppressLint("MissingPermission")
    private fun getCalendarId(): Long {
        var calendarId: Long = 0
        val selection = (CalendarContract.Calendars.VISIBLE + " = 1 AND "
                + CalendarContract.Calendars.IS_PRIMARY + " = 1")
        val calendarUri = CalendarContract.Calendars.CONTENT_URI
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(calendarUri, null, selection, null, null)
                    ?.takeIf { it.count > 0 }
                    ?: run { contentResolver.query(calendarUri, null, null, null, null) }
            cursor?.takeIf { it.moveToFirst() }?.let {
                calendarId = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))
            }
        } finally {
            cursor?.close()
        }
        return calendarId
    }

    @SuppressLint("MissingPermission")
    override fun createCalendarEvent(data: CalendarData): Long {
        val contentValues = createContentValues(data)
        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues)
        return uri?.lastPathSegment?.toLongOrNull()?.also { eventId ->
            sharedPreferences.edit { putLong(EVENT_ID_PREFIX + data.id, eventId) }
        } ?: 0
    }

    override fun removeCalendarEvent(id: Int): Boolean = getEventId(id).takeIf { it > -1 }?.let {
        val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, it)
        val rows = contentResolver.delete(deleteUri, null, null)
        sharedPreferences.edit { remove(EVENT_ID_PREFIX + id) }
        rows > 0
    } ?: false
}