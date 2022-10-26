package com.gnest.remember.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.ViewCompat

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.gnest.remember.App
import com.gnest.remember.R
import com.gnest.remember.presenter.EditMemoPresenter
import com.gnest.remember.presenter.IEditMemoPresenter
import com.gnest.remember.ui.adapters.ColorSpinnerAdapter
import com.gnest.remember.ui.view.IEditMemoView
import com.hannesdorfmann.mosby3.mvp.MvpFragment

import java.util.Date
import java.util.Locale
import java.util.TimeZone

import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController

//import androidx.navigation.fragment.NavHostFragment.findNavController
import com.gnest.remember.ui.MainActivity
import com.gnest.remember.extensions.setSupportActionBar
import com.gnest.remember.extensions.setupActionBarWithNavController
import com.gnest.remember.extensions.supportActionBar
import com.gnest.remember.services.AlarmReceiver
import com.gnest.remember.ui.layoutmanagers.MyGridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_fragment_edit.*
import kotlinx.android.synthetic.main.content_fragment_edit.*

class EditMemoFragment : MvpFragment<IEditMemoView, IEditMemoPresenter>(), AdapterView.OnItemSelectedListener, IEditMemoView, TimeSetListener {

    private val timePickFragment by lazy { TimePickerFragment() }

    private var color: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
//        val refWatcher = App.getRefWatcher()
//        refWatcher.watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_edit_memo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = ""
        val activity = activity as? AppCompatActivity

        setSupportActionBar(toolbar)
        supportActionBar()?.setDisplayHomeAsUpEnabled(true)
        supportActionBar()?.setHomeButtonEnabled(true)
        activity?.drawerLayout?.let { setupActionBarWithNavController(it) }
//        activity?.addOnBackPressedCallback(this)

        timePickFragment.setTimeSetListener(this)
        editTextMemo.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }
        editTextMemo.typeface = App.FONT
        editTextMemo.textSize = App.FONT_SIZE.toFloat()
        removeAlert.setOnClickListener { alertView ->
            presenter.processRemoveAlarm(getString(R.string.alarm_remove_text))
            setAlarmVisibility(false)
        }
        compactCalendarView.setLocale(TimeZone.getDefault(), Locale.getDefault())
        compactCalendarView.setShouldDrawDaysHeader(true)
        compactCalendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                presenter.processDayClicked(dateClicked)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                presenter.processMonthScroll(firstDayOfNewMonth)
            }
        })

        datePickerButton.setOnClickListener { v -> presenter.processDatePicker() }

        presenter.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timePickFragment.setTimeSetListener(null)
//        requireActivity().removeOnBackPressedCallback(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(TEXT_KEY, editTextMemo?.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState?.getString(TEXT_KEY) != null) {
            editTextMemo.setText(savedInstanceState.getString(TEXT_KEY))
        }
    }

    override fun createPresenter(): IEditMemoPresenter {
//        val memoId = arguments?.let { EditMemoFragmentArgs.fromBundle(it).memoId } ?: 0
        return EditMemoPresenter(0)
    }

    override fun setData(memoText: String, color: String, alarmSet: Boolean) {
        editTextMemo.setText(memoText)
        this.color = color
        setAlarmVisibility(alarmSet)
    }

//    override fun handleOnBackPressed(): Boolean {
//        saveMemo()
//        presenter.processPressBackButton()
//        return true
//    }

    override fun getMemoText(): String {
        return editTextMemo.text.toString()
    }

    override fun getMemoColor(): String? {
        return color
    }

    override fun getAlarmSetText(): String? {
        return getString(R.string.alarm_set_text)
    }

    override fun returnFromEdit(memoPosition: Int) {
//        findNavController(this).apply {
//            val direction = EditMemoFragmentDirections.openMain()
//            if (memoPosition != -1) {
//                direction
//                        .setOrientation(MyGridLayoutManager.HORIZONTAL)
//                        .setPosition(memoPosition)
//                        .setIsExpanded(true).shouldRestore = true
//            }
//            navigate(direction)
//        }
    }

    override fun addToCalendar(memoId: Int, description: String, timeInMillis: Long) {
        (requireActivity() as? MainActivity)?.addToCalendar(memoId, description, timeInMillis)
    }

    override fun removeFromCalendar(memoId: Int) {
        (requireActivity() as? MainActivity)?.removeFromCalendar(memoId)
    }

    override fun setAlarm(isSet: Boolean, alarmDate: Long, notificationText: String, id: Int) {
        val intent = AlarmReceiver.getReceiverIntent(requireContext(), id, notificationText, alarmDate, isSet, true);
        requireActivity().sendBroadcast(intent)
    }

    override fun showAlarmToast(alarmText: String) {
        Toast.makeText(context, alarmText, Toast.LENGTH_LONG).show()
    }

    override fun setCalendarExpanded(isCalendarExpanded: Boolean) {
        appBarLayout.setExpanded(isCalendarExpanded, true)
    }

    override fun showTimePicker(hour: Int, minute: Int) {
        val bundle = Bundle()
        bundle.putInt(TimePickerFragment.HOUR_KEY, hour)
        bundle.putInt(TimePickerFragment.MINUTE_KEY, minute)
        timePickFragment.arguments = bundle
        timePickFragment.show(requireFragmentManager(), "timePicker")
    }

    override fun animateArrow(isCalendarExpanded: Boolean) {
        val rotation = if (isCalendarExpanded) 0 else 180
        ViewCompat.animate(datePickerArrow).rotation(rotation.toFloat()).start()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.ab_editfragment, menu)
        val item = menu.findItem(R.id.item_color_choice_spinner)
        val colorChoiceSpinner = item.actionView as? AppCompatSpinner
        colorChoiceSpinner?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        val colorSpinnerAdapter = ColorSpinnerAdapter(context)
        colorChoiceSpinner?.adapter = colorSpinnerAdapter
        colorChoiceSpinner?.onItemSelectedListener = this
        color?.let { colorChoiceSpinner?.setSelection(ColorSpinnerAdapter.Colors.valueOf(it).ordinal) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                return false
//                return handleOnBackPressed()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        color = ColorSpinnerAdapter.Colors.values()[i].name
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {

    }

    override fun setSubtitle(subtitle: String) {
        datePickerTextView.text = subtitle

    }

    override fun setCurrentDate(date: Date) {
        compactCalendarView.setCurrentDate(date)
    }

    override fun onTimeSet(hour: Int, minute: Int) {
        presenter.processTimeSet(hour, minute)
    }

    override fun setAlarmVisibility(visible: Boolean) {
        removeAlert.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    private fun saveMemo() {
        presenter.saveData()
    }


    private fun hideKeyboard(v: View) {
        val inputMethodManager = App.self().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)
    }

    class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
        private var mListener: TimeSetListener? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            var hour = 0
            var minute = 0
            if (arguments != null) {
                hour = requireArguments()!!.getInt(HOUR_KEY)
                minute = requireArguments()!!.getInt(MINUTE_KEY)
            }
            return TimePickerDialog(activity, this, hour, minute,
                    DateFormat.is24HourFormat(activity))
        }

        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            mListener?.onTimeSet(hourOfDay, minute)

        }

        fun setTimeSetListener(listener: TimeSetListener?) {
            this.mListener = listener
        }

        companion object {

            const val HOUR_KEY = "HOUR_KEY"

            const val MINUTE_KEY = "MINUTE_KEY"
        }

    }

    companion object {

        private const val TEXT_KEY = "Text key"
    }
}