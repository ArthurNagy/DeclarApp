package com.arthurnagy.staysafe.feature.newdocument.statement.personaldata

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.StatementPersonalDataBinding
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.halcyonmobile.android.common.extensions.navigation.findSafeNavController
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

class StatementPersonalDataFragment : Fragment(R.layout.fragment_statement_personal_data) {

    private val viewModelFactory by inject<NewDocumentViewModel.Factory>()
    private val sharedViewModel: NewDocumentViewModel by navGraphViewModels(navGraphId = R.id.nav_new_document) { viewModelFactory }
    private val viewModel: StatementPersonalDataViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = StatementPersonalDataBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@StatementPersonalDataFragment.viewModel
        }
        with(binding) {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            clickableBirthDate.setOnClickListener {
                openBirthDateSelection()
            }
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