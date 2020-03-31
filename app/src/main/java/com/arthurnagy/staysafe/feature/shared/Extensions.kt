package com.arthurnagy.staysafe.feature.shared

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.core.model.Motive
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

@ColorInt
fun Context.color(@ColorRes colorRes: Int): Int = ContextCompat.getColor(this, colorRes)

fun Context.drawable(@DrawableRes drawableRes: Int): Drawable? = ContextCompat.getDrawable(this, drawableRes)

@Px
fun Context.dimensionPixel(@DimenRes dimension: Int): Int = resources.getDimensionPixelSize(dimension)

inline fun <reified VM : ViewModel> Fragment.parentViewModel(): Lazy<VM> = lazy {
    requireParentFragment().getViewModel<VM>()
}

inline fun <reified VM : ViewModel> Fragment.parentGraphViewModel(@IdRes navGraphId: Int): Lazy<VM> = lazy {
    requireParentFragment().sharedGraphViewModel<VM>(navGraphId = navGraphId).value
}

inline fun <reified VM : ViewModel> Fragment.sharedGraphViewModel(
    @IdRes navGraphId: Int,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = lazy {
    val store = findNavController().getViewModelStoreOwner(navGraphId).viewModelStore
    getKoin().getViewModel(ViewModelParameter(VM::class, qualifier, parameters, null, store, null))
}

fun ConstraintLayout.updateConstraintSet(updateConstraints: ConstraintSet.() -> Unit) {
    with(ConstraintSet()) {
        clone(this@updateConstraintSet)
        updateConstraints()
        applyTo(this@updateConstraintSet)
    }
}

fun Bitmap.tint(@ColorInt tintColor: Int): Bitmap {
    val paint = Paint()
    paint.colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
    val bitmapResult = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmapResult)
    canvas.drawBitmap(this, 0F, 0F, paint)
    return bitmapResult
}

val Motive.labelRes: Int
    get() = when (this) {
        Motive.PROFESSIONAL_INTERESTS -> R.string.motive_professional_interests
        Motive.NECESSITY_PROVISIONING -> R.string.motive_necessity_provisioning
        Motive.MEDICAL_ASSISTANCE -> R.string.motive_medical_assistance
        Motive.JUSTIFIED_HELP -> R.string.motive_justified_help
        Motive.PHYSICAL_ACTIVITY -> R.string.motive_physical_activity
        Motive.AGRICULTURAL_ACTIVITIES -> R.string.motive_agricultural_activities
        Motive.BLOOD_DONATION -> R.string.motive_blood_donation
        Motive.VOLUNTEERING -> R.string.motive_volunteering
        Motive.COMMERCIALIZE_AGRICULTURAL_PRODUCES -> R.string.motive_commercialize_agricultural_produces
        Motive.PROFESSIONAL_ACTIVITY_NECESSITIES -> R.string.motive_professional_activity_necessities
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