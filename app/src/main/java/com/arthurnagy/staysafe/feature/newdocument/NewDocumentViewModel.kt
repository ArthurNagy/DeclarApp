package com.arthurnagy.staysafe.feature.newdocument

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.core.db.CertificateDao
import com.arthurnagy.staysafe.core.db.StatementDao
import com.arthurnagy.staysafe.core.model.Certificate
import com.arthurnagy.staysafe.core.model.Document
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.DocumentIdentifier
import com.arthurnagy.staysafe.feature.DocumentType
import com.arthurnagy.staysafe.feature.util.Event
import com.arthurnagy.staysafe.feature.util.mediatorLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NewDocumentViewModel(
    val type: DocumentType,
    private val statementDao: StatementDao,
    private val certificateDao: CertificateDao
) : ViewModel() {

    private val lastSavedDocument: LiveData<Document?> = liveData {
        emit(
            try {
                when (type) {
                    DocumentType.STATEMENT -> statementDao.getLast()
                    DocumentType.CERTIFICATE -> certificateDao.getLast()
                }
            } catch (exception: NoSuchElementException) {
                null
            }
        )
    }

    private val _pendingDocument: MutableLiveData<PendingDocument> = mediatorLiveData(
        defaultValue = when (type) {
            DocumentType.STATEMENT -> PendingStatement()
            DocumentType.CERTIFICATE -> PendingCertificate()
        },
        dependency = lastSavedDocument,
        onChanged = { pendingDocument: PendingDocument?, lastSavedDocument: Document? ->
            when (type) {
                DocumentType.STATEMENT -> transformLastSavedStatementToPendingStatement(pendingDocument as? PendingStatement, lastSavedDocument as? Statement)
                DocumentType.CERTIFICATE -> transformLastSavedCertificateToPendingCertificate(
                    pendingDocument as? PendingCertificate,
                    lastSavedDocument as? Certificate
                )
            }
        })
    val pendingDocument: LiveData<PendingDocument> get() = _pendingDocument

    private val _currentPageIndex = MutableLiveData<Int>(
        when (type) {
            DocumentType.STATEMENT -> NewDocumentPagerAdapter.STATEMENT_PERSONAL_DATA_INDEX
            DocumentType.CERTIFICATE -> NewDocumentPagerAdapter.CERTIFICATE_EMPLOYER_DATA_INDEX
        }
    )
    val currentPageIndex: LiveData<Int> get() = _currentPageIndex

    val isActionEnabled: LiveData<Boolean> =
        currentPageIndex.asFlow().combine(pendingDocument.asFlow()) { currentPageIndex: Int, pendingDocument: PendingDocument ->
            true
        }.asLiveData()
    val actionText: LiveData<Int> = currentPageIndex.map {
        val targetEndIndex = when (type) {
            DocumentType.STATEMENT -> NewDocumentPagerAdapter.STATEMENT_SIGNATURE_INDEX
            DocumentType.CERTIFICATE -> NewDocumentPagerAdapter.CERTIFICATE_SIGNATURE_INDEX
        }
        if (currentPageIndex.value == targetEndIndex) {
            R.string.generate
        } else {
            R.string.next
        }
    }

    private val _events = MutableLiveData<Event<Action>>()
    val events: LiveData<Event<Action>> get() = _events

    fun onActionSelected() {
        val targetEndIndex = when (type) {
            DocumentType.STATEMENT -> NewDocumentPagerAdapter.STATEMENT_SIGNATURE_INDEX
            DocumentType.CERTIFICATE -> NewDocumentPagerAdapter.CERTIFICATE_SIGNATURE_INDEX
        }
        if (currentPageIndex.value == targetEndIndex) {
            generateDocument()
        } else {
            _currentPageIndex.value = _currentPageIndex.value?.inc()
        }
    }

    private fun generateDocument() {
        viewModelScope.launch {
            when (type) {
                DocumentType.STATEMENT -> (pendingDocument.value as? PendingStatement)?.let { pendingStatement ->
                    val statementId = createNewStatement(pendingStatement)
                    _events.value = Event(Action.OpenDocument(DocumentIdentifier(id = statementId, type = DocumentType.STATEMENT)))
                }
                DocumentType.CERTIFICATE -> (pendingDocument.value as? PendingCertificate)?.let { pendingCertificate ->
                    val certificateId = createNewCertificate(pendingCertificate)
                    _events.value = Event(Action.OpenDocument(DocumentIdentifier(id = certificateId, type = DocumentType.CERTIFICATE)))
                }
            }
        }
    }

    fun updateCurrentPageIndex(index: Int) {
        if (index != _currentPageIndex.value) {
            _currentPageIndex.value = index
        }
    }

    private suspend fun createNewCertificate(pendingCertificate: PendingCertificate): Long {
        val newCertificate = Certificate(
            employerFirstName = pendingCertificate.employerFirstName.orIllegalState(),
            employerLastName = pendingCertificate.employerLastName.orIllegalState(),
            employerJobTitle = pendingCertificate.employerJobTitle.orIllegalState(),
            companyName = pendingCertificate.companyName.orIllegalState(),
            companyAddress = pendingCertificate.companyAddress.orIllegalState(),
            employeeFirstName = pendingCertificate.employeeFirstName.orIllegalState(),
            employeeLastName = pendingCertificate.employeeLastName.orIllegalState(),
            employeeJobTitle = pendingCertificate.employeeJobTitle.orIllegalState(),
            employeeAddress = pendingCertificate.employeeAddress.orIllegalState(),
            birthDate = pendingCertificate.birthDate.orIllegalState(),
            route = pendingCertificate.route.orIllegalState(),
            transportationMethod = pendingCertificate.transportationMethod.orIllegalState(),
            fromDate = pendingCertificate.fromDate.orIllegalState(),
            toDate = pendingCertificate.toDate.orIllegalState(),
            signatureUri = pendingCertificate.signatureUri.orIllegalState()
        )
        return certificateDao.insert(newCertificate)
    }

    private suspend fun createNewStatement(pendingStatement: PendingStatement): Long {
        val newStatement = Statement(
            firstName = pendingStatement.firstName.orIllegalState(),
            lastName = pendingStatement.lastName.orIllegalState(),
            birthDate = pendingStatement.birthDate.orIllegalState(),
            address = pendingStatement.address.orIllegalState(),
            route = pendingStatement.route.orIllegalState(),
            date = pendingStatement.date.orIllegalState(),
            signatureUri = pendingStatement.signatureUri.orIllegalState()
        )
        return statementDao.insert(newStatement)
    }

    private fun transformLastSavedStatementToPendingStatement(pendingStatement: PendingStatement?, statement: Statement?): PendingStatement? =
        pendingStatement?.copy(
            firstName = statement?.firstName ?: pendingStatement.firstName,
            lastName = statement?.lastName ?: pendingStatement.lastName,
            birthDate = statement?.birthDate ?: pendingStatement.birthDate,
            address = statement?.address ?: pendingStatement.address,
            signatureUri = statement?.signatureUri ?: pendingStatement.signatureUri
        )

    private fun transformLastSavedCertificateToPendingCertificate(pendingCertificate: PendingCertificate?, certificate: Certificate?): PendingCertificate? =
        pendingCertificate?.copy(
            employerFirstName = certificate?.employerFirstName ?: pendingCertificate.employerFirstName,
            employerLastName = certificate?.employerLastName ?: pendingCertificate.employerLastName,
            employerJobTitle = certificate?.employerJobTitle ?: pendingCertificate.employerJobTitle,
            companyName = certificate?.companyName ?: pendingCertificate.companyName,
            companyAddress = certificate?.companyAddress ?: pendingCertificate.companyAddress,
            employeeFirstName = certificate?.employeeFirstName ?: pendingCertificate.employeeFirstName,
            employeeLastName = certificate?.employeeLastName ?: pendingCertificate.employeeLastName,
            employeeJobTitle = certificate?.employeeJobTitle ?: pendingCertificate.employeeJobTitle,
            employeeAddress = certificate?.employeeAddress ?: pendingCertificate.employeeAddress,
            birthDate = certificate?.birthDate ?: pendingCertificate.birthDate,
            signatureUri = certificate?.signatureUri ?: pendingCertificate.signatureUri
        )

    private inline fun <reified T> T?.orIllegalState(): T = this ?: throw IllegalStateException("Shouldn't be null at this point!")

    sealed class Action {
        data class OpenDocument(val documentIdentifier: DocumentIdentifier) : Action()
    }

    interface PendingDocument

    data class PendingStatement(
        val firstName: String? = null,
        val lastName: String? = null,
        val birthDate: Long? = null,
        val address: String? = null,
        val route: String? = null,
        val date: Long? = null,
        val signatureUri: String? = null
    ) : PendingDocument

    data class PendingCertificate(
        val employerFirstName: String? = null,
        val employerLastName: String? = null,
        val employerJobTitle: String? = null,
        val companyName: String? = null,
        val companyAddress: String? = null,
        val employeeFirstName: String? = null,
        val employeeLastName: String? = null,
        val employeeJobTitle: String? = null,
        val employeeAddress: String? = null,
        val birthDate: Long? = null,
        val route: String? = null,
        val transportationMethod: String? = null,
        val fromDate: Long? = null,
        val toDate: Long? = null,
        val signatureUri: String? = null
    ) : PendingDocument
}