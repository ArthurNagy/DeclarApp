package com.arthurnagy.staysafe.feature.newdocument

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.arthurnagy.staysafe.core.db.StatementDao
import com.arthurnagy.staysafe.core.model.Motive
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.shared.mediatorLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class NewDocumentViewModel(private val statementDao: StatementDao) : ViewModel() {

    private val lastSavedStatement: LiveData<Statement?> = liveData {
        emit(
            try {
                statementDao.getLast()
            } catch (exception: NoSuchElementException) {
                null
            }
        )
    }
    val hasExistingSignature: LiveData<Boolean> = lastSavedStatement.map { it?.signaturePath != null }
    val existingSignaturePath: LiveData<String?> = lastSavedStatement.map { it?.signaturePath }

    private val _pendingStatement: MutableLiveData<PendingStatement> = mediatorLiveData(
        defaultValue = PendingStatement(),
        dependency = lastSavedStatement,
        onChanged = { pendingDocument: PendingStatement?, lastSavedDocument: Statement? ->
            transformLastSavedStatementToPendingStatement(pendingDocument, lastSavedDocument)
        })
    val pendingStatement: LiveData<PendingStatement> get() = _pendingStatement

    fun updateStatement(func: PendingStatement.() -> PendingStatement?) {
        _pendingStatement.value = _pendingStatement.value?.func()
    }

    private fun transformLastSavedStatementToPendingStatement(pendingStatement: PendingStatement?, statement: Statement?): PendingStatement? =
        pendingStatement?.copy(
            firstName = statement?.firstName ?: pendingStatement.firstName,
            lastName = statement?.lastName ?: pendingStatement.lastName,
            birthDate = statement?.birthDate ?: pendingStatement.birthDate,
            address = statement?.address ?: pendingStatement.address
        )

    data class PendingStatement(
        val firstName: String? = null,
        val lastName: String? = null,
        val birthDate: Long? = null,
        val address: String? = null,
        val route: String? = null,
        val motive: Motive? = null,
        val date: Long? = null,
        val signaturePath: String? = null
    )

    class Factory(private val statementDao: StatementDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewDocumentViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NewDocumentViewModel(statementDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}