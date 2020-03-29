package com.arthurnagy.staysafe.feature.newdocument.signature

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.util.StringProvider

class SignatureViewModel(private val newDocumentViewModel: NewDocumentViewModel, stringProvider: StringProvider) : ViewModel() {
    val label: LiveData<String> = newDocumentViewModel.currentPageIndex.map { stringProvider.getString(R.string.signature_label, it + 1) }
    val fileName = SIGNATURE_FILE_NAME_SUFFIX.format(System.currentTimeMillis())

    fun onClearSignature() {
        newDocumentViewModel.clearSignature()
    }

    fun onSignatureCreated(signatureUri: Uri) {
        newDocumentViewModel.updateSignature(signatureUri.toString())
    }

    companion object {
        private const val SIGNATURE_FILE_NAME_SUFFIX = "%d_signature.png"
    }
}