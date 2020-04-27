package com.gnest.remember.presentation.ui.memolist

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.gnest.remember.R
import mva3.adapter.MultiViewAdapter
import mva3.adapter.decorator.Decorator

class SpaceItemDecoration(context: Context, adapter: MultiViewAdapter) : Decorator(adapter) {

    private val offset = context.resources.getDimensionPixelOffset(R.dimen.memo_item_offset)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State, adapterPosition: Int) {
        outRect.set(offset, offset, offset, offset)
    }
}