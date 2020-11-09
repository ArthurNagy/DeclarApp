package com.arthurnagy.staysafe.feature.shared

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateMargins
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.arthurnagy.staysafe.feature.newdocument.statement.routedata.StatementRouteDataViewModel
import java.io.File

@BindingAdapter("textRes")
fun TextView.textResource(@StringRes textResource: Int) {
    if (textResource != 0) {
        setText(textResource)
    }
}

@BindingAdapter("textResList")
fun TextView.textResouceList(textResourceList: List<Int>?) {
    if (!textResourceList.isNullOrEmpty()) {
        text = textResourceList.joinToString { context.getString(it) }
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

@BindingAdapter("isInvisible")
fun View.isInvisible(invisible: Boolean) {
    this.isInvisible = invisible
}

@BindingAdapter("image")
fun ImageView.loadImage(path: String?) {
    path?.let { load(File(it)) }
}

@BindingAdapter("motiveChecked")
fun CheckBox.setMotiveChecked(motive: StatementRouteDataViewModel.MotiveUiModel) {
    isChecked = motive.selected
}

@BindingAdapter("marginStart", "marginTop", "marginEnd", "marginBottom", requireAll = false)
fun View.updateMargins(marginStart: Float? = null, marginTop: Float? = null, marginEnd: Float? = null, marginBottom: Float? = null) {
    val layoutParams: ViewGroup.MarginLayoutParams? = layoutParams as? ViewGroup.MarginLayoutParams
    layoutParams?.apply {
        updateMargins(
            left = marginStart?.toInt() ?: leftMargin,
            top = marginTop?.toInt() ?: topMargin,
            right = marginEnd?.toInt() ?: rightMargin,
            bottom = marginBottom?.toInt() ?: bottomMargin
        )
    }
}