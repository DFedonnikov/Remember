package com.gnest.remember.presentation.mappers

import com.gnest.remember.BLUE
import com.gnest.remember.EMERALD
import com.gnest.remember.PURPLE
import com.gnest.remember.YELLOW
import com.gnest.remember.domain.Memo
import com.gnest.remember.presentation.ui.memolist.MemoItem
import com.gnest.remember.presentation.ui.MemoView

interface MemoMapper {

    fun mapUi(uiItems: List<MemoItem>): List<Memo>
    fun mapDomain(domainItems: List<Memo>): List<MemoItem>
}

class MemoMapperImpl : MemoMapper {

    override fun mapUi(uiItems: List<MemoItem>): List<Memo> {
        return uiItems.map {
            Memo(it.id, it.text, mapColor(it.color), it.position, null, false)
        }
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
                    alarmDate = it.alarmDate?.toString() ?: "",
                    position = it.position)
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
}