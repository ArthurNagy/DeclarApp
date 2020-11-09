package com.arthurnagy.staysafe.feature.newdocument.statement.personaldata

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.StatementPersonalDataBinding
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.doIfAboveVersion
import com.arthurnagy.staysafe.feature.shared.hideKeyboard
import com.arthurnagy.staysafe.feature.shared.sharedGraphViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.halcyonmobile.android.common.extensions.navigation.findSafeNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

class StatementPersonalDataFragment : Fragment(R.layout.fragment_statement_personal_data) {

    private val sharedViewModel: NewDocumentViewModel by sharedGraphViewModel(navGraphId = R.id.nav_new_document)
    private val viewModel: StatementPersonalDataViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doIfAboveVersion(Build.VERSION_CODES.LOLLIPOP_MR1) {
            exitTransition = Slide(Gravity.START)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = StatementPersonalDataBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@StatementPersonalDataFragment.viewModel
        }
        with(binding) {
            toolbar.setNavigationOnClickListener {navigateBack() }
            clickableBirthDate.setOnClickListener { openBirthDateSelection() }
            next.setOnClickListener {
                findSafeNavController().navigate(
                    StatementPersonalDataFragmentDirections.actionStatementPersonalDataFragmentToStatementRouteDataFragment(),
                    FragmentNavigatorExtras(
                        binding.toolbar to getString(R.string.transition_toolbar),
                        binding.next to getString(R.string.transition_action)
                    )
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { navigateBack() }
    }

    private fun navigateBack(){
        findNavController().navigateUp()
        view?.hideKeyboard()
    }

    private fun openBirthDateSelection() {
        val limitTimestamp = LocalDate.now().atStartOfDay().minusYears(BIRTH_DATE_MIN_AGE).toInstant(ZoneOffset.UTC).toEpochMilli()
        val birthdayTimestamp = viewModel.birthDate.value ?: limitTimestamp
        val constraints = CalendarConstraints.Builder()
            .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
            .setValidator(DateValidatorPointBackward.before(limitTimestamp))
            .setOpenAt(birthdayTimestamp)
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(birthdayTimestamp)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.addOnPositiveButtonClickListener(viewModel::onBirthDateSelected)
        datePicker.show(childFragmentManager, datePicker.toString())
    }

    companion object {
        private const val BIRTH_DATE_MIN_AGE = 16L
    }
}