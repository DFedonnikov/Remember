package com.gnest.remember.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import com.gnest.remember.R
import kotlinx.android.synthetic.main.multi_action_fab.view.*


class MultiActionFab @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) : MotionLayout(context, attributeSet, defStyleAttr) {

    private var isExpanded = false
    private var isShareHidden = false
    private val mainFabTransitionDrawable = TransitionDrawable(arrayOf(
            BitmapDrawable(context.resources, getBitmapFromVectorDrawable(context, R.drawable.ic_fab_add)),
            BitmapDrawable(context.resources, getBitmapFromVectorDrawable(context, R.drawable.ic_delete))
    )).apply { isCrossFadeEnabled = true }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    init {
        inflate(context, R.layout.multi_action_fab, this)
        mainFab.setImageDrawable(mainFabTransitionDrawable)
        mainFab.setOnClickListener {
            when {
                isExpanded -> removeListener?.invoke()
                else -> addListener?.invoke()
            }
        }
        archiveFab.setOnClickListener { archiveListener?.invoke() }
        shareFab.setOnClickListener { shareListener?.invoke() }
    }

    var addListener: (() -> Unit)? = null
    var removeListener: (() -> Unit)? = null
    var archiveListener: (() -> Unit)? = null
    var shareListener: (() -> Unit)? = null
    var isUnarchive = false
        set(value) {
            field = value
            when {
                value -> archiveFab.setImageResource(R.drawable.ic_fab_unarchive)
                else -> archiveFab.setImageResource(R.drawable.ic__fab_archive)
            }
        }

    fun expand() {
        if (!isExpanded) {
            mainFabTransitionDrawable.startTransition(500)
        }
        if (!isExpanded || isShareHidden) {
            fabRoot.transitionToState(R.id.multiFabEnd)
            isShareHidden = false
        }
        isExpanded = true
    }

    fun shrink() {
        if (!isExpanded) return
        mainFabTransitionDrawable.reverseTransition(500)
        fabRoot.transitionToState(R.id.multFabStart)
        isExpanded = false
    }

    fun hideShare() {
        if (isShareHidden) return
        fabRoot.transitionToState(R.id.shareHidden)
        isShareHidden = true
    }
}