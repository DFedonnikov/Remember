package com.gnest.remember.presentation.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gnest.remember.R
import com.gnest.remember.presentation.ui.memolist.MemoItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.memo_edit_item.*

class EditMemoListAdapter(private val transitionStartListener: () -> Unit,
                          private val itemChangedListener: (MemoItem) -> Unit) : ListAdapter<MemoItem, EditMemoListAdapter.EditMemoViewHolder>(MemoItemDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditMemoViewHolder {
        return EditMemoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.memo_edit_item, parent, false))
    }

    override fun onBindViewHolder(holder: EditMemoViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class EditMemoViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val item: MemoItem?
            get() = containerView.tag as? MemoItem

        init {
            memo.textChangeListener = {
                item?.apply {
                    text = it
                    itemChangedListener(this)
                }
            }
            memo.colorChangeListener = {
                item?.apply {
                    color = it
                    itemChangedListener(this)
                }
            }
        }

        fun bind(item: MemoItem) {
            containerView.tag = null
//            memo.transitionName = item.transitionName
//            if (item.transitionName.isNotEmpty()) {
//                transitionStartListener()
//            }
//            item.transitionName = ""
            memo.setText(item.text)
            memo.setColor(item.color)
            containerView.tag = item
        }

    }
}

class MemoItemDiffUtilCallback : DiffUtil.ItemCallback<MemoItem>() {

    override fun areItemsTheSame(oldItem: MemoItem, newItem: MemoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MemoItem, newItem: MemoItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: MemoItem, newItem: MemoItem): Any? {
        return super.getChangePayload(oldItem, newItem)
    }
}