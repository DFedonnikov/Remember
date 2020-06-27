package com.gnest.remember.domain

import org.joda.time.DateTime

data class Memo(val id: Int,
           val text: String,
           val color: String,
           val position: Int,
           val alarmDate: DateTime?,
           val isArchived: Boolean)