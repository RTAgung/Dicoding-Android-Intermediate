package com.example.submission2.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.example.submission2.R
import com.google.android.material.button.MaterialButton

class MyButton : MaterialButton {

    private lateinit var disabledIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        showDrawable(isEnabled)
    }

    private fun init() {
        disabledIcon =
            ContextCompat.getDrawable(context, R.drawable.round_disabled_by_default_24) as Drawable
        disabledIcon.setTint(ContextCompat.getColor(context, android.R.color.darker_gray))
        showDrawable(isEnabled)
    }

    private fun showDrawable(enabled: Boolean) {
        if (!enabled) setCompoundDrawablesRelativeWithIntrinsicBounds(
            disabledIcon,
            null,
            disabledIcon,
            null
        )
        else setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
    }
}