package com.arthurnagy.staysafe.feature.newdocument.statement.routedata

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.StatementRouteDataBinding
import com.arthurnagy.staysafe.core.model.Motive
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.labelRes
import com.arthurnagy.staysafe.feature.shared.parentViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class StatementRouteDataFragment : Fragment(R.layout.fragment_statement_route_data) {

    private val sharedViewModel by parentViewModel<NewDocumentViewModel>()
    private val viewModel: StatementRouteDataViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = StatementRouteDataBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@StatementRouteDataFragment.viewModel
        }

        val motives = Motive.values()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_motive, motives.mapIndexed { index, motive -> "${index + 1}.${getString(motive.labelRes)}" })
        with(binding) {
            motiveAutoComplete.setAdapter(adapter)
            motiveAutoComplete.setOnItemClickListener { _, _, position, _ ->
                viewModel?.onMotiveSelected(motives[position])
            }
            clickableDate.setOnClickListener {
                openDateSelection()
            }
        }
    }

    private fun openDateSelection() {
        val todayTimestamp = MaterialDatePicker.todayInUtcMilliseconds()
        val currentDate = viewModel.date.value ?: todayTimestamp
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .setOpenAt(currentDate)
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(currentDate)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.addOnPositiveButtonClickListener(viewModel::onDateSelected)
        datePicker.show(childFragmentManager, datePicker.toString())
    }
}