package com.arthurnagy.staysafe.feature.shared

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.querySkuDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

typealias OnConnected = (billingResult: BillingResult) -> Unit
typealias OnDisconnected = () -> Unit

const val IAP_TAG = "InAppPurchase"

object InAppPurchaseHelper {
    private var purchaseChecked: Boolean = false

    fun startPurchaseFlow(billingClient: BillingClient, onConnected: OnConnected? = null, onDisconnected: OnDisconnected? = null) {
        billingClient.startConnection(SimpleBillingClientListener(onConnected, onDisconnected))
    }

    fun checkPurchases(billingClient: BillingClient, onConnected: OnConnected? = null, onDisconnected: OnDisconnected? = null) {
        if (!purchaseChecked) {
            billingClient.startConnection(
                SimpleBillingClientListener(
                    onConnected = { billingResult ->
                        purchaseChecked = true
                        onConnected?.invoke(billingResult)
                    },
                    onDisconnected = onDisconnected
                )
            )
        }
    }

    suspend fun consumeAlreadyPurchased(billingClient: BillingClient) {
        val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        if (purchasesResult.billingResult.isOk) {
            val pendingPurchases = purchasesResult.purchasesList.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
            pendingPurchases.forEach { purchase ->
                consumePurchase(billingClient, purchase)
            }
        }
    }

    suspend fun launchBillingFlow(billingClient: BillingClient, activity: Activity) {
        querySkuDetails(billingClient)?.let { skuDetails ->
            billingClient.launchBillingFlow(
                activity, BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build()
            )
        }
    }

    suspend fun consumePurchase(billingClient: BillingClient, purchase: Purchase): PurchaseResult {
        return if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val consumeResult = withContext(Dispatchers.IO) {
                    billingClient.consume(
                        ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .setDeveloperPayload(purchase.developerPayload)
                            .build()
                    )
                }
                if (consumeResult.billingResult.isOk) {
                    PurchaseResult.Success
                } else {
                    PurchaseResult.Error
                }
            } else PurchaseResult.Ignored
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            return PurchaseResult.Pending
        } else PurchaseResult.Ignored
    }

    sealed class PurchaseResult {
        object Success : PurchaseResult()
        object Ignored : PurchaseResult()
        object Pending : PurchaseResult()
        object Error : PurchaseResult()
    }

    private suspend fun querySkuDetails(billingClient: BillingClient): SkuDetails? {
        val skuList = ArrayList<String>()
        skuList.add("staysafe.buy.me.a.coffee")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        val skuDetailsResult = withContext(Dispatchers.IO) {
            billingClient.querySkuDetails(params.build())
        }
        return if (skuDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            skuDetailsResult.skuDetailsList?.firstOrNull()
        } else null
    }

    private suspend fun BillingClient.consume(consumeParams: ConsumeParams) = suspendCancellableCoroutine<ConsumeResult> {
        consumeAsync(consumeParams) { billingResult: BillingResult, s: String ->
            it.resume(ConsumeResult(billingResult, s))
        }
    }

    val BillingResult.isOk: Boolean get() = responseCode == BillingClient.BillingResponseCode.OK

    private class SimpleBillingClientListener(
        private val onConnected: OnConnected? = null,
        private val onDisconnected: OnDisconnected? = null
    ) : BillingClientStateListener {
        override fun onBillingServiceDisconnected() {
            onDisconnected?.invoke()
        }

        override fun onBillingSetupFinished(p0: BillingResult?) {
            p0?.let { billingResult ->
                if (billingResult.isOk) {
                    onConnected?.invoke(billingResult)
                } else {
                    onDisconnected?.invoke()
                }
            }
        }
    }

    abstract class SimplePurchaseListener : PurchasesUpdatedListener {

        open fun onPurchase(purchases: MutableList<Purchase>) = Unit
        open fun onUserCanceled() = Unit
        open fun onError() = Unit

        override fun onPurchasesUpdated(purchaseResult: BillingResult, purchases: MutableList<Purchase>?) {
            if (purchaseResult.isOk && !purchases.isNullOrEmpty()) {
                onPurchase(purchases)
            } else if (purchaseResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                onUserCanceled()
            } else {
                onError()
            }
        }
    }
}