package com.arthurnagy.staysafe.feature.newdocument.certificate.employeedata

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arthurnagy.staysafe.CertificateEmployeeDataBinding
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.parentGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CertificateEmployeeDataFragment : Fragment(R.layout.fragment_certificate_employee_data) {

    private val sharedViewModel by parentGraphViewModel<NewDocumentViewModel>(navGraphId = R.id.nav_new_document)
    private val viewModel: CertificateEmployeeDataViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = CertificateEmployeeDataBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CertificateEmployeeDataFragment.viewModel
        }
    }
}