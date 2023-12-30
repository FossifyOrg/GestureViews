package com.alexvasilkov.gestures

import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView

class GestureImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ImageView(context, attrs, defStyle) {
    private val imageViewMatrix = Matrix()
    var controller = GestureController(this)

    init {
        controller.addOnStateChangeListener(object : GestureController.OnStateChangeListener {
            override fun onStateChanged(state: State) {
                applyState(state)
            }
        })

        scaleType = ScaleType.MATRIX
    }

    override fun onTouchEvent(event: MotionEvent) = controller.onTouch(this, event)

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        controller.settings.setViewport(width - paddingLeft - paddingRight, height - paddingTop - paddingBottom)
        controller.resetState()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)

        val settings = controller.settings
        val oldWidth = settings.imageWidth
        val oldHeight = settings.imageHeight

        if (drawable == null) {
            settings.setImageSize(0f, 0f)
        } else if (drawable.intrinsicWidth == -1 || drawable.intrinsicHeight == -1) {
            settings.setImageSize(settings.viewportWidth.toFloat(), settings.viewportHeight.toFloat())
        } else {
            settings.setImageSize(drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
        }

        val newWidth = settings.imageWidth
        val newHeight = settings.imageHeight

        if (newWidth > 0f && newHeight > 0f && oldWidth > 0f && oldHeight > 0f) {
            val scaleFactor = Math.min(oldWidth / newWidth, oldHeight / newHeight)
            controller.stateController.setTempZoomPatch(scaleFactor)
            controller.updateState()
            controller.stateController.setTempZoomPatch(0f)
        } else {
            controller.resetState()
        }
    }

    private fun applyState(state: State) {
        state[imageViewMatrix]
        imageMatrix = imageViewMatrix
    }
}
