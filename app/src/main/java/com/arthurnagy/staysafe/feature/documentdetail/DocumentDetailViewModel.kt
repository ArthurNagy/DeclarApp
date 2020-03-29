package com.arthurnagy.staysafe.feature.documentdetail

import androidx.lifecycle.ViewModel
import com.arthurnagy.staysafe.core.db.CertificateDao
import com.arthurnagy.staysafe.core.db.StatementDao
import com.arthurnagy.staysafe.feature.DocumentIdentifier

class DocumentDetailViewModel(
    private val documentIdentifier: DocumentIdentifier,
    private val statementDao: StatementDao,
    private val certificateDao: CertificateDao
) : ViewModel() {
    // TODO: Implement the ViewModel
}