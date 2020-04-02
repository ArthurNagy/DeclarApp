package com.arthurnagy.staysafe.feature.newdocument

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.viewpager.widget.ViewPager
import com.arthurnagy.staysafe.NewDocumentBinding
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.feature.shared.addPageChangeListenerTo
import com.halcyonmobile.android.common.extensions.navigation.findSafeNavController
import org.koin.android.ext.android.inject

class NewDocumentFragment : Fragment(R.layout.fragment_new_document) {

    private val viewModelFactory by inject<NewDocumentViewModel.Factory>()
    private val viewModel: NewDocumentViewModel by navGraphViewModels(navGraphId = R.id.nav_new_document) { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = NewDocumentBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@NewDocumentFragment.viewModel
        }
        val newDocumentPagerAdapter = NewDocumentPagerAdapter(childFragmentManager)
        with(binding) {
            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }

            pager.adapter = newDocumentPagerAdapter

            addPageChangeListenerTo(pager, onPageSelected = { pageIndex ->
                this@NewDocumentFragment.viewModel.updateCurrentPageIndex(pageIndex)
            })

            setupBackNavigation(pager)
        }
        with(viewModel) {
            currentPageIndex.observe(viewLifecycleOwner) {
                if (binding.pager.currentItem != it) {
                    binding.pager.setCurrentItem(it, true)
                }
            }
            events.observe(viewLifecycleOwner) {
                when (val action = it.consume()) {
                    is NewDocumentViewModel.Action.OpenDocument -> findSafeNavController().navigate(
                        NewDocumentFragmentDirections.actionNewDocumentFragmentToDocumentDetailFragment(action.documentId)
                    )
                }
            }
        }
    }

    private fun setupBackNavigation(pager: ViewPager) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (pager.currentItem == NewDocumentPagerAdapter.STATEMENT_PERSONAL_DATA_INDEX) {
                    findNavController().navigateUp()
                } else {
                    pager.setCurrentItem(pager.currentItem - 1, true)
                }
            }
        })
    }
}