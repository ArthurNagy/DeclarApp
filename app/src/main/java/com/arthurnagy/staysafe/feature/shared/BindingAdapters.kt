package com.arthurnagy.staysafe.feature.shared

import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter

@BindingAdapter("textRes")
fun TextView.textResource(@StringRes textResource: Int) {
    if (textResource != 0) {
        setText(textResource)
    }
}