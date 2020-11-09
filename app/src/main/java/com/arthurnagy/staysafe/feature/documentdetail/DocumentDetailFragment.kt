package com.arthurnagy.staysafe.feature.documentdetail

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import com.arthurnagy.staysafe.BuildConfig
import com.arthurnagy.staysafe.DocumentDetailBinding
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.core.model.Motive
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.shared.consume
import com.arthurnagy.staysafe.feature.shared.formatToFormalDate
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset

class DocumentDetailFragment : Fragment(R.layout.fragment_document_detail) {

    private val args by navArgs<DocumentDetailFragmentArgs>()
    private val viewModel: DocumentDetailViewModel by viewModel { parametersOf(args.documentId) }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = DocumentDetailBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@DocumentDetailFragment.viewModel
        }

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(requireContext()))
            .addPathHandler("/files/", WebViewAssetLoader.InternalStoragePathHandler(requireContext(), requireContext().filesDir))
            .build()

        with(binding) {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            toolbar.setOnMenuItemClickListener {
                consume {
                    when (it.itemId) {
                        R.id.print -> createWebPrintJob(webview)
                        R.id.delete -> showDeleteDialog()
                    }
                }
            }
            webview.webViewClient = object : WebViewClientCompat() {
                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                    return assetLoader.shouldInterceptRequest(request.url)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    view?.let { webView ->
                        viewModel?.statement?.value?.let { statement ->
                            addStatementData(webView, statement)
                        }
                    }
                }
            }
            webview.settings.apply {
                builtInZoomControls = true
                displayZoomControls = false
                useWideViewPort = true
                loadWithOverviewMode = true
                allowFileAccess = true
                javaScriptEnabled = true
            }
            initializeAd(adView)
        }
        with(viewModel) {
            statement.observe(viewLifecycleOwner) {
                binding.webview.loadUrl(HTML_STATEMENT)
            }
            navigateBackEvent.observe(viewLifecycleOwner) {
                if (it.consume() != null) findNavController().navigateUp()
            }
        }
    }

    private fun initializeAd(adView: AdView) {
        adView.apply {
            adSize = AdSize.BANNER
            adUnitId = BuildConfig.AD_MOB_BANNER_UNIT_ID
            loadAd(AdRequest.Builder().build())
        }
    }

    private fun createWebPrintJob(webView: WebView) {
        (activity?.getSystemService(Context.PRINT_SERVICE) as? PrintManager)?.let { printManager ->
            val jobName = "${getString(R.string.app_name)} ${getString(R.string.statement)}"
            val printAdapter = webView.createPrintDocumentAdapter(jobName)
            printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
        }
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_title)
            .setMessage(R.string.delete_desc)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                viewModel.deleteDocument()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addStatementData(webView: WebView, statement: Statement) {
        with(webView) {
            addContent("fullName", "${statement.lastName} ${statement.firstName}")

            val localDate = Instant.ofEpochMilli(statement.birthDate).atOffset(ZoneOffset.UTC).toLocalDate()
            addContent("birthDate", "${localDate.dayOfMonth}.${localDate.month.value}.${localDate.year}")
            addContent("birthdayLocation", statement.birthdayLocation)

            addContent("officialLocation", statement.location)
            addContent("currentLocation", statement.currentLocation)

            statement.motives.forEach { motive ->
                when (motive) {
                    Motive.PROFESSIONAL_INTERESTS -> {
                        show("optionOne")
                        statement.workLocation?.let { addContent("workLocation", it) }
                        statement.workAddresses?.let { addContent("workAddress", it) }
                    }
                    Motive.MEDICAL_ASSISTANCE -> show("optionTwo")
                    Motive.PURCHASE_OF_MEDICATION -> show("optionThree")
                    Motive.MOTIVE_HELP -> show("optionFour")
                    Motive.MOTIVE_FAMILY_DECEASE -> show("optionFive")
                }
            }

            addContent("date", formatToFormalDate(statement.date))
            signature("$HTML_FILES${statement.signaturePath.substringAfterLast("/")}")
        }
    }

    private fun WebView.addContent(tagId: String, content: String) {
        loadUrl("javascript:addContent('$tagId', '$content')")
    }

    private fun WebView.show(tagId: String) {
        loadUrl("javascript:show('$tagId')")
    }

    private fun WebView.signature(signaturePath: String) {
        loadUrl("javascript:signature('$signaturePath')")
    }

    private fun WebView.fontSize(tagId: String, sizeInPixel: Int) {
        loadUrl("javascript:fontSize('$tagId', '${sizeInPixel}px')")
    }

    companion object {
        private const val SINGLE_LINE_ADDRESS_LIMIT = 50
        private const val HTML_STATEMENT = "https://appassets.androidplatform.net/assets/declaratie_proprie_raspundere.html"
        private const val HTML_FILES = "https://appassets.androidplatform.net/files/"
    }
}