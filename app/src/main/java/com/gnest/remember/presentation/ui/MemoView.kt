package com.gnest.remember.presentation.ui

import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.gnest.remember.R
import com.gnest.remember.extensions.setPaddingOnly
import kotlinx.android.synthetic.main.memo_view.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import org.joda.time.DateTime
import kotlin.coroutines.CoroutineContext
import kotlin.math.atan2

class MemoView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) : MotionLayout(context, attributeSet, defStyleAttr),
        CoroutineScope {

    private val datePicker by lazy { DateTimePicker(context) }
    private val paddingBottomReadonly = context.resources.getDimensionPixelSize(R.dimen.memo_padding_bottom_readonly)
    private val paddingBottomEdit = context.resources.getDimensionPixelSize(R.dimen.memo_padding_bottom_edit)
    var isReadOnly = false
        set(value) {
            field = value
            clickableArea.isVisible = value
            memoEditText.isFocusable = !value
            memoEditText.setPaddingOnly(bottom = when {
                value -> paddingBottomReadonly
                else -> paddingBottomEdit
            })
            colorSwipeArea.setOnTouchListener { _, event ->
                swipeDetector.onTouchEvent(event)
                true
            }
            when {
                value -> {
                    clickableArea.setOnClickListener { clickListener?.invoke() }
                    clickableArea.setOnLongClickListener { onLongClickListener?.invoke() ?: false }
                    clickableArea.setOnTouchListener { _, event ->
                        if (event.action == MotionEvent.ACTION_MOVE) {
                            onMoveListener?.invoke()
                        }
                        false
                    }
                }
                else -> {
                    memoEditText.setOnClickListener { clickListener?.invoke() }
                    memoEditText.setOnLongClickListener { onLongClickListener?.invoke() ?: false }
                    memoEditText.setOnTouchListener { _, event ->
                        if (event.action == MotionEvent.ACTION_MOVE) {
                            onMoveListener?.invoke()
                        }
                        false
                    }
                }
            }
        }

    init {
        inflate(context, R.layout.memo_view, this)
        setupListeners()
        isReadOnly = false
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val colorState = MemoColorState()

    private val swipeDetector = SwipeDetector(context) { colorState.switchColor() }
    private val inputChannel = BroadcastChannel<String>(1)
    private var isFromSetText = false
    private var isAlarmSet = false

    private fun setupListeners() {
        launch {
            inputChannel.asFlow()
                    .debounce(100)
                    .flowOn(Dispatchers.IO)
                    .catch { }
                    .collect {
                        memoText = it
                        when {
                            isFromSetText -> isFromSetText = false
                            else -> textChangeListener?.invoke(it)
                        }
                    }
        }
        memoEditText.doAfterTextChanged { launch { inputChannel.send(it.toString()) } }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            memoEditText.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                if (scrollY > 0) {
                    memoRoot.transitionToState(R.id.alarmHide)
                } else {
                    memoRoot.transitionToState(R.id.alarmShow)
                }
            }
        }

        alarmChip.setOnClickListener { datePicker.show() }
        alarmChip.setOnCloseIconClickListener {
            resetAlarm()
            alarmChip.isCloseIconVisible = false
            onAlarmDismissListener?.invoke()
        }
        datePicker.onDateTimeSetListener = { onDateChosen(it) }
    }

    private fun resetAlarm() {
        isAlarmSet = false
        alarmChip.text = context.getString(R.string.set_alarm)
        datePicker.reset()
        colorState.updateAlarmChipIcon(false)
    }

    private fun onDateChosen(date: DateTime) {
        isAlarmSet = true
        setAlarmChipText(date)
        alarmChip.isCloseIconVisible = true
        colorState.updateAlarmChipIcon(true)
        onAlarmSetListener?.invoke(date)
    }

    private fun setAlarmChipText(date: DateTime) {
        val dateText = "${date.dayOfMonth}.${date.monthOfYear}.${date.year} ${date.hourOfDay}:${date.minuteOfHour}"
        alarmChip.text = dateText
    }

    var textChangeListener: ((String) -> Unit)? = null
    var colorChangeListener: ((MemoColor) -> Unit)? = null
    var clickListener: (() -> Unit)? = null
    var onLongClickListener: (() -> Boolean)? = null
    var onMoveListener: (() -> Unit)? = null
    var onAlarmSetListener: ((DateTime) -> Unit)? = null
    var onAlarmDismissListener: (() -> Unit)? = null

    override fun setSelected(isSelected: Boolean) {
        super.setSelected(isSelected)
        memoRoot.getConstraintSet(R.id.start)
        memoRoot.getTransition(R.id.scale_transition)
        when {
            isSelected -> memoRoot.transitionToEnd()
            else -> memoRoot.transitionToStart()
        }
    }

    internal inner class MemoColorState {

        private val colors =
                arrayOf(
                        MemoRenderData(
                                MemoColor.BLUE,
                                R.drawable.memo_blue,
                                R.drawable.memo_emerald,
                                R.drawable.ic_alarm_blue,
                                R.drawable.ic_alarm_emerald,
                                R.color.blue),
                        MemoRenderData(
                                MemoColor.EMERALD,
                                R.drawable.memo_emerald,
                                R.drawable.memo_yellow,
                                R.drawable.ic_alarm_emerald,
                                R.drawable.ic_alarm_yellow,
                                R.color.emerald),
                        MemoRenderData(
                                MemoColor.YELLOW,
                                R.drawable.memo_yellow,
                                R.drawable.memo_purple,
                                R.drawable.ic_alarm_yellow,
                                R.drawable.ic_alarm_purple,
                                R.color.yellow),
                        MemoRenderData(
                                MemoColor.PURPLE,
                                R.drawable.memo_purple,
                                R.drawable.memo_blue,
                                R.drawable.ic_alarm_purple,
                                R.drawable.ic_alarm_blue,
                                R.color.purple))

        private var currentColorIndex = 0
        private val alarmDisabledDrawable = context.getDrawable(R.drawable.ic_alarm_disabled)

        init {
            with(colors[0]) {
                memoEditText.background = background
                alarmChip.chipIcon = alarm
                alarmChip.setRippleColorResource(chipRippleBackground)
            }
        }

        internal fun switchColor() {
            val currentColor = colors[currentColorIndex]
            memoEditText.background = currentColor.background
            updateAlarmChipIcon(isAlarmSet)
            currentColor.background.startTransition(500)
            currentColor.alarm.startTransition(500)
            currentColorIndex = currentColorIndex.inc().rem(colors.size)
            val nextColor = colors[currentColorIndex]
            alarmChip.setRippleColorResource(nextColor.chipRippleBackground)
            colorChangeListener?.invoke(nextColor.color)
        }

        internal fun setColor(color: MemoColor) {
            colors.find { it.color == color }?.let { renderData ->
                currentColorIndex = colors.indexOf(renderData)
                memoEditText.background = renderData.background
                updateAlarmChipIcon(isAlarmSet)
                alarmChip.setRippleColorResource(renderData.chipRippleBackground)
            }
        }

        fun updateAlarmChipIcon(isEnabled: Boolean) {
            when {
                isEnabled -> {
                    val currentColor = colors[currentColorIndex]
                    alarmChip.chipIcon = currentColor.alarm
                }
                else -> alarmChip.chipIcon = alarmDisabledDrawable
            }
        }
    }

    internal inner class MemoRenderData(internal val color: MemoColor,
                                        @DrawableRes startBackground: Int,
                                        @DrawableRes endBackground: Int,
                                        @DrawableRes startAlarmIcon: Int,
                                        @DrawableRes endAlarmIcon: Int,
                                        @ColorRes internal val chipRippleBackground: Int) {

        internal val background by lazy {
            with(context) {
                TransitionDrawable(arrayOf(getDrawable(startBackground), getDrawable(endBackground)))
            }
        }

        internal val alarm by lazy {
            with(context) {
                TransitionDrawable(arrayOf(getDrawable(startAlarmIcon), getDrawable(endAlarmIcon)))
            }
        }
    }

    var memoText = ""

    fun setText(text: String) {
        if (memoText == text) return
        isFromSetText = true
        this.memoText = text
        memoEditText.setText(text)
        memoEditText.setSelection(text.length)
    }

    fun hideAlarm() {
        alarmChip.isVisible = false
    }

    fun setColor(color: MemoColor) {
        colorState.setColor(color)
    }

    fun setAlarm(date: DateTime?) {
        date?.let {
            isAlarmSet = true
            setAlarmChipText(it)
            alarmChip.isCloseIconVisible = true
            colorState.updateAlarmChipIcon(true)
            datePicker.updateDate(it)
        } ?: resetAlarm()
    }

    enum class MemoColor {
        BLUE,
        EMERALD,
        YELLOW,
        PURPLE
    }
}

