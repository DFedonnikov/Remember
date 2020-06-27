package com.gnest.remember.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gnest.remember.R
import com.gnest.remember.presentation.ui.memolist.MemoItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.memo_edit_item.*

private const val TEXT_PAYLOAD = "TEXT_PAYLOAD"
private const val COLOR_PAYLOAD = "COLOR_PAYLOAD"
private const val ALARM_PAYLOAD = "ALARM_PAYLAOD"

class EditMemoListAdapter(private val transitionStartListener: () -> Unit,
                          private val itemChangedListener: (MemoItem) -> Unit,
                          private val alarmSetListener: (MemoItem) -> Unit,
                          private val alarmDismissListener: (MemoItem) -> Unit) : ListAdapter<MemoItem, EditMemoListAdapter.EditMemoViewHolder>(MemoItemDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditMemoViewHolder {
        return EditMemoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.memo_edit_item, parent, false))
    }

    override fun onBindViewHolder(holder: EditMemoViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onBindViewHolder(holder: EditMemoViewHolder, position: Int, payloads: MutableList<Any>) {
        getItem(position)?.let { holder.bind(it, payloads) }
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
            memo.onAlarmSetListener = {
                item?.apply {
                    alarmDate = it
                    itemChangedListener(this)
                    alarmSetListener(this)
                }
            }
            memo.onAlarmDismissListener = {
                item?.apply {
                    alarmDate = null
                    itemChangedListener(this)
                    alarmDismissListener(this)
                }
            }
        }

        fun bind(item: MemoItem, payloads: List<Any> = emptyList()) {
            containerView.tag = null
//            memo.transitionName = item.transitionName
//            if (item.transitionName.isNotEmpty()) {
//                transitionStartListener()
//            }
//            item.transitionName = ""
            payloads.onTextChanged { setText(item.text) }
            payloads.onColorChanged { setColor(item.color) }
            payloads.onAlarmChanged { setAlarm(item.alarmDate) }
            containerView.tag = item
        }

        private inline fun List<Any>.onTextChanged(updateFunc: MemoView.() -> Unit) {
            getPayloadAs(TEXT_PAYLOAD)?.let { if (it) memo.updateFunc() }
                    ?: memo.updateFunc()
        }

        private fun List<Any>.getPayloadAs(payloadKey: String): Boolean? = (firstOrNull() as? Bundle)?.getBoolean(payloadKey)

        private inline fun List<Any>.onColorChanged(updateFunc: MemoView.() -> Unit) {
            getPayloadAs(COLOR_PAYLOAD)?.let { if (it) memo.updateFunc() }
                    ?: memo.updateFunc()
        }

        private inline fun List<Any>.onAlarmChanged(updateFunc: MemoView.() -> Unit) {
            getPayloadAs(ALARM_PAYLOAD)?.let { if (it) memo.updateFunc() }
                    ?: memo.updateFunc()
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
        return bundleOf(TEXT_PAYLOAD to (oldItem.text != newItem.text),
                COLOR_PAYLOAD to (oldItem.color != newItem.color),
                ALARM_PAYLOAD to (oldItem.alarmDate != newItem.alarmDate))
    }
}