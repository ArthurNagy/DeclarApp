package com.arthurnagy.staysafe.feature.newdocument.statement.routedata

import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.transition.ChangeBounds
import androidx.transition.Slide
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.StatementRouteDataBinding
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.doIfAboveVersion
import com.arthurnagy.staysafe.feature.shared.sharedGraphViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.halcyonmobile.android.common.extensions.navigation.findSafeNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class StatementRouteDataFragment : Fragment(R.layout.fragment_statement_route_data) {

    private val sharedViewModel by sharedGraphViewModel<NewDocumentViewModel>(navGraphId = R.id.nav_new_document)
    private val viewModel: StatementRouteDataViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doIfAboveVersion(Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O && Build.VERSION.SDK_INT != Build.VERSION_CODES.O_MR1) {
                exitTransition = Slide(Gravity.START)
                enterTransition = Slide(Gravity.END)
                sharedElementEnterTransition = ChangeBounds()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = StatementRouteDataBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@StatementRouteDataFragment.viewModel
        }
        val motiveAdapter = MotiveAdapter {
            viewModel.onMotiveSelected(it)
        }
        with(binding) {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            recycler.apply {
                adapter = motiveAdapter
                val itemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                val firstKeyline = resources.getDimensionPixelSize(R.dimen.first_keyline)
                itemDecoration.setDrawable(InsetDrawable(itemDecoration.drawable?.mutate(), firstKeyline, 0, firstKeyline, 0))
                addItemDecoration(itemDecoration)
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
        viewModel.motiveItems.observe(viewLifecycleOwner, motiveAdapter::submitList)
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