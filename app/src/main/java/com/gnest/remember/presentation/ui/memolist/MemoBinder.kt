package com.gnest.remember.presentation.ui.memolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import com.gnest.remember.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.memo_list_item.*
import mva3.adapter.ItemBinder
import mva3.adapter.ItemViewHolder
import mva3.adapter.util.PayloadProvider


class MemoBinder : ItemBinder<MemoItem, MemoBinder.MemoListViewHolder>() {

    var isSelectionActivated = false

    override fun bindViewHolder(holder: MemoListViewHolder?, item: MemoItem?) {

        item?.let { holder?.bind(item) }
    }

    override fun bindViewHolder(holder: MemoListViewHolder?, item: MemoItem?, payloads: MutableList<Any?>?) {
        item?.let { holder?.bind(it, payloads?.get(0) as? Boolean ?: false) }
    }

    override fun createViewHolder(parent: ViewGroup?): MemoListViewHolder =
            MemoListViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.memo_list_item, parent, false))

    override fun canBindData(item: Any?): Boolean = item is MemoItem

    inner class MemoListViewHolder(override val containerView: View) : ItemViewHolder<MemoItem>(containerView), LayoutContainer {

        private val item: MemoItem?
            get() = containerView.tag as? MemoItem

        init {
            memo.isReadOnly = true
            memo.hideAlarm()
            memo.clickListener = {
                when {
                    isSelectionActivated -> toggleItemSelection()
                    else -> onItemClick()
                }
            }
            memo.onMoveListener = {
                startDrag()
            }
            memo.onLongClickListener = {
                toggleItemSelection()
                true
            }
        }

        fun bind(item: MemoItem, isTextChanged: Boolean = true) {
            containerView.tag = item
            if (isTextChanged) {
                memo.setText(item.text)
            }
            memo.isSelected = isItemSelected
            memo.setColor(item.color)
//            memo.transitionName = item.transitionName
//            if (item.transitionName.isNotEmpty()) {
//                transitionStartListener()
//            }
//            item.transitionName = ""
        }

        override fun getDragDirections(): Int = (ItemTouchHelper.LEFT
                or ItemTouchHelper.UP
                or ItemTouchHelper.RIGHT
                or ItemTouchHelper.DOWN)

        override fun getSwipeDirections(): Int = (ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }
}

class MemoPayloadProvider : PayloadProvider<MemoItem> {
    override fun areItemsTheSame(oldItem: MemoItem?, newItem: MemoItem?): Boolean =
            oldItem?.id == newItem?.id

    override fun getChangePayload(oldItem: MemoItem?, newItem: MemoItem?): Any =
            oldItem?.text != newItem?.text

    override fun areContentsTheSame(oldItem: MemoItem?, newItem: MemoItem?): Boolean =
            oldItem == newItem
}