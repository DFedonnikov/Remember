package com.gnest.remember.presentation.ui

import android.content.Context
import android.graphics.drawable.TransitionDrawable
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
import kotlinx.android.synthetic.main.memo_view.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.atan2

class MemoView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) : MotionLayout(context, attributeSet, defStyleAttr),
        CoroutineScope {

    init {
        inflate(context, R.layout.memo_view, this)
        setupListeners()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val colorState = MemoColorState()

    private val swipeDetector = SwipeDetector(context) {
        colorState.switchColor()
//        isReadOnly = false
    }
    private val inputChannel = BroadcastChannel<String>(1)
    private var isFromSetText = false

    private fun setupListeners() {
        launch {
            inputChannel.asFlow()
                    .debounce(100)
                    .flowOn(Dispatchers.IO)
                    .catch { }
                    .collect {
                        when {
                            isFromSetText -> isFromSetText = false
                            else -> textChangeListener?.invoke(it)
                        }
                    }
        }
        memoEditText.doAfterTextChanged { launch { inputChannel.send(it.toString()) } }
        alarmChip.setOnTouchListener { _, _ -> isReadOnly }
    }

    var textChangeListener: ((String) -> Unit)? = null
    var colorChangeListener: ((MemoColor) -> Unit)? = null
    var clickListener: (() -> Unit)? = null
    var onLongClickListener: (() -> Boolean)? = null
    var onMoveListener: (() -> Unit)? = null
    var isReadOnly = false
        set(value) {
            field = value
            clickableArea.isVisible = value
            memoEditText.isFocusable = !value
            when {
                value -> {
                    clickableArea.setOnClickListener { clickListener?.invoke() }
                    clickableArea.setOnLongClickListener { onLongClickListener?.invoke() ?: false }
                    clickableArea.setOnTouchListener { _, event ->
                        swipeDetector.onTouchEvent(event)
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
                        swipeDetector.onTouchEvent(event)
                        if (event.action == MotionEvent.ACTION_MOVE) {
                            onMoveListener?.invoke()
                        }
                        false
                    }
                }
            }
        }

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

        init {
            with(colors[0]) {
                memoEditText.background = background
                alarmChip.checkedIcon = alarm
                alarmChip.setRippleColorResource(chipRippleBackground)
            }
            isReadOnly = false
        }


        internal fun switchColor() {
            val currentColor = colors[currentColorIndex.rem(colors.size)]
            memoEditText.background = currentColor.background
            alarmChip.checkedIcon = currentColor.alarm
            currentColor.background.startTransition(500)
            currentColor.alarm.startTransition(500)
            currentColorIndex++
            val nextColor = colors[currentColorIndex.rem(colors.size)]
            alarmChip.setRippleColorResource(nextColor.chipRippleBackground)
            colorChangeListener?.invoke(nextColor.color)
        }

        internal fun setColor(color: MemoColor) {
            colors.find { it.color == color }?.let { renderData ->
                currentColorIndex = colors.indexOf(renderData)
                memoEditText.background = renderData.background
                alarmChip.checkedIcon = renderData.alarm
                alarmChip.setRippleColorResource(renderData.chipRippleBackground)
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
    }

    fun hideAlarm() {
        alarmChip.isVisible = false
    }

    fun setColor(color: MemoColor) {
        colorState.setColor(color)
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
                    .debounce(100)
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
        if (angle > 70 && angle <= 110) {
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