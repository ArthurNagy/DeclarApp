package com.arthurnagy.staysafe.feature.documentdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.arthurnagy.staysafe.core.db.CertificateDao
import com.arthurnagy.staysafe.core.db.StatementDao
import com.arthurnagy.staysafe.core.model.Certificate
import com.arthurnagy.staysafe.core.model.Document
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.DocumentIdentifier
import com.arthurnagy.staysafe.feature.DocumentType
import com.arthurnagy.staysafe.feature.shared.Event
import kotlinx.coroutines.launch

class DocumentDetailViewModel(
    private val documentIdentifier: DocumentIdentifier,
    private val statementDao: StatementDao,
    private val certificateDao: CertificateDao
) : ViewModel() {
    private val _navigateBackEvent = MutableLiveData<Event<Unit>>()
    val navigateBackEvent: LiveData<Event<Unit>> get() = _navigateBackEvent
    val document: LiveData<Document> = liveData {
        emit(
            when (documentIdentifier.type) {
                DocumentType.STATEMENT -> statementDao.getById(documentIdentifier.id)
                DocumentType.CERTIFICATE -> certificateDao.getById(documentIdentifier.id)
            }
        )
    }

    fun deleteDocument() {
        viewModelScope.launch {
            when (documentIdentifier.type) {
                DocumentType.STATEMENT -> (document.value as? Statement)?.let { statementDao.delete(it) }
                DocumentType.CERTIFICATE -> (document.value as? Certificate)?.let { certificateDao.delete(it) }
            }
            _navigateBackEvent.value = Event(Unit)
        }
    }
}