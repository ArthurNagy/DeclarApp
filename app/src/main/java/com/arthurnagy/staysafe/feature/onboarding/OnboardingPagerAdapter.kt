package com.arthurnagy.staysafe.feature.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.arthurnagy.staysafe.OnboardingItemBinding
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.feature.shared.dimensionPixel
import com.arthurnagy.staysafe.feature.shared.updateConstraintSet

class OnboardingPagerAdapter : PagerAdapter() {

    val items: List<OnboardingUiModel> = listOf(
        OnboardingUiModel(
            message = R.string.onboarding_desc_first,
            firstIcon = R.drawable.ic_account_details_24dp,
            secondIcon = R.drawable.ic_home_edit_24dp,
            thirdIcon = R.drawable.ic_text_box_check_24dp
        ),
        OnboardingUiModel(
            message = R.string.onboarding_desc_second,
            firstIcon = R.drawable.ic_assignment_24dp,
            secondIcon = R.drawable.ic_print_24dp,
            thirdIcon = R.drawable.ic_clipboard_list_24dp
        ),
        OnboardingUiModel(
            message = R.string.onboarding_desc_third,
            firstIcon = R.drawable.ic_cloud_off_24dp,
            secondIcon = R.drawable.ic_database_24dp,
            thirdIcon = R.drawable.ic_shield_account_24dp
        )
    )

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun getCount(): Int = items.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = OnboardingItemBinding.inflate(LayoutInflater.from(container.context), container, false)
        val circleRadius = container.context.dimensionPixel(R.dimen.onboarding_circle_radius)
        with(binding) {
            uiModel = items[position]
            (root as ConstraintLayout).updateConstraintSet {
                constrainCircle(iconFirst.id, iconBackground.id, circleRadius, FIRST_ICON_ANGLE + (position * EXTRA_ANGLE))
                constrainCircle(iconSecond.id, iconBackground.id, circleRadius, SECOND_ICON_ANGLE + (position * EXTRA_ANGLE))
                constrainCircle(iconThird.id, iconBackground.id, circleRadius, THIRD_ICON_ANGLE + (position * EXTRA_ANGLE))
            }
            container.addView(root)
        }
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as? View)
    }

    data class OnboardingUiModel(
        @StringRes val message: Int,
        @DrawableRes val firstIcon: Int,
        @DrawableRes val secondIcon: Int,
        @DrawableRes val thirdIcon: Int
    )

    companion object {
        private const val EXTRA_ANGLE = 30F
        private const val FIRST_ICON_ANGLE = 300F
        private const val SECOND_ICON_ANGLE = 60F
        private const val THIRD_ICON_ANGLE = 180F
    }
}