package com.example.submission2.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.submission2.R
import com.google.android.material.textfield.TextInputEditText

class MyEditText : TextInputEditText, View.OnTouchListener {

    private lateinit var actionButtonImage: Drawable
    private var isPasswordType = false
    private var isEmailType = false
    private var isPasswordVisible = false
    var isValid = false

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
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        setActionButtonImage()
        setOnTouchListener(this)

        doOnTextChanged { s, _, _, _ ->
            setFormValidation(s.toString())
            if (s.toString().isNotEmpty()) showActionButton(true) else showActionButton(false)
        }
    }

    private fun setFormValidation(s: String) {
        if (isPasswordType) {
            isValid = s.length >= 8
            error = if (!isValid) context.getString(R.string.password_min8_message) else null
        } else if (isEmailType) {
            isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()
            error = if (!isValid) context.getString(R.string.email_format_message) else null
        } else {
            isValid = true
        }
    }

    private fun setActionButtonImage() {
        actionButtonImage =
            ContextCompat.getDrawable(context, R.drawable.round_clear_24) as Drawable
        when ((inputType - 1)) {
            InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                actionButtonImage = ContextCompat.getDrawable(
                    context,
                    R.drawable.baseline_visibility_24
                ) as Drawable
                isPasswordType = true
            }

            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                isEmailType = true
            }
        }
    }

    private fun showActionButton(isShow: Boolean) {
        if (isShow)
            setButtonDrawables(endOfTheText = actionButtonImage)
        else
            setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val actionButtonStart: Float
            val actionButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                actionButtonEnd = (actionButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < actionButtonEnd -> isClearButtonClicked = true
                }
            } else {
                actionButtonStart =
                    (width - paddingEnd - actionButtonImage.intrinsicWidth).toFloat()
                when {
                    event.x > actionButtonStart -> isClearButtonClicked = true
                }
            }
            return if (isClearButtonClicked) setActionListener(event) else false
        }
        return false
    }

    private fun setActionListener(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (text != null) {
                    if (isPasswordType) {
                        changePasswordVisibility()
                    } else {
                        text?.clear()
                        showActionButton(false)
                    }
                }
                return true
            }

            else -> return false
        }
    }

    private fun changePasswordVisibility() {
        if (isPasswordVisible) {
            transformationMethod = PasswordTransformationMethod.getInstance()
            isPasswordVisible = false
            actionButtonImage =
                ContextCompat.getDrawable(context, R.drawable.baseline_visibility_24) as Drawable
        } else {
            transformationMethod = HideReturnsTransformationMethod.getInstance()
            isPasswordVisible = true
            actionButtonImage =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.baseline_visibility_off_24
                ) as Drawable
        }
        showActionButton(true)
        setSelection(text?.length ?: 0)
    }
}