@FlowPreview
@ExperimentalCoroutinesApi
private class SwipeDetector(context: Context?,
                            private val onSwipeUp: () -> Unit) : GestureDetector.OnGestureListener, CoroutineScope {

    val channel = BroadcastChannel<Unit>(1)

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    private lateinit var job: Job

    init {
        observeSwipe()
    }

    private fun observeSwipe() {
        job = launch {
            channel.asFlow()
                    .debounce(200)
                    .flowOn(Dispatchers.IO)
                    .catch { }
                    .collect { onSwipeUp() }
        }
    }

    fun onTouchEvent(event: MotionEvent) {
        gestureDetector.onTouchEvent(event)
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        e1 ?: return false
        e2 ?: return false
        val angle = Math.toDegrees(atan2((e1.y - e2.y).toDouble(), (e2.x - e1.x).toDouble())).toFloat()
        if (angle in 70.0..110.0) {
            launch { channel.send(Unit) }
            return true
        }
        return false
    }

    override fun onShowPress(e: MotionEvent?) {}

    override fun onSingleTapUp(e: MotionEvent?) = false

    override fun onDown(e: MotionEvent?) = false

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean = false

    override fun onLongPress(e: MotionEvent?) {}

    private val gestureDetector = GestureDetectorCompat(context, this)
}