package com.gnest.remember.ui.fragments

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras

import com.gnest.remember.App
import com.gnest.remember.R

import com.gnest.remember.services.AlarmService
import com.gnest.remember.ui.layoutmanagers.MyGridLayoutManager
import com.google.android.material.snackbar.Snackbar

import java.util.Locale

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gnest.remember.FAB_TRANSITION
import com.gnest.remember.presentation.ui.EditFragmentDirections
import com.gnest.remember.presentation.ui.memolist.MemoItem
import com.gnest.remember.presentation.ui.memolist.MemoBinder
import com.gnest.remember.presentation.viewmodel.EditMemoListViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_active_list.*
import mva3.adapter.ListSection
import mva3.adapter.MultiViewAdapter
import org.koin.android.viewmodel.ext.android.sharedViewModel

open class ListItemFragment : Fragment() {

    private val compositeDisposable = CompositeDisposable()
    //    private val actionMenu by lazy { ActionMenu(this) }
//    private val listPresenter: IListFragmentPresenter by inject(named(MAIN))
    private val listViewModel: EditMemoListViewModel by sharedViewModel()


    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    }

    open fun onBackPressed() {
        when (myGridLayoutManager?.orientation) {
            RecyclerView.HORIZONTAL -> {
                myGridLayoutManager?.orientation = RecyclerView.VERTICAL
                myGridLayoutManager?.spanCount = mColumnCount
                myGridLayoutManager?.scrollToPosition(myGridLayoutManager?.lastPosition ?: 0)
//                adapter?.isItemsExpanded = false
            }
        }
    }

    private var mColumnCount: Int = 2
    private var mMemoSize: Int = 0
    private var mMargins: Int = 0

    private val adapter: MultiViewAdapter = MultiViewAdapter()
    private val listSections = ListSection<MemoItem>()

    private var itemTouchHelper: ItemTouchHelper? = null
    private var mActionMode: ActionMode? = null
    private var savedState: Bundle? = null

    var myGridLayoutManager: MyGridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val refWatcher = App.getRefWatcher()
        refWatcher?.watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_active_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        add.transitionName = ""
//        add.setOnClickListener {
//            val extras = FragmentNavigatorExtras(add to FAB_TRANSITION)
//            findNavController().navigate(EditFragmentDirections.openEdit(), extras)
//        }

        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)


//        val layoutWeakReference = WeakReference<LinearLayout>(items_fragment)
//        Glide.with(this).load(R.drawable.itemfragment_background_pin_board).into(object : SimpleTarget<Drawable>() {
//            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                layoutWeakReference.get()?.background = resource
//            }
//        })
//        getRenderParams()?.let {
//            mColumnCount = it.columns
//            mMemoSize = it.memoSizePx
//            mMargins = it.marginsPx
//
//        }
//        setSupportActionBar(itemFragmentToolbar)
//        supportActionBar()?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar()?.setHomeButtonEnabled(true)
        myGridLayoutManager = MyGridLayoutManager(requireContext(), mColumnCount)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        adapter.setSpanCount(2)
        layoutManager.spanCount = 2
        layoutManager.spanSizeLookup = adapter.spanSizeLookup
        listSections.setSpanCount(2)
        memoActiveList.layoutManager = layoutManager
//        adapter = MemoListAdapter(
//                onItemClick = { item, view ->
////                    val extras = FragmentNavigatorExtras(view to MEMO_TRANSITION)
//                    val action = EditFragmentDirections.openEdit()
//                    action.memoId = item.id
//                    findNavController().navigate(action)
//                },
//                transitionStartListener = { })
//        adapter?.setActionListener(this)
//        myGridLayoutManager?.setExpandListener(adapter)


        adapter.registerItemBinders(MemoBinder())
        adapter.addSection(listSections)

        memoActiveList.adapter = adapter


//        val callback = ItemTouchHelperCallback(adapter)
//        itemTouchHelper = ItemTouchHelper(callback)
//        itemTouchHelper?.attachToRecyclerView(memoList)


        subscribeToListUpdate()


//        if (!checkNotification()) {
//            presenter.loadData()
//        }
    }

    private fun subscribeToListUpdate() {
        listViewModel.list
                .observe(viewLifecycleOwner, Observer<List<MemoItem>> {
                    listSections.addAll(it)

                    //                    adapter?.memos = it
//                    adapter?.notifyDataSetChanged()
//                    checkStateRestore()
                })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.ab_itemfragment, menu)
    }

    override fun onDetach() {
        super.onDetach()
//        if (adapter != null) {
//            adapter!!.setActionListener(null)
//        }
        if (myGridLayoutManager != null) {
            myGridLayoutManager!!.setExpandListener(null)
        }
        compositeDisposable.dispose()
//        shutDownActionMode()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


//    override fun createPresenter(): IListFragmentPresenter = listPresenter


//    override fun setData(data: RealmResults<Memo>) {
//        adapter?.memos = data
//        adapter?.notifyDataSetChanged()
//        checkStateRestore()
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val state = saveListState()
        outState.putBundle(SAVED_STATE_KEY, state)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            savedState = savedInstanceState.getParcelable(SAVED_STATE_KEY)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
//        return when (item.itemId) {
//            R.id.add -> {
//                openEdit()
//                true
//            }
//            android.R.id.home -> {
//                activity?.drawerLayout?.openDrawer(GravityCompat.START)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
    }

//    override fun onArchiveButtonPressed() {
//        presenter.processArchiveActionOnSelected(adapter!!.selectedIds)
//    }
//
//    override fun onShareButtonPressed() {
//        presenter.processShare(adapter!!.selectedIds)
//    }
//
//    override fun onDeleteButtonPressed() {
//        presenter.processDeleteSelectedMemos(adapter!!.selectedIds)
//    }
//
//    override fun onPerformSwipeDismiss(memoId: Int, memoPosition: Int) {
//        presenter.processSwipeDismiss(memoId, memoPosition)
//    }

//    override fun getArchiveSnackbar(numOfNotes: Int): Snackbar {
//        return createAndShowSnackbar(getArchiveActionPluralForm(getPlural(numOfNotes)), numOfNotes)
//    }
//
//    override fun getDeleteSnackbar(numOfNotes: Int): Snackbar {
//        return createAndShowSnackbar(getRemoveActionPluralForm(getPlural(numOfNotes)), numOfNotes)
//    }
//
//    override fun shareMemoText(memoText: String) {
//        val intent = Intent(Intent.ACTION_SEND)
//        intent.type = "text/plain"
//        intent.putExtra(Intent.EXTRA_TEXT, memoText)
//        val chooserIntent = Intent.createChooser(intent, getString(R.string.send_memo_intent_title))
//        mActionMode!!.finish()
//        startActivity(chooserIntent)
//    }
//
//    override fun setAlarm(memoId: Int, alarmDate: Long, notificationText: String, isAlarmSet: Boolean, isAlarmMovedToMainScreen: Boolean) {
//        val intent = AlarmReceiver.getReceiverIntent(context, memoId, notificationText, alarmDate, isAlarmSet, isAlarmMovedToMainScreen)
//        activity?.sendBroadcast(intent)
//    }
//
//    override fun removeAlarm(memoId: Int) {
//        val activity = activity
//        if (activity != null) {
//            val manager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            val intent = AlarmService.getServiceIntent(activity, null, memoId, true)
//            val pendingIntent = PendingIntent.getService(activity, memoId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//            manager.cancel(pendingIntent)
//        }
//    }
//
//    override fun removeFromCalendar(id: Int) {
//        (requireActivity() as? MainActivity)?.removeFromCalendar(id)
//    }
//
//    override fun isNotificationVisible(id: Int): Boolean {
//        if (Build.VERSION.SDK_INT >= 23) {
//            val activity = activity
//            var manager: NotificationManager? = null
//            if (activity != null) {
//                manager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            }
//            if (manager != null) {
//                for (notification in manager.activeNotifications) {
//                    if (notification.id == id) {
//                        return true
//                    }
//                }
//            }
//            return false
//        } else {
//            val notificationIntent = Intent(context, MainActivity::class.java)
//            val pendingIntent = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_NO_CREATE)
//            return pendingIntent != null
//        }
//    }
//
//    override fun openFromNotification(id: Int) {
//        presenter.processOpenFromNotification(id)
//    }
//
//    override fun closeNotification(id: Int) {
//        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.cancel(id)
//    }
//
//    override fun swapMemos(fromId: Int, fromPosition: Int, toId: Int, toPosition: Int) {
//        presenter.processMemoSwap(fromId, fromPosition, toId, toPosition)
//    }

//    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
//        itemTouchHelper?.startDrag(viewHolder)
//    }

//    override fun getActionMode(): ActionMode? {
//        return mActionMode
//    }

//    override fun showActionMode() {
//        mActionMode = (activity as? AppCompatActivity)?.startSupportActionMode(actionMenu)
//    }

//    override fun shutDownActionMode() {
//        mActionMode?.finish()
//    }

//    override fun updateContextActionMenu(numOfSelectedItems: Int) {
//        mActionMode?.title = when {
//            numOfSelectedItems != 0 -> numOfSelectedItems.toString()
//            else -> ""
//        }
//    }

//    override fun switchMultiSelect(switchedOn: Boolean) {
//        adapter?.switchMultiSelect(switchedOn)
//    }
//
//    override fun clearSelection() {
//        adapter?.clearSelectedList()
//    }

//    override fun setShareButtonVisibility(isVisible: Boolean) {
//        actionMenu.setShareButtonVisibility(isVisible)
//    }

//    override fun onSingleChoiceMemoClicked(memo: Memo) {
//        when {
//            myGridLayoutManager?.orientation == RecyclerView.VERTICAL -> myGridLayoutManager?.openItem(memo.position)
//            else -> {
//                openEdit(memo.id)
//            }
//        }
//    }

//    override fun getAdapter(): MySelectableAdapter? {
//        return adapter
//    }
//
//    override fun getLayoutManager(): MyGridLayoutManager? {
//        return myGridLayoutManager
//    }


    open fun getArchiveActionPluralForm(plural: Int): String {
        return when (plural) {
            2 -> getString(R.string.note_archived_message_2)
            1 -> getString(R.string.note_archived_message_1)
            else -> getString(R.string.note_archived_message)
        }
    }

    private fun checkNotification(): Boolean {
//        requireActivity().intent?.apply {
//            val id = getIntExtra(AlarmService.NOTIFICATION_MEMO_ID, -1)
//            return if (id != -1) {
//                val showOnCurrentScreen = getBooleanExtra(AlarmService.IS_ON_MAIN_SCREEN, true)
//                removeExtra(AlarmService.IS_ON_MAIN_SCREEN)
//                if (showOnCurrentScreen) {
//                    presenter.loadData()
//                } else {
//                    findNavController().navigate(R.id.archive)
//                }
//                removeFromCalendar(id)
//                true
//            } else {
//                false
//            }
//        }
        return false
    }

    private fun checkStateRestore() {
        //Restoring state after config change
        savedState?.let {
            restoreLayoutManagerState(it.getInt(MyGridLayoutManager.LM_SCROLL_ORIENTATION_KEY),
                    it.getBoolean(EXPANDED_KEY),
                    it.getParcelable(SAVED_LAYOUT_MANAGER),
                    it.getInt(MyGridLayoutManager.POSITION_KEY))
            return
        }

        //Restoring state after returning from edit mode
        arguments?.let {
//            ListItemFragmentArgs.fromBundle(it).apply {
//                if (shouldRestore) {
//                    restoreLayoutManagerState(orientation, isExpanded, anchorPosition = position)
//                    return
//                }
//            }
        }

        requireActivity().intent?.apply {
            val id = getIntExtra(AlarmService.NOTIFICATION_MEMO_ID, -1)
            if (id != -1) {
                removeExtra(AlarmService.NOTIFICATION_MEMO_ID)
//                openFromNotification(id)
            }
        }
    }

    private fun restoreLayoutManagerState(orientation: Int, isItemsExtended: Boolean, state: Parcelable? = null, anchorPosition: Int) {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            myGridLayoutManager?.spanCount = 1
        }
        myGridLayoutManager?.orientation = orientation
//        adapter?.isItemsExpanded = isItemsExtended
        state?.let { myGridLayoutManager?.onRestoreInstanceState(it) }
        myGridLayoutManager?.scrollToPosition(anchorPosition)
    }

    private fun createAndShowSnackbar(pluralForm: String?, numOfNotes: Int): Snackbar {
        val text = "$numOfNotes $pluralForm"
/*Have to pass a dummy listener, so action button could be visible
        * Undo action is being processed by rxSnackbar in presenter*/
        val snackbar = Snackbar
                .make(requireView(), text, Snackbar.LENGTH_SHORT)
                .setAction(android.R.string.cancel) { }
                .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
        snackbar.show()
        return snackbar
    }

    private fun getRemoveActionPluralForm(plural: Int): String? {
        return when (plural) {
            2 -> getString(R.string.note_removed_message_2)
            1 -> getString(R.string.note_removed_message_1)
            else -> getString(R.string.note_removed_message)
        }
    }

    private fun getPlural(numOfNotes: Int): Int {
        return when (Locale.getDefault().language) {
            "en" -> if (numOfNotes == 1) 0 else 1
            "ru" -> if (numOfNotes % 10 == 1 && numOfNotes % 100 != 11)
                0
            else if (numOfNotes % 10 in 2..4 &&
                    (numOfNotes % 100 < 10 || numOfNotes % 100 >= 20))
                1
            else
                2
            else -> 0
        }
    }

    private fun saveListState(): Bundle {
        return Bundle().apply {
            putParcelable(SAVED_LAYOUT_MANAGER, myGridLayoutManager?.onSaveInstanceState())
//            putBoolean(EXPANDED_KEY, adapter?.isItemsExpanded ?: false)
            putInt(MyGridLayoutManager.LM_SCROLL_ORIENTATION_KEY, myGridLayoutManager?.orientation
                    ?: RecyclerView.VERTICAL)
            putInt(MyGridLayoutManager.POSITION_KEY, myGridLayoutManager?.lastPosition ?: 0)
            savedState = this
        }
    }


    private fun openEdit(id: Int = -1) {
//        val direction = ListItemFragmentDirections.openEdit().setMemoId(id)
//        findNavController().navigate(direction)
    }

    companion object {

        const val EXPANDED_KEY = "Expanded key"
        private const val SAVED_LAYOUT_MANAGER = "Saved layout manager"
        private const val SAVED_STATE_KEY = "Saved state key"

    }
}