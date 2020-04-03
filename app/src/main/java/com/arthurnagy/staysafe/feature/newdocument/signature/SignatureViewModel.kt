package com.arthurnagy.staysafe.feature.newdocument.signature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.arthurnagy.staysafe.core.db.StatementDao
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.Event
import kotlinx.coroutines.launch

class SignatureViewModel(
    private val newDocumentViewModel: NewDocumentViewModel,
    private val statementDao: StatementDao
) : ViewModel() {

    private val pendingStatement: LiveData<NewDocumentViewModel.PendingStatement> get() = newDocumentViewModel.pendingStatement

    val hasExistingSignature: LiveData<Boolean> = newDocumentViewModel.hasExistingSignature
    val fileName = SIGNATURE_FILE_NAME_SUFFIX.format(System.currentTimeMillis())
    val isGenerateEnabled: LiveData<Boolean> = pendingStatement.map { it.signaturePath != null }
    private val _events = MutableLiveData<Event<Action>>()
    val events: LiveData<Event<Action>> get() = _events

    fun onClearSignature() {
        newDocumentViewModel.updateStatement { copy(signaturePath = null) }
    }

    fun onSignatureCreated(signaturePath: String) {
        newDocumentViewModel.updateStatement { copy(signaturePath = signaturePath) }
    }

    fun onGenerateDocument() {
        viewModelScope.launch {
            pendingStatement.value?.let { pendingStatement ->
                val statementId = createNewStatement(pendingStatement)
                _events.value = Event(Action.OpenDocument(documentId = statementId))
            }
        }
    }

    private suspend fun createNewStatement(pendingStatement: NewDocumentViewModel.PendingStatement): Long {
        val newStatement = Statement(
            firstName = pendingStatement.firstName.orIllegalState(),
            lastName = pendingStatement.lastName.orIllegalState(),
            birthDate = pendingStatement.birthDate.orIllegalState(),
            address = pendingStatement.address.orIllegalState(),
            route = pendingStatement.route.orIllegalState(),
            motive = pendingStatement.motive.orIllegalState(),
            date = pendingStatement.date.orIllegalState(),
            signaturePath = pendingStatement.signaturePath.orIllegalState()
        )
        return statementDao.insert(newStatement)
    }

    private inline fun <reified T> T?.orIllegalState(): T = this ?: throw IllegalStateException("Shouldn't be null at this point!")

    sealed class Action {
        data class OpenDocument(val documentId: Long) : Action()
    }

    companion object {
        private const val SIGNATURE_FILE_NAME_SUFFIX = "%d_signature.png"
    }
}