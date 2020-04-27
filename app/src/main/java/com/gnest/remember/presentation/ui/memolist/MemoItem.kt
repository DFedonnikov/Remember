package com.gnest.remember.presentation.ui.memolist

import com.gnest.remember.presentation.ui.MemoView

data class MemoItem(val id: Int = 0,
                    var text: String = "",
                    var color: MemoView.MemoColor = MemoView.MemoColor.BLUE,
                    var alarmDate: String = "",
                    var transitionName: String = "",
                    val position: Int = 0)