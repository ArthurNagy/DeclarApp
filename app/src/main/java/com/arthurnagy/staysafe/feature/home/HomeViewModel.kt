package com.arthurnagy.staysafe.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.arthurnagy.staysafe.core.Result
import com.arthurnagy.staysafe.core.StatementLocalSource
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.shared.Event
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(private val statementLocalSource: StatementLocalSource) : ViewModel() {

    private val _statementDeletedEvent = MutableLiveData<Event<Statement>>()
    val statementDeletedEvent: LiveData<Event<Statement>> get() = _statementDeletedEvent

    private val _statementDateUpdatedEvent = MutableLiveData<Event<Unit>>()
    val statementDateUpdatedEvent: LiveData<Event<Unit>> get() = _statementDateUpdatedEvent

    val purchaseEvent = MutableLiveData<Event<Unit>>()

    val items: LiveData<List<StatementUiModel>> = statementLocalSource.getStatementsFlow().map { statements -> statements.map { StatementUiModel(it) } }
        .asLiveData()
    val isEmpty: LiveData<Boolean> = items.map { it.isEmpty() }

    fun updateStatementDate(timestamp: Long, statement: Statement) {
        viewModelScope.launch {
            when (val result = statementLocalSource.update(statement.copy(date = timestamp))) {
                is Result.Success -> _statementDateUpdatedEvent.value = Event(Unit)
                is Result.Error -> Timber.e(result.exception)
            }
        }
    }

    fun deleteStatement(statement: Statement) {
        viewModelScope.launch {
            when (val result = statementLocalSource.delete(statement)) {
                is Result.Success -> _statementDeletedEvent.value = Event(statement)
                is Result.Error -> Timber.e(result.exception)
            }
        }
    }

    fun undoStatementDeletion(statement: Statement) {
        viewModelScope.launch {
            statementLocalSource.save(statement)
        }
    }
}