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
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.viewpager.widget.ViewPager
import org.koin.androidx.viewmodel.ext.android.getViewModel

@ColorInt
fun Context.color(@ColorRes colorRes: Int): Int = ContextCompat.getColor(this, colorRes)

fun Context.drawable(@DrawableRes drawableRes: Int): Drawable? = ContextCompat.getDrawable(this, drawableRes)

@Px
fun Context.dimensionPixel(@DimenRes dimension: Int): Int = resources.getDimensionPixelSize(dimension)

inline fun <reified VM : ViewModel> Fragment.parentViewModel(): Lazy<VM> = lazy {
    requireParentFragment().getViewModel<VM>()
}

fun Fragment.addPageChangeListenerTo(
    viewPager: ViewPager,
    onPageSelected: ((position: Int) -> Unit)? = null,
    onPageScrolled: ((position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit)? = null
) {
    val pageChangeListener = LifecyclePageChangeListener(viewPager, onPageSelected, onPageScrolled)
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            viewLifecycleOwnerLiveData.observe(this@addPageChangeListenerTo) { viewLifecycleOwner ->
                viewLifecycleOwner?.lifecycle?.addObserver(pageChangeListener)
            }
        }
    })
}

class LifecyclePageChangeListener(
    private val viewPager: ViewPager,
    private val onPageSelected: ((position: Int) -> Unit)? = null,
    private val onPageScrolled: ((position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit)? = null,
    private val onPageScrollStateChanged: ((state: Int) -> Unit)? = null
) : DefaultLifecycleObserver {

    private val pageChangeCallback: ViewPager.SimpleOnPageChangeListener

    init {
        pageChangeCallback = object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                this@LifecyclePageChangeListener.onPageSelected?.invoke(position)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                this@LifecyclePageChangeListener.onPageScrolled?.invoke(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageScrollStateChanged(state: Int) {
                this@LifecyclePageChangeListener.onPageScrollStateChanged?.invoke(state)
            }
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        viewPager.addOnPageChangeListener(pageChangeCallback)
    }

    override fun onPause(owner: LifecycleOwner) {
        viewPager.removeOnPageChangeListener(pageChangeCallback)
    }
}