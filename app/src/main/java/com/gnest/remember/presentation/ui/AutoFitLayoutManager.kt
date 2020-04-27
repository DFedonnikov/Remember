package com.gnest.remember.presentation.ui

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class AutoFitLayoutManager(private val context: Context, spanCount: Int) : GridLayoutManager(context, spanCount) {

    private val density = context.resources.displayMetrics.density
    private val screenWidth
        get() = context.resources.displayMetrics.widthPixels
    private val space = (8 * density).roundToInt()

    override fun generateDefaultLayoutParams() =
            scaledLayoutParams(super.generateDefaultLayoutParams())

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?) =
            scaledLayoutParams(super.generateLayoutParams(lp))

    override fun generateLayoutParams(c: Context?, attrs: AttributeSet?) =
            scaledLayoutParams(super.generateLayoutParams(c, attrs))

    private fun scaledLayoutParams(layoutParams: RecyclerView.LayoutParams): RecyclerView.LayoutParams {
        return layoutParams.apply {
            val width = (screenWidth - 2 * space * (spanCount + 1)).toFloat() / spanCount + 5
            val height = width * 1.095f
            this.width = width.roundToInt()
            this.height = height.roundToInt()
        }
    }
}