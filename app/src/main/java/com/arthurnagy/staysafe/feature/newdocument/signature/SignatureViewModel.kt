package com.arthurnagy.staysafe.feature.newdocument.signature

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.core.ResultWrapper
import com.arthurnagy.staysafe.core.StatementLocalSource
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.Event
import com.arthurnagy.staysafe.feature.shared.combine
import com.arthurnagy.staysafe.feature.shared.doOnChanged
import com.arthurnagy.staysafe.feature.shared.provider.FileProvider
import com.arthurnagy.staysafe.feature.shared.tint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SignatureViewModel(
    private val newDocumentViewModel: NewDocumentViewModel,
    private val statementLocalSource: StatementLocalSource,
    private val fileProvider: FileProvider
) : ViewModel() {

    private val pendingStatement: LiveData<NewDocumentViewModel.PendingStatement> get() = newDocumentViewModel.pendingStatement

    val hasExistingSignature: LiveData<Boolean> = newDocumentViewModel.hasExistingSignature
    val existingSignaturePath: LiveData<String?> get() = newDocumentViewModel.existingSignaturePath
    private val newSignatureFileName = SIGNATURE_FILE_NAME_SUFFIX.format(System.currentTimeMillis())
    private val newSignatureCreated = MutableLiveData(false)

    private val _events = MutableLiveData<Event<Action>>()
    val events: LiveData<Event<Action>> get() = _events

    private val _checkedSignatureSelection = MutableLiveData<Int>()
    val checkedSignatureSelection: LiveData<Int> get() = _checkedSignatureSelection

    val isGenerateEnabled: LiveData<Boolean> =
        combine(hasExistingSignature, newSignatureCreated, checkedSignatureSelection) { hasExistingSignature, newSignatureCreated, checkedSignatureSelection ->
            when (checkedSignatureSelection) {
                R.id.signature_existing -> hasExistingSignature
                else -> newSignatureCreated
            }
        }
    val isExistingVisible: LiveData<Boolean> get() = checkedSignatureSelection.map { it == R.id.signature_existing }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

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
        newSignatureCreated.value = false
    }

    fun onSignatureCreated() {
        newSignatureCreated.value = true
    }

    fun onGenerateDocument(newSignature: Bitmap, signatureColor: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            setFinalSignature(newSignature, signatureColor)
            pendingStatement.value?.let { pendingStatement ->
                when (val statementIdResult = createNewStatement(pendingStatement)) {
                    is ResultWrapper.Success -> _events.value = Event(Action.OpenDocument(documentId = statementIdResult.value))
                    is ResultWrapper.Error -> Timber.e(statementIdResult.exception)
                }
                _isLoading.value = false
            }
        }
    }

    private suspend fun setFinalSignature(newSignature: Bitmap, signatureColor: Int) {
        when (checkedSignatureSelection.value) {
            R.id.signature_new -> {
                try {
                    val signaturePng = withContext(Dispatchers.IO) {
                        val signatureBitmap = newSignature.tint(signatureColor)
                        fileProvider.openAppFile(fileName = newSignatureFileName) {
                            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                        }
                        fileProvider.getAppFile(fileName = newSignatureFileName)
                    }
                    newDocumentViewModel.updateStatement { copy(signaturePath = signaturePng.path) }
                } catch (exception: Exception) {
                    Timber.e(exception.message.orEmpty())
                }
            }
            R.id.signature_existing -> newDocumentViewModel.updateStatement { copy(signaturePath = existingSignaturePath.value) }
        }
    }

    private suspend fun createNewStatement(pendingStatement: NewDocumentViewModel.PendingStatement): ResultWrapper<Long> {
        val newStatement = Statement(
            firstName = pendingStatement.firstName.orIllegalState(),
            lastName = pendingStatement.lastName.orIllegalState(),
            birthDate = pendingStatement.birthDate.orIllegalState(),
            birthdayLocation = pendingStatement.birthdayLocation.orIllegalState(),
            location = pendingStatement.location.orIllegalState(),
            currentLocation = pendingStatement.currentLocation.orIllegalState(),
            motives = pendingStatement.motives.orIllegalState(),
            workLocation = pendingStatement.workLocation,
            workAddresses = pendingStatement.workAddresses,
            date = pendingStatement.date.orIllegalState(),
            signaturePath = pendingStatement.signaturePath.orIllegalState(),
            restrictionStartHour = pendingStatement.restrictionStartHour.orIllegalState(),
            createdAt = System.currentTimeMillis()
        )
        return statementLocalSource.save(newStatement)
    }

    private inline fun <reified T> T?.orIllegalState(): T = this ?: throw IllegalStateException("Shouldn't be null at this point!")

    sealed class Action {
        data class OpenDocument(val documentId: Long) : Action()
    }

    companion object {
        private const val SIGNATURE_FILE_NAME_SUFFIX = "%d_signature.png"
    }
}