package com.gnest.remember.domain

import org.joda.time.DateTime

data class CalendarData(val id: Int,
                        val title: String,
                        val description: String,
                        val date: DateTime)