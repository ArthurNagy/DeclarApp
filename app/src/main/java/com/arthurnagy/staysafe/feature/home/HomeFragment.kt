package com.arthurnagy.staysafe.feature.home

import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arthurnagy.staysafe.HomeBinding
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.core.PreferenceManager
import com.arthurnagy.staysafe.feature.DocumentIdentifier
import com.arthurnagy.staysafe.feature.DocumentType
import com.arthurnagy.staysafe.feature.shared.consume
import com.halcyonmobile.android.common.extensions.navigation.findSafeNavController
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val preferenceManager by inject<PreferenceManager>()
    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (preferenceManager.shouldShowOnboarding) {
            findSafeNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOnboardingFragment())
            preferenceManager.shouldShowOnboarding = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = HomeBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@HomeFragment.viewModel
        }
        val documentsAdapter = DocumentsAdapter {
            findSafeNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDocumentDetailFragment(
                    DocumentIdentifier(
                        it.id,
                        DocumentType.STATEMENT
                    )
                )
            )
        }
        with(binding) {
            bar.setOnMenuItemClickListener {
                consume { findSafeNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOptionsBottomSheet()) }
            }
            add.setOnClickListener {
                findSafeNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewDocumentFragment(DocumentType.STATEMENT))
            }

            with(recycler) {
                layoutManager = LinearLayoutManager(requireContext())
                if (itemDecorationCount == 0) {
                    val itemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                    val firstKeyline = resources.getDimensionPixelSize(R.dimen.first_keyline)
                    itemDecoration.setDrawable(InsetDrawable(itemDecoration.drawable?.mutate(), firstKeyline, 0, firstKeyline, 0))
                    addItemDecoration(itemDecoration)
                }
                adapter = documentsAdapter
            }
        }
        viewModel.items.observe(viewLifecycleOwner, documentsAdapter::submitList)
    }
}