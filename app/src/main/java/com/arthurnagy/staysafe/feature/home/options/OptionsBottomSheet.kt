package com.arthurnagy.staysafe.feature.home.options

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.arthurnagy.staysafe.OptionsBinding
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.core.PreferenceManager
import com.arthurnagy.staysafe.feature.shared.IAP_TAG
import com.arthurnagy.staysafe.feature.shared.InAppPurchaseHelper
import com.arthurnagy.staysafe.feature.shared.OnDisconnected
import com.arthurnagy.staysafe.feature.shared.ThemeHelper
import com.arthurnagy.staysafe.feature.shared.openUrl
import com.arthurnagy.staysafe.feature.shared.showSnackbar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.chrisbanes.insetter.InsetterBindingAdapters
import org.koin.android.ext.android.inject

class OptionsBottomSheet : BottomSheetDialogFragment() {

    private val preferenceManager: PreferenceManager by inject()
    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(requireContext())
            .setListener(createPurchaseListener())
            .enablePendingPurchases()
            .build()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = super.onCreateDialog(savedInstanceState).apply {
        window?.decorView?.let { InsetterBindingAdapters.setEdgeToEdgeFlags(it, true) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        OptionsBinding.inflate(inflater, container, false).apply {
            donate.setOnClickListener {
                startPurchaseFlow {
                    Log.e(IAP_TAG, "startPurchaseFlow: onDisconnected")
                    root.showSnackbar(R.string.in_app_purchase_connection_failed)
                }
            }
            contactMe.setOnClickListener {
                openUrl(requireContext(), WEB_PAGE_URL)
            }
            sourceCode.setOnClickListener {
                openUrl(requireContext(), REPO_URL)
            }
            theme.setOnClickListener {
                showThemeDialog()
            }
            review.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(PLAY_STORE_URL) })
            }
            share.setOnClickListener {
                startActivity(Intent.createChooser(Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, PLAY_STORE_URL)
                    type = "text/plain"
                }, null))
            }
        }.root

    private fun startPurchaseFlow(onDisconnected: OnDisconnected) {
        InAppPurchaseHelper.startPurchaseFlow(
            billingClient = billingClient,
            onConnected = {
                Log.d(IAP_TAG, "startPurchaseFlow: onConnected")
                lifecycleScope.launchWhenResumed {
                    InAppPurchaseHelper.launchBillingFlow(billingClient, requireActivity())
                }
            },
            onDisconnected = onDisconnected
        )
    }

    private fun createPurchaseListener(): PurchasesUpdatedListener = object : InAppPurchaseHelper.SimplePurchaseListener() {
        override fun onPurchase(purchases: MutableList<Purchase>) {
            Log.e(IAP_TAG, "createPurchaseListener: onPurchase: $purchases")
            lifecycleScope.launchWhenResumed {
                purchases.forEach {
                    when (InAppPurchaseHelper.consumePurchase(billingClient, it)) {
                        InAppPurchaseHelper.PurchaseResult.Success -> view?.showSnackbar(R.string.in_app_purchase_success)
                        InAppPurchaseHelper.PurchaseResult.Pending -> view?.showSnackbar(R.string.in_app_purchase_pending)
                        InAppPurchaseHelper.PurchaseResult.Error -> view?.showSnackbar(R.string.in_app_purchase_error)
                    }
                }
            }
        }

        override fun onError() {
            Log.e(IAP_TAG, "createPurchaseListener: onError")
            view?.showSnackbar(R.string.in_app_purchase_error)
        }
    }

    private fun showThemeDialog() {
        val themeNames = requireContext().resources.getStringArray(R.array.theme_names)
        val themeEntries = requireContext().resources.getStringArray(R.array.theme_entries)
        val currentTheme = preferenceManager.theme
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.choose_theme)
            .setSingleChoiceItems(themeNames, themeEntries.indexOf(currentTheme)) { dialog, which ->
                themeEntries[which]?.let { selectedTheme ->
                    preferenceManager.theme = selectedTheme
                    ThemeHelper.applyTheme(selectedTheme)
                    dialog.dismiss()
                }
            }
            .show()
    }

    companion object {
        private const val REPO_URL = "https://github.com/ArthurNagy/DeclarApp"
        private const val WEB_PAGE_URL = "https://arthurnagy.com/"
        private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.arthurnagy.staysafe"
    }
}