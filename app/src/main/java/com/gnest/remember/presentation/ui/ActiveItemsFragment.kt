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
import com.gnest.remember.presentation.ui.memolist.MemoBinder
import com.gnest.remember.presentation.ui.memolist.MemoItem
import com.gnest.remember.presentation.ui.memolist.MemoPayloadProvider
import com.gnest.remember.presentation.ui.memolist.SpaceItemDecoration
import com.gnest.remember.presentation.ui.state.DismissArchivedState
import com.gnest.remember.presentation.ui.state.DismissRemovedState
import com.gnest.remember.presentation.viewmodel.ActiveListViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_active_list.*
import mva3.adapter.ListSection
import mva3.adapter.MultiViewAdapter
import mva3.adapter.util.Mode
import org.koin.android.viewmodel.ext.android.viewModel

class ActiveItemsFragment : Fragment() {

    private val listViewModel: ActiveListViewModel by viewModel()
    private lateinit var sections: ListSection<MemoItem>
    private lateinit var itemBinder: MemoBinder
    private val dismissArchivedItemsObserver = Observer<DismissArchivedState> {
        val snackbar = Snackbar
                .make(requireView(), it.message, Snackbar.LENGTH_SHORT)
                .setAction(android.R.string.cancel) {
                    listViewModel.onItemDismissArchivedCancel()
                    unsubscribeFromSnackbarDismissArchived()
                }
                .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                .addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        when (event) {
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE, BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT
                            -> listViewModel.onItemDismissArchivedTimeout()
                            else -> {
                            }
                        }
                        unsubscribeFromSnackbarDismissArchived()
                    }
                })
        snackbar.show()
    }
    private val dismissRemovedItemsObserver = Observer<DismissRemovedState> {
        val snackbar = Snackbar
                .make(requireView(), it.message, Snackbar.LENGTH_SHORT)
                .setAction(android.R.string.cancel) {
                    listViewModel.onItemDismissRemovedCancel()
                    unsubscribeFromSnackbarDismissRemoved()
                }
                .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                .addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        when (event) {
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE, BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT
                            -> listViewModel.onItemDismissRemovedTimeout()
                            else -> {
                            }
                        }
                        unsubscribeFromSnackbarDismissRemoved()
                    }
                })
        snackbar.show()
    }
    private val onSelectedBackCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            sections.clearSelections()
            fab.shrink()
            isEnabled = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onSelectedBackCallback)
        return inflater.inflate(R.layout.fragment_active_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMemoList()
        initListeners()
        initSubscriptions()
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
        memoActiveList.addItemDecoration(adapter.itemDecoration)
        memoActiveList.layoutManager = layoutManager
        memoActiveList.adapter = adapter
        adapter.itemTouchHelper.attachToRecyclerView(memoActiveList)
        adapter.setSelectionMode(Mode.MULTIPLE)
        sections.setPayloadProvider(MemoPayloadProvider())
    }

    private fun initListeners() {
        fab.addListener = { findNavController().navigate(EditFragmentDirections.openEdit()) }
        fab.removeListener = {
            subscribeToSnackbarDismissRemoved()
            listViewModel.onItemsRemove(sections.selectedItems)
            fab.shrink()
        }
        fab.archiveListener = {
            subscribeToSnackbarDismissArchived()
            listViewModel.onItemsArchive(sections.selectedItems)
            fab.shrink()
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
            sections.clearSelections()
            fab.shrink()
        }
        sections.setOnItemClickListener { position, item ->
            val action = EditFragmentDirections.openEdit()
            action.memoId = item.id
            action.position = position
            findNavController().navigate(action)
        }
        sections.setSwipeToDismissListener { _, item ->
            subscribeToSnackbarDismissArchived()
            listViewModel.onItemArchive(item)
        }
        sections.setOnSelectionChangedListener { _, _, selectedItems ->
            itemBinder.isSelectionActivated = selectedItems.isNotEmpty()
            onSelectedBackCallback.isEnabled = selectedItems.isNotEmpty()
            when {
                selectedItems.size > 1 -> fab.hideShare()
                selectedItems.isNotEmpty() -> fab.expand()
                else -> fab.shrink()
            }
        }
    }

    private fun initSubscriptions() {
        subscribeToListUpdate()
    }

    private fun subscribeToListUpdate() {
        listViewModel.list.observe(viewLifecycleOwner, Observer {
            sections.set(it)
        })
    }

    private fun subscribeToSnackbarDismissArchived() {
        listViewModel.dismissArchivedLiveData.observe(viewLifecycleOwner, dismissArchivedItemsObserver)
    }

    private fun unsubscribeFromSnackbarDismissArchived() {
        listViewModel.dismissArchivedLiveData.removeObserver(dismissArchivedItemsObserver)
    }

    private fun subscribeToSnackbarDismissRemoved() {
        listViewModel.dismissRemovedLiveData.observe(viewLifecycleOwner, dismissRemovedItemsObserver)
    }

    private fun unsubscribeFromSnackbarDismissRemoved() {
        listViewModel.dismissRemovedLiveData.removeObserver(dismissRemovedItemsObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listViewModel.onScreenClose(sections.data)
        onSelectedBackCallback.remove()
    }
}