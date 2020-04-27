package com.gnest.remember.presentation.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gnest.remember.R
import com.gnest.remember.presentation.ui.memolist.MemoItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.memo_list_item.*

class MemoListAdapter(private val onItemClick: (MemoItem, View) -> Unit,
                      private val transitionStartListener: () -> Unit) : ListAdapter<MemoItem, MemoListAdapter.MemoListViewHolder>(MemoItemDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoListViewHolder =
            MemoListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.memo_list_item, parent, false))

    override fun onBindViewHolder(holder: MemoListViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class MemoListViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val item: MemoItem?
            get() = containerView.tag as? MemoItem

        init {
            memo.isReadOnly = true
            memo.hideAlarm()
            memo.clickListener = {
                item?.let {

                    onItemClick(it, containerView)
                }
            }
        }

        fun bind(item: MemoItem) {
            containerView.tag = item
            memo.setText(item.text)
            memo.transitionName = item.transitionName
            if (item.transitionName.isNotEmpty()) {
                transitionStartListener()
            }
            item.transitionName = ""
        }

    }
}