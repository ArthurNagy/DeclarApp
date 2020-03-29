package com.arthurnagy.staysafe.feature.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthurnagy.staysafe.HomeBinding
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.feature.DocumentType
import com.arthurnagy.staysafe.feature.util.consume
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = HomeBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@HomeFragment.viewModel
        }
        with(binding) {
            bar.setOnMenuItemClickListener {
                consume { findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOptionsBottomSheet()) }
            }
            add.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewDocumentFragment(DocumentType.STATEMENT))
            }
        }
    }
}