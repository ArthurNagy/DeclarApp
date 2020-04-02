package com.arthurnagy.staysafe.feature.newdocument

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.core.db.StatementDao
import com.arthurnagy.staysafe.core.model.Motive
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.shared.Event
import com.arthurnagy.staysafe.feature.shared.mediatorLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

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

    private val _pendingStatement: MutableLiveData<PendingStatement> = mediatorLiveData(
        defaultValue = PendingStatement(),
        dependency = lastSavedStatement,
        onChanged = { pendingDocument: PendingStatement?, lastSavedDocument: Statement? ->
            transformLastSavedStatementToPendingStatement(pendingDocument, lastSavedDocument)
        })
    val pendingDocument: LiveData<PendingStatement> get() = _pendingStatement

    private val _currentPageIndex = MutableLiveData<Int>(NewDocumentPagerAdapter.STATEMENT_PERSONAL_DATA_INDEX)
    val currentPageIndex: LiveData<Int> get() = _currentPageIndex

    val isActionEnabled: LiveData<Boolean> =
        currentPageIndex.asFlow().combine(pendingDocument.asFlow()) { currentPageIndex: Int, pendingDocument: PendingStatement ->
            isActionEnabled(currentPageIndex, pendingDocument)
        }.asLiveData()
    val actionText: LiveData<Int> = currentPageIndex.map {
        val targetEndIndex = NewDocumentPagerAdapter.STATEMENT_SIGNATURE_INDEX
        if (currentPageIndex.value == targetEndIndex) {
            R.string.generate
        } else {
            R.string.next
        }
    }

    private val _events = MutableLiveData<Event<Action>>()
    val events: LiveData<Event<Action>> get() = _events

    fun updateStatement(func: PendingStatement.() -> PendingStatement?) {
        _pendingStatement.value = _pendingStatement.value?.func()
    }

    fun clearSignature() {
        _pendingStatement.value?.copy(signaturePath = null)
    }

    fun updateSignature(signaturePath: String) {
        _pendingStatement.value?.copy(signaturePath = signaturePath)
    }

    fun onActionSelected() {
        if (currentPageIndex.value == NewDocumentPagerAdapter.STATEMENT_SIGNATURE_INDEX) {
            generateDocument()
        } else {
            _currentPageIndex.value = _currentPageIndex.value?.inc()
        }
    }

    private fun generateDocument() {
        viewModelScope.launch {
            pendingDocument.value?.let { pendingStatement ->
                val statementId = createNewStatement(pendingStatement)
                _events.value = Event(Action.OpenDocument(documentId = statementId))
            }
        }
    }

    fun updateCurrentPageIndex(index: Int) {
        if (index != _currentPageIndex.value) {
            _currentPageIndex.value = index
        }
    }

    private fun isActionEnabled(currentPageIndex: Int, pendingStatement: PendingStatement): Boolean = when (currentPageIndex) {
        NewDocumentPagerAdapter.STATEMENT_PERSONAL_DATA_INDEX -> areStatementPersonalDataValid(pendingStatement)
        NewDocumentPagerAdapter.STATEMENT_ROUTE_DATA_INDEX -> areStatementRouteDataValid(pendingStatement)
        NewDocumentPagerAdapter.STATEMENT_SIGNATURE_INDEX -> pendingStatement.signaturePath != null
        else -> false
    }

    private fun areStatementPersonalDataValid(pendingStatement: PendingStatement) =
        !pendingStatement.firstName.isNullOrEmpty() && !pendingStatement.lastName.isNullOrEmpty() &&
            !pendingStatement.address.isNullOrBlank() && pendingStatement.birthDate != null

    private fun areStatementRouteDataValid(pendingStatement: PendingStatement) = !pendingStatement.route.isNullOrEmpty() && pendingStatement.motive != null &&
        pendingStatement.date != null

    private suspend fun createNewStatement(pendingStatement: PendingStatement): Long {
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

    private fun transformLastSavedStatementToPendingStatement(pendingStatement: PendingStatement?, statement: Statement?): PendingStatement? =
        pendingStatement?.copy(
            firstName = statement?.firstName ?: pendingStatement.firstName,
            lastName = statement?.lastName ?: pendingStatement.lastName,
            birthDate = statement?.birthDate ?: pendingStatement.birthDate,
            address = statement?.address ?: pendingStatement.address,
            signaturePath = statement?.signaturePath ?: pendingStatement.signaturePath
        )

    private inline fun <reified T> T?.orIllegalState(): T = this ?: throw IllegalStateException("Shouldn't be null at this point!")

    sealed class Action {
        data class OpenDocument(val documentId: Long) : Action()
    }

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
                return NewDocumentViewModel(statementDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}