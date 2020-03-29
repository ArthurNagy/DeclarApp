package com.arthurnagy.staysafe.feature.documentdetail

import android.content.Context
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import com.arthurnagy.staysafe.DocumentDetailBinding
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.core.model.Certificate
import com.arthurnagy.staysafe.core.model.Motive
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.DocumentType
import com.arthurnagy.staysafe.feature.shared.consume
import com.arthurnagy.staysafe.feature.shared.formatToFormalDate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset

class DocumentDetailFragment : Fragment(R.layout.fragment_document_detail) {

    private val args by navArgs<DocumentDetailFragmentArgs>()
    private val viewModel: DocumentDetailViewModel by viewModel { parametersOf(args.documentIdentifier) }

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
                    view?.let {
                        when (val document = viewModel?.document?.value) {
                            is Statement -> addStatementData(it, document)
                            is Certificate -> addCertificateData(it, document)
                            else -> throw IllegalStateException("Document not read or known")
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
        }
        with(viewModel) {
            document.observe(viewLifecycleOwner) { document ->
                val baseUrl = when (document) {
                    is Statement -> HTML_STATEMENT
                    is Certificate -> HTML_CERTIFICATE
                    else -> throw IllegalStateException("Can't load any other document of type: $document")
                }
                binding.webview.loadUrl(baseUrl)
            }
            navigateBackEvent.observe(viewLifecycleOwner) {
                if (it.consume() != null) findNavController().navigateUp()
            }
        }
    }

    private fun createWebPrintJob(webView: WebView) {
        (activity?.getSystemService(Context.PRINT_SERVICE) as? PrintManager)?.let { printManager ->
            val jobName = "${getString(R.string.app_name)} ${getString(
                when (args.documentIdentifier.type) {
                    DocumentType.STATEMENT -> R.string.statement
                    DocumentType.CERTIFICATE -> R.string.certificate
                }
            )}"
            val printAdapter = webView.createPrintDocumentAdapter(jobName)
            printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
        }
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(context)
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
            addContent("lastName", statement.lastName)
            addContent("firstName", statement.firstName)
            val localDate = Instant.ofEpochMilli(statement.birthDate).atOffset(ZoneOffset.UTC).toLocalDate()
            addContent("birthDateDay", "${localDate.dayOfMonth}.")
            addContent("birthDateMonth", "${localDate.month.value}.")
            addContent("birthDateYear", "${localDate.year}")
            if (statement.address.length > SINGLE_LINE_ADDRESS_LIMIT) {
                addContent("locationAddressOne", statement.address.substring(0, SINGLE_LINE_ADDRESS_LIMIT))
                addContent("locationAddressTwo", statement.address.substring(SINGLE_LINE_ADDRESS_LIMIT))
            } else {
                addContent("locationAddressOne", statement.address)
            }
            when (statement.motive) {
                Motive.PROFESSIONAL_INTERESTS -> show("optionOne")
                Motive.NECESSITY_PROVISIONING -> show("optionTwo")
                Motive.MEDICAL_ASSISTANCE -> show("optionThree")
                Motive.JUSTIFIED_HELP -> show("optionFour")
                Motive.PHYSICAL_ACTIVITY -> show("optionFive")
                Motive.AGRICULTURAL_ACTIVITIES -> show("optionSix")
                Motive.BLOOD_DONATION -> show("optionSeven")
                Motive.VOLUNTEERING -> show("optionEight")
                Motive.COMMERCIALIZE_AGRICULTURAL_PRODUCES -> show("optionNine")
                Motive.PROFESSIONAL_ACTIVITY_NECESSITIES -> show("optionTen")
            }
            addContent("routeAddress", statement.route)
            addContent("date", formatToFormalDate(statement.date))
            signature("$HTML_FILES${statement.signaturePath.substringAfterLast("/")}")
        }
    }

    private fun addCertificateData(webView: WebView, certificate: Certificate) {
        TODO()
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

    companion object {
        private const val SINGLE_LINE_ADDRESS_LIMIT = 50
        private const val HTML_STATEMENT = "https://appassets.androidplatform.net/assets/declaratie_proprie_raspundere.html"
        private const val HTML_CERTIFICATE = "https://appassets.androidplatform.net/assets/adeverinta_angajator.html"
        private const val HTML_FILES = "https://appassets.androidplatform.net/files/"
    }
}