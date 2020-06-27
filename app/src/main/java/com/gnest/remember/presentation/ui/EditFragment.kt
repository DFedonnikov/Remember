package com.gnest.remember.presentation.ui

import android.Manifest
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.transition.ChangeBounds
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.gnest.remember.R
import com.gnest.remember.extensions.dismissNotificationAlarm
import com.gnest.remember.extensions.scheduleNotificationAlarm
import com.gnest.remember.extensions.showToast
import com.gnest.remember.presentation.ui.memolist.MemoItem
import com.gnest.remember.presentation.viewmodel.EditMemoListViewModel
import com.gnest.remember.presentation.viewmodel.SingleEventObserver
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_edit.*
import org.koin.android.viewmodel.ext.android.viewModel


class EditFragment : Fragment() {

    private val viewModel: EditMemoListViewModel by viewModel()
    private val args: EditFragmentArgs by navArgs()
    private val rxPermissions by lazy { RxPermissions(this) }
    private val disposable = CompositeDisposable()

    val adapter = EditMemoListAdapter(
            transitionStartListener = { },
            itemChangedListener = { viewModel.onItemChanged(it) },
            alarmSetListener = {
                context?.scheduleNotificationAlarm(it)
                requestCalendarPermissions { viewModel.saveToCalendar(it) }
            },
            alarmDismissListener = {
                context?.dismissNotificationAlarm(it.id)
                requestCalendarPermissions { viewModel.removeFromCalendar(it.id) }
            })

    private fun requestCalendarPermissions(onGranted: () -> Unit) {
        rxPermissions.requestEachCombined(Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR)
                .subscribe { permission ->
                    when {
                        permission.granted -> onGranted()
                        permission.shouldShowRequestPermissionRationale -> context?.showToast(R.string.calendar_perm_denied_toast)
                        else -> context?.showToast(R.string.calendar_perm_denied_without_ask_again_toast)
                    }
                }
                .addTo(disposable)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        sharedElementEnterTransition = ChangeBounds().apply {
            duration = 300
        }
//        postponeEnterTransition()
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        memoEditList.adapter = adapter
        memoEditList.layoutManager = RatioLayoutManager(requireContext())
        memoEditList.doOnNextLayout { viewModel.onListLayoutComplete() }
        memoEditList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            private val offset = requireContext().resources.getDimensionPixelOffset(R.dimen.memo_item_offset)

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(offset, offset, offset, offset)
            }
        })
        LinearSnapHelper().attachToRecyclerView(memoEditList)
        viewModel.init(args.memoId, args.isArchived)
        initSubscriptions()
    }


    private fun initSubscriptions() {
        viewModel.scrollToLiveData.observe(viewLifecycleOwner, Observer { memoEditList.smoothScrollToPosition(args.position) })
        viewModel.list.observe(viewLifecycleOwner, object : Observer<List<MemoItem>> {
            override fun onChanged(items: List<MemoItem>) {
                adapter.submitList(items)
                viewModel.list.removeObserver(this)
            }
        })
        viewModel.calendarEventSaveState.observe(viewLifecycleOwner, SingleEventObserver {
            context?.showToast(it.message)
        })
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }
}

open class RatioLayoutManager(context: Context,
                              @RecyclerView.Orientation orientation: Int = RecyclerView.HORIZONTAL,
                              reverseLayout: Boolean = false) : LinearLayoutManager(context, orientation, reverseLayout) {

    var ratio = 0.93f

    private val horizontalSpace get() = width - paddingStart - paddingEnd

    private val verticalSpace get() = height - paddingTop - paddingBottom

    override fun generateDefaultLayoutParams() =
            scaledLayoutParams(super.generateDefaultLayoutParams())

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?) =
            scaledLayoutParams(super.generateLayoutParams(lp))

    override fun generateLayoutParams(c: Context?, attrs: AttributeSet?) =
            scaledLayoutParams(super.generateLayoutParams(c, attrs))

    private fun scaledLayoutParams(layoutParams: RecyclerView.LayoutParams) = when {
        itemCount > 1 -> layoutParams.apply {
            when (orientation) {
                HORIZONTAL -> width = (horizontalSpace * ratio + 0.5).toInt()
                VERTICAL -> height = (verticalSpace * ratio + 0.5).toInt()
            }
        }
        else -> layoutParams
    }

}