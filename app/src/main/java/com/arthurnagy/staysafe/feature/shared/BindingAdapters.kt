package com.arthurnagy.staysafe.feature.shared

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import coil.api.load
import java.io.File

@BindingAdapter("textRes")
fun TextView.textResource(@StringRes textResource: Int) {
    if (textResource != 0) {
        setText(textResource)
    }
}

@BindingAdapter("drawableTint")
fun TextView.drawableTint(@ColorRes colorRes: Int) {
    TextViewCompat.setCompoundDrawableTintList(this, if (colorRes != 0) ColorStateList.valueOf(context.color(colorRes)) else null)
}

@BindingAdapter("isVisible")
fun View.setVisible(visible: Boolean) {
    this.isVisible = visible
}

@BindingAdapter("image")
fun ImageView.loadImage(path: String?) {
    path?.let { load(File(it)) }
}