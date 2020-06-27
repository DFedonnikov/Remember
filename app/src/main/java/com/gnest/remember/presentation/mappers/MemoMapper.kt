package com.gnest.remember.presentation.mappers

import android.content.Context
import com.gnest.remember.*
import com.gnest.remember.domain.CalendarData
import com.gnest.remember.domain.Memo
import com.gnest.remember.presentation.ui.memolist.MemoItem
import com.gnest.remember.presentation.ui.MemoView
import org.joda.time.DateTime

interface MemoMapper {

    fun mapUi(uiItems: List<MemoItem>): List<Memo>
    fun mapUi(item: MemoItem): Memo
    fun mapDomain(domainItems: List<Memo>): List<MemoItem>
    fun mapToCalendarData(item: MemoItem): CalendarData
}

class MemoMapperImpl(private val context: Context) : MemoMapper {

    override fun mapUi(uiItems: List<MemoItem>): List<Memo> = uiItems.map { mapUi(it) }

    override fun mapUi(item: MemoItem): Memo = with(item) {
        Memo(id, text, mapColor(color), position, alarmDate, isArchived)
    }

    private fun mapColor(color: MemoView.MemoColor): String {
        return when (color) {
            MemoView.MemoColor.BLUE -> BLUE
            MemoView.MemoColor.EMERALD -> EMERALD
            MemoView.MemoColor.YELLOW -> YELLOW
            MemoView.MemoColor.PURPLE -> PURPLE
        }
    }

    override fun mapDomain(domainItems: List<Memo>): List<MemoItem> {
        return domainItems.map {
            MemoItem(id = it.id,
                    text = it.text,
                    color = mapColor(it.color),
                    alarmDate = it.alarmDate,
                    position = it.position,
                    isArchived = it.isArchived)
        }
    }

    private fun mapColor(color: String): MemoView.MemoColor {
        return when (color) {
            BLUE -> MemoView.MemoColor.BLUE
            EMERALD -> MemoView.MemoColor.EMERALD
            YELLOW -> MemoView.MemoColor.YELLOW
            PURPLE -> MemoView.MemoColor.PURPLE
            else -> MemoView.MemoColor.BLUE
        }
    }

    override fun mapToCalendarData(item: MemoItem): CalendarData {
        return CalendarData(
                id = item.id,
                title = context.getString(R.string.notification_title),
                description = item.text.take(30),
                date = item.alarmDate ?: DateTime.now())
    }
}