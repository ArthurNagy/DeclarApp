package com.arthurnagy.staysafe.feature.newdocument.statement.routedata

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Slide
import android.view.Gravity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.StatementRouteDataBinding
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.halcyonmobile.android.common.extensions.navigation.findSafeNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class StatementRouteDataFragment : Fragment(R.layout.fragment_statement_route_data) {

    private val sharedViewModel by navGraphViewModels<NewDocumentViewModel>(navGraphId = R.id.nav_new_document)
    private val viewModel: StatementRouteDataViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Slide(Gravity.START)
        enterTransition = Slide(Gravity.END)
        sharedElementEnterTransition = ChangeBounds()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = StatementRouteDataBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@StatementRouteDataFragment.viewModel
        }
        with(binding) {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            clickableMotive.setOnClickListener {
                showMotiveSelection()
            }
            clickableDate.setOnClickListener {
                openDateSelection()
            }
            next.setOnClickListener {
                findSafeNavController().navigate(
                    StatementRouteDataFragmentDirections.actionStatementRouteDataFragmentToSignatureFragment(),
                    FragmentNavigatorExtras(
                        binding.toolbar to getString(R.string.transition_toolbar),
                        binding.next to getString(R.string.transition_action)
                    )
                )
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

    private fun showMotiveSelection() {
        findSafeNavController().navigate(StatementRouteDataFragmentDirections.actionStatementRouteDataFragmentToMotivePickerBottomSheet())
    }
}