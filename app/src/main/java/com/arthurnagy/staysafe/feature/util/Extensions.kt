package com.arthurnagy.staysafe.feature.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

@ColorInt
fun Context.color(@ColorRes colorRes: Int): Int = ContextCompat.getColor(this, colorRes)

fun Context.drawable(@DrawableRes drawableRes: Int): Drawable? = ContextCompat.getDrawable(this, drawableRes)

@Px
fun Context.dimensionPixel(@DimenRes dimension: Int): Int = resources.getDimensionPixelSize(dimension)

inline fun <reified VM : ViewModel> Fragment.parentViewModel(): Lazy<VM> = lazy {
    requireParentFragment().getViewModel<VM>()
}