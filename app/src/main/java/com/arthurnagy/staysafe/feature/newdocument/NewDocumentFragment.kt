package com.arthurnagy.staysafe.feature.newdocument

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.arthurnagy.staysafe.NewDocumentBinding
import com.arthurnagy.staysafe.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NewDocumentFragment : Fragment(R.layout.fragment_new_document) {

    private val args by navArgs<NewDocumentFragmentArgs>()
    private val viewModel: NewDocumentViewModel by viewModel { parametersOf(args.documentType) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = NewDocumentBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@NewDocumentFragment.viewModel
        }
        val newDocumentPagerAdapter = NewDocumentPagerAdapter(childFragmentManager, args.documentType)
        with(binding) {
            pager.adapter = newDocumentPagerAdapter
        }
    }
}