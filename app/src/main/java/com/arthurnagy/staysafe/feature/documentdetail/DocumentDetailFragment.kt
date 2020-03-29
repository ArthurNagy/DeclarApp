package com.arthurnagy.staysafe.feature.documentdetail

import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import com.arthurnagy.staysafe.DocumentDetailBinding
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.feature.DocumentType
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DocumentDetailFragment : Fragment(R.layout.fragment_document_detail) {

    private val args by navArgs<DocumentDetailFragmentArgs>()
    private val viewModel: DocumentDetailViewModel by viewModel { parametersOf(args.documentIdentifier) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = DocumentDetailBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@DocumentDetailFragment.viewModel
        }
        val documentIdentifier = args.documentIdentifier
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
            webview.loadUrl(
                when (documentIdentifier.type) {
                    DocumentType.STATEMENT -> HTML_STATEMENT
                    DocumentType.CERTIFICATE -> HTML_CERTIFICATE
                }
            )
        }
    }

    companion object {
        private const val HTML_STATEMENT = "https://appassets.androidplatform.net/assets/declaratie_proprie_raspundere.html"
        private const val HTML_CERTIFICATE = "https://appassets.androidplatform.net/assets/adeverinta_angajator.html"
    }
}