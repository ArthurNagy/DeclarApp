package com.arthurnagy.staysafe.feature.newdocument.statement.personaldata

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.StatementPersonalDataBinding
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.util.parentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class StatementPersonalDataFragment : Fragment(R.layout.fragment_statement_personal_data) {

    private val sharedViewModel by parentViewModel<NewDocumentViewModel>()
    private val viewModel: StatementPersonalDataViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = StatementPersonalDataBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@StatementPersonalDataFragment.viewModel
        }
    }
}