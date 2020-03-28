package com.arthurnagy.staysafe.feature.newdocument.signature

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.SignatureBinding
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.util.parentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SignatureFragment : Fragment(R.layout.fragment_signature) {

    private val sharedViewModel by parentViewModel<NewDocumentViewModel>()
    private val viewModel: SignatureViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = SignatureBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@SignatureFragment.viewModel
        }
    }
}