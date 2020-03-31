package com.arthurnagy.staysafe.feature.newdocument.certificate.employerdata

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arthurnagy.staysafe.CertificateEmployerDataBinding
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.parentGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CertificateEmployerDataFragment : Fragment(R.layout.fragment_certificate_employer_data) {

    private val sharedViewModel by parentGraphViewModel<NewDocumentViewModel>(navGraphId = R.id.newDocument)
    private val viewModel: CertificateEmployerDataViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = CertificateEmployerDataBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CertificateEmployerDataFragment.viewModel
        }
    }
}