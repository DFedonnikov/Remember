package com.gnest.remember.presentation.ui.memolist

import com.gnest.remember.presentation.ui.MemoView
import org.joda.time.DateTime

data class MemoItem(val id: Int = 0,
                    var text: String = "",
                    var color: MemoView.MemoColor = MemoView.MemoColor.BLUE,
                    var alarmDate: DateTime? = null,
                    var transitionName: String = "",
                    val position: Int = 0,
                    val isArchived: Boolean = false)