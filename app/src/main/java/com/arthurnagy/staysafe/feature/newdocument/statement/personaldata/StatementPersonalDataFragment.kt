package com.arthurnagy.staysafe.feature.newdocument.statement.personaldata

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.StatementPersonalDataBinding
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.parentViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

class StatementPersonalDataFragment : Fragment(R.layout.fragment_statement_personal_data) {

    private val sharedViewModel by parentViewModel<NewDocumentViewModel>()
    private val viewModel: StatementPersonalDataViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = StatementPersonalDataBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@StatementPersonalDataFragment.viewModel
        }
        with(binding) {
            clickableBirthDate.setOnClickListener {
                openBirthDateSelection()
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