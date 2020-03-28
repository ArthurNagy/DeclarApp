package com.arthurnagy.staysafe.feature.newdocument.statement.routedata

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.StatementRouteDataBinding
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.util.parentViewModel
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
    }
}