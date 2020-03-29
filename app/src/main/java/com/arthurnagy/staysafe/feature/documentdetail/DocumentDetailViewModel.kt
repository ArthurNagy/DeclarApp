package com.arthurnagy.staysafe.feature.documentdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.arthurnagy.staysafe.core.db.CertificateDao
import com.arthurnagy.staysafe.core.db.StatementDao
import com.arthurnagy.staysafe.core.model.Document
import com.arthurnagy.staysafe.feature.DocumentIdentifier
import com.arthurnagy.staysafe.feature.DocumentType

class DocumentDetailViewModel(
    private val documentIdentifier: DocumentIdentifier,
    private val statementDao: StatementDao,
    private val certificateDao: CertificateDao
) : ViewModel() {
    val document: LiveData<Document> = liveData {
        emit(
            when (documentIdentifier.type) {
                DocumentType.STATEMENT -> statementDao.getById(documentIdentifier.id)
                DocumentType.CERTIFICATE -> certificateDao.getById(documentIdentifier.id)
            }
        )
    }
}