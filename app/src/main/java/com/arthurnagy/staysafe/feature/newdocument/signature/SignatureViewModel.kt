package com.arthurnagy.staysafe.feature.newdocument.signature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.core.db.StatementDao
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.Event
import com.arthurnagy.staysafe.feature.shared.doOnChanged
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
    private val _checkedSignatureSelection = MutableLiveData<Int>()
    val checkedSignatureSelection: LiveData<Int> get() = _checkedSignatureSelection
    val isExistingVisible: LiveData<Boolean> get() = checkedSignatureSelection.map { it == R.id.signature_existing }
    val existingSignaturePath: LiveData<String?> get() = newDocumentViewModel.existingSignaturePath

    init {
        hasExistingSignature.doOnChanged {
            _checkedSignatureSelection.value = if (it) R.id.signature_existing else R.id.signature_new
        }
    }

    fun toggleSignatureSelection() {
        _checkedSignatureSelection.value?.let { checkedSignatureSelection ->
            _checkedSignatureSelection.value = when (checkedSignatureSelection) {
                R.id.signature_existing -> R.id.signature_new
                else -> R.id.signature_existing
            }
        }
    }

    fun onClearSignature() {
        newDocumentViewModel.updateStatement { copy(signaturePath = null) }
    }

    fun onSignatureCreated(signaturePath: String) {
        newDocumentViewModel.updateStatement { copy(signaturePath = signaturePath) }
    }

    fun onGenerateDocument() {
        viewModelScope.launch {
            val finalSignature = when (checkedSignatureSelection.value) {
                R.id.signature_existing -> existingSignaturePath.value
                else -> pendingStatement.value?.signaturePath
            }

            finalSignature?.let {
                newDocumentViewModel.updateStatement {
                    copy(signaturePath = it)
                }
            }

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
            signaturePath = pendingStatement.signaturePath.orIllegalState(),
            createdAt = System.currentTimeMillis()
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