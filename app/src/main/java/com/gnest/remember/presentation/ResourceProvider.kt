package com.gnest.remember.presentation

import android.content.Context
import com.gnest.remember.R

interface ResourceProvider {

    val saveCalendarEventSuccess: String
    val saveCalendarEventFailure: String
    val removeCalendarEventSuccess: String
    val removeCalendarEventFailure: String

    fun getMemosArchivedMessage(count: Int): String
    fun getMemosUnarchivedMessage(count: Int): String
    fun getMemosRemovedMessage(count: Int): String
}

class ResourceProviderImpl(private val context: Context) : ResourceProvider {

    override val saveCalendarEventSuccess: String by lazy { context.getString(R.string.calendar_event_save_success) }
    override val saveCalendarEventFailure: String by lazy { context.getString(R.string.calendar_event_save_failure) }
    override val removeCalendarEventSuccess: String by lazy { context.getString(R.string.calendar_event_remove_success) }
    override val removeCalendarEventFailure: String by lazy { context.getString(R.string.calendar_event_remove_failure) }

    override fun getMemosArchivedMessage(count: Int): String = context.resources.getQuantityString(R.plurals.memo_archived_message, count, count)
    override fun getMemosUnarchivedMessage(count: Int): String = context.resources.getQuantityString(R.plurals.memo_unarchived_message, count, count)
    override fun getMemosRemovedMessage(count: Int): String = context.resources.getQuantityString(R.plurals.memo_removed_message, count, count)
}