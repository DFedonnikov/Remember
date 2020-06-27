package com.gnest.remember.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gnest.remember.R
import com.gnest.remember.extensions.dismissNotificationsAlarm
import com.gnest.remember.presentation.ui.memolist.MemoBinder
import com.gnest.remember.presentation.ui.memolist.MemoItem
import com.gnest.remember.presentation.ui.memolist.MemoPayloadProvider
import com.gnest.remember.presentation.ui.memolist.SpaceItemDecoration
import com.gnest.remember.presentation.viewmodel.ArchivedListViewModel
import com.gnest.remember.presentation.viewmodel.SingleEventObserver
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_archived_list.*
import kotlinx.android.synthetic.main.fragment_archived_list.fab
import mva3.adapter.ListSection
import mva3.adapter.MultiViewAdapter
import mva3.adapter.util.Mode
import org.koin.android.viewmodel.ext.android.viewModel

class ArchivedItemsFragment : Fragment() {

    private val listViewModel: ArchivedListViewModel by viewModel()
    private lateinit var sections: ListSection<MemoItem>
    private lateinit var itemBinder: MemoBinder

    private val onSelectedBackCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            sections.clearSelections()
            archivedRoot.transitionToStart()
            isEnabled = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onSelectedBackCallback)
        return inflater.inflate(R.layout.fragment_archived_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMemoList()
        initListeners()
        initSubscriptions()
        fab.isUnarchive = true
    }

    private fun initMemoList() {
        val layoutManager = AutoFitLayoutManager(requireContext(), 2)
        val adapter = MultiViewAdapter()
        sections = ListSection()
        itemBinder = MemoBinder()
        adapter.registerItemBinders(itemBinder)
        itemBinder.addDecorator(SpaceItemDecoration(requireContext(), adapter))
        adapter.addSection(sections)
        adapter.setSpanCount(layoutManager.spanCount)
        layoutManager.spanSizeLookup = adapter.spanSizeLookup
        memoArchivedList.addItemDecoration(adapter.itemDecoration)
        memoArchivedList.layoutManager = layoutManager
        memoArchivedList.adapter = adapter
        adapter.itemTouchHelper.attachToRecyclerView(memoArchivedList)
        adapter.setSelectionMode(Mode.MULTIPLE)
        sections.setPayloadProvider(MemoPayloadProvider())
    }

    private fun initListeners() {
        fab.removeListener = {
            listViewModel.onItemsRemove(sections.selectedItems)
            archivedRoot.transitionToStart()
        }
        fab.archiveListener = {
            listViewModel.onItemsUnarchive(sections.selectedItems)
            archivedRoot.transitionToStart()
        }
        fab.shareListener = {
            sections.selectedItems.firstOrNull()?.let { memo ->
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, memo.text)
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_memo_intent_title))
                activity?.let { activity ->
                    if (chooserIntent.resolveActivity(activity.packageManager) != null) {
                        startActivity(chooserIntent)
                    }
                }
            }
            archivedRoot.transitionToStart()
            sections.clearSelections()
        }
        sections.setOnItemClickListener { position, item ->
            val action = ArchivedItemsFragmentDirections.openEdit()
            action.memoId = item.id
            action.position = position
            action.isArchived = true
            findNavController().navigate(action)
        }
        sections.setSwipeToDismissListener { _, item -> listViewModel.onItemUnarchive(item) }
        sections.setOnSelectionChangedListener { _, _, selectedItems ->
            itemBinder.isSelectionActivated = selectedItems.isNotEmpty()
            onSelectedBackCallback.isEnabled = selectedItems.isNotEmpty()
            when {
                selectedItems.size > 1 -> fab.hideShare()
                selectedItems.isNotEmpty() -> {
                    archivedRoot.transitionToEnd()
                    fab.expand()
                }
                else -> {
                    fab.shrink()
                    archivedRoot.transitionToStart()
                }
            }
        }
    }

    private fun initSubscriptions() {
        subscribeToListUpdate()
        subscribeToSnackbarDismissRemoved()
        subscribeToSnackbarDismissUnarchived()
        subscribeToNotificationsAlarmDismiss()
    }

    private fun subscribeToListUpdate() {
        listViewModel.list.observe(viewLifecycleOwner, Observer {
            sections.set(it)
        })
    }

    private fun subscribeToSnackbarDismissUnarchived() {
        listViewModel.dismissUnarchivedLiveData.observe(viewLifecycleOwner, SingleEventObserver {
            val snackbar = Snackbar
                    .make(requireView(), it.message, Snackbar.LENGTH_SHORT)
                    .setAction(android.R.string.cancel) { listViewModel.onItemDismissUnarchivedCancel() }
                    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            when (event) {
                                BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE, BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT
                                -> listViewModel.onItemDismissUnarchivedTimeout()
                                else -> {
                                }
                            }
                        }
                    })
            snackbar.show()
        })
    }

    private fun subscribeToSnackbarDismissRemoved() {
        listViewModel.dismissRemovedLiveData.observe(viewLifecycleOwner, SingleEventObserver {
            val snackbar = Snackbar
                    .make(requireView(), it.message, Snackbar.LENGTH_SHORT)
                    .setAction(android.R.string.cancel) { listViewModel.onItemDismissRemovedCancel() }
                    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            when (event) {
                                BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE, BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT
                                -> listViewModel.onItemDismissRemovedTimeout()
                                else -> {
                                }
                            }
                        }
                    })
            snackbar.show()
        })
    }

    private fun subscribeToNotificationsAlarmDismiss() {
        listViewModel.removeNotificationsAlarmLiveData.observe(viewLifecycleOwner, SingleEventObserver {
            context?.dismissNotificationsAlarm(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listViewModel.onScreenClose(sections.data)
        onSelectedBackCallback.remove()
    }
}