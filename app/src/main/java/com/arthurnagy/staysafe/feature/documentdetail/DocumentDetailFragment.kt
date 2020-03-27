package com.arthurnagy.staysafe.feature.documentdetail

import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import com.arthurnagy.staysafe.DocumentDetailBinding
import com.arthurnagy.staysafe.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class DocumentDetailFragment : Fragment(R.layout.fragment_document_detail) {

    private val viewModel: DocumentDetailViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = DocumentDetailBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@DocumentDetailFragment.viewModel
        }
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(requireContext()))
            .build()

        with(binding) {
            webview.webViewClient = object : WebViewClientCompat() {
                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                    return assetLoader.shouldInterceptRequest(request.url)
                }
            }
            webview.settings.apply {
                builtInZoomControls = true
                displayZoomControls = false
                useWideViewPort = true
                loadWithOverviewMode = true
            }
            webview.loadUrl("https://appassets.androidplatform.net/assets/declaratie_proprie_raspundere.html")
        }
    }
}