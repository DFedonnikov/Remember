package com.gnest.remember.data.mappers

import com.gnest.remember.data.dto.MemoDTO
import com.gnest.remember.domain.Memo

interface MemoDTOMapper {

    fun mapDto(dto: List<MemoDTO>): List<Memo>
    fun mapDomain(memos: List<Memo>): List<MemoDTO>
}

class MemoDTOMapperImpl : MemoDTOMapper {

    override fun mapDto(dto: List<MemoDTO>): List<Memo> {
        return dto.map { Memo(it.id, it.text, it.color, it.position, it.dateTime, it.isAlarmSet) }
    }

    override fun mapDomain(memos: List<Memo>): List<MemoDTO> {
        return memos.map { MemoDTO(it.id, it.text, it.position, it.color, it.alarmDate, it.alarmDate != null, false) }
    }
}