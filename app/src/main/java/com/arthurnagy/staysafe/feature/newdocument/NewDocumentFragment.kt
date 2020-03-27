package com.arthurnagy.staysafe.feature.newdocument

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arthurnagy.staysafe.NewDocumentBinding
import com.arthurnagy.staysafe.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewDocumentFragment : Fragment(R.layout.fragment_new_document) {

    private val viewModel: NewDocumentViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = NewDocumentBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@NewDocumentFragment.viewModel
        }
    }
}