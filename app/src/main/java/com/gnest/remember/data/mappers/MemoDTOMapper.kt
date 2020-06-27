package com.gnest.remember.data.mappers

import com.gnest.remember.data.dto.MemoDTO
import com.gnest.remember.domain.Memo

interface MemoDTOMapper {

    fun mapDto(dto: List<MemoDTO>): List<Memo>
    fun mapDto(dto: MemoDTO): Memo
    fun mapDomain(memos: List<Memo>): List<MemoDTO>
    fun mapDomain(memo: Memo): MemoDTO
}

class MemoDTOMapperImpl : MemoDTOMapper {

    override fun mapDto(dto: List<MemoDTO>): List<Memo> = dto.map { mapDto(it) }

    override fun mapDto(dto: MemoDTO): Memo = with(dto) {
        Memo(id, text, color, position, dateTime, isArchived)
    }

    override fun mapDomain(memos: List<Memo>): List<MemoDTO> = memos.map { mapDomain(it) }

    override fun mapDomain(memo: Memo): MemoDTO = with(memo) {
        MemoDTO(id, text, position, color, alarmDate, isArchived)
    }
}