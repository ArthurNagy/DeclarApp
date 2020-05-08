package com.arthurnagy.staysafe.feature.home

import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.arthurnagy.staysafe.HomeBinding
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.core.PreferenceManager
import com.arthurnagy.staysafe.feature.shared.InAppPurchaseHelper
import com.arthurnagy.staysafe.feature.shared.OnDisconnected
import com.arthurnagy.staysafe.feature.shared.autoCleared
import com.arthurnagy.staysafe.feature.shared.consume
import com.arthurnagy.staysafe.feature.shared.setupSwipeToDelete
import com.arthurnagy.staysafe.feature.shared.sharedGraphViewModel
import com.arthurnagy.staysafe.feature.shared.showSnackbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.halcyonmobile.android.common.extensions.navigation.findSafeNavController
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import timber.log.Timber

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val preferenceManager by inject<PreferenceManager>()
    private val viewModel by sharedGraphViewModel<HomeViewModel>(navGraphId = R.id.nav_main)
    private var binding by autoCleared<HomeBinding>()
    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(requireContext())
            .setListener(createPurchaseListener())
            .enablePendingPurchases()
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (preferenceManager.shouldShowOnboarding) {
            findSafeNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOnboardingFragment())
            preferenceManager.shouldShowOnboarding = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = HomeBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@HomeFragment.viewModel
        }
        val documentsAdapter = DocumentsAdapter(
            onStatementSelected = {
                findSafeNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToDocumentDetailFragment(it.id)
                )
            },
            onStatementDateUpdate = {
                viewModel.updateStatementDate(MaterialDatePicker.todayInUtcMilliseconds(), it)
            }
        )
        with(binding) {
            bar.setOnMenuItemClickListener {
                consume { findSafeNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOptionsBottomSheet()) }
            }
            add.setOnClickListener {
                findSafeNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewDocument())
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

            setupSwipeToDelete(recycler, documentsAdapter, onRemoveItem = {
                viewModel?.deleteStatement(it.statement)
            })
        }

        with(viewModel) {
            items.observe(viewLifecycleOwner, documentsAdapter::submitList)
            statementDeletedEvent.observe(viewLifecycleOwner) {
                val statement = it.consume()
                if (statement != null) {
                    showSnackbar(binding.coordinator, binding.bar, R.string.form_deleted_message, R.string.undo) {
                        viewModel.undoStatementDeletion(statement)
                    }
                }
            }
            statementDateUpdatedEvent.observe(viewLifecycleOwner) {
                showSnackbar(view = binding.coordinator, anchorView = binding.bar, message = R.string.statement_date_updated)
            }
            purchaseEvent.observe(viewLifecycleOwner) {
                if (it.consume() != null) {
                    startPurchaseFlow {
                        Timber.e("startPurchaseFlow: onDisconnected")
                        showSnackbar(view = binding.coordinator, anchorView = binding.bar, message = R.string.in_app_purchase_connection_failed)
                    }
                }
            }
        }
        InAppPurchaseHelper.checkPurchases(billingClient, onConnected = {
            Timber.d("checkPurchases: onConnected: $it")
            lifecycleScope.launchWhenResumed {
                InAppPurchaseHelper.consumeAlreadyPurchased(billingClient)
            }
        }, onDisconnected = {
            Timber.e("checkPurchases: onDisconnected")
            // TODO: check if we need to handle this ATM
        })
    }

    private fun startPurchaseFlow(onDisconnected: OnDisconnected) {
        InAppPurchaseHelper.startPurchaseFlow(
            billingClient = billingClient,
            onConnected = {
                Timber.d("startPurchaseFlow: onConnected: ${it.responseCode}")
                lifecycleScope.launchWhenResumed {
                    InAppPurchaseHelper.launchBillingFlow(billingClient, requireActivity())
                }
            },
            onDisconnected = onDisconnected
        )
    }

    private fun createPurchaseListener(): PurchasesUpdatedListener = object : InAppPurchaseHelper.SimplePurchaseListener() {

        override fun onPurchase(purchases: MutableList<Purchase>) {
            Timber.d("createPurchaseListener: onPurchase: $purchases")
            lifecycleScope.launchWhenResumed {
                purchases.forEach {
                    when (InAppPurchaseHelper.consumePurchase(billingClient, it)) {
                        InAppPurchaseHelper.PurchaseResult.Success -> showSnackbar(
                            view = binding.coordinator,
                            anchorView = binding.bar,
                            message = R.string.in_app_purchase_success
                        )
                        InAppPurchaseHelper.PurchaseResult.Pending -> showSnackbar(
                            view = binding.coordinator,
                            anchorView = binding.bar,
                            message = R.string.in_app_purchase_pending
                        )
                        InAppPurchaseHelper.PurchaseResult.Error -> showSnackbar(
                            view = binding.coordinator,
                            anchorView = binding.bar,
                            message = R.string.in_app_purchase_error
                        )
                    }
                }
            }
        }

        override fun onError(purchaseResult: BillingResult) {
            Timber.e("createPurchaseListener: onError: $purchaseResult")
            showSnackbar(view = binding.coordinator, anchorView = binding.bar, message = R.string.in_app_purchase_error)
        }
    }
}