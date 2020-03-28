package com.arthurnagy.staysafe.feature.newdocument.certificate.routedata

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arthurnagy.staysafe.CertificateRouteDataBinding
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.util.parentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CertificateRouteDataFragment : Fragment(R.layout.fragment_certificate_route_data) {

    private val sharedViewModel by parentViewModel<NewDocumentViewModel>()
    private val viewModel: CertificateRouteDataViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = CertificateRouteDataBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CertificateRouteDataFragment.viewModel
        }
    }
}