package com.gnest.remember.domain

import org.joda.time.DateTime

class Memo(val id: Int,
           val text: String,
           val color: String,
           val position: Int,
           val alarmDate: DateTime?,
           val isAlarmSet: Boolean)