package com.arthurnagy.staysafe.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.arthurnagy.staysafe.core.db.StatementDao
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.shared.Event
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val statementDao: StatementDao) : ViewModel() {

    private val _statementDeletedEvent = MutableLiveData<Event<Statement>>()
    val statementDeletedEvent: LiveData<Event<Statement>> get() = _statementDeletedEvent

    private val _statementDateUpdatedEvent = MutableLiveData<Event<Unit>>()
    val statementDateUpdatedEvent: LiveData<Event<Unit>> get() = _statementDateUpdatedEvent

    val purchaseEvent = MutableLiveData<Event<Unit>>()

    val items: LiveData<List<StatementUiModel>> = statementDao.getDistinct().map { statements -> statements.map { StatementUiModel(it) } }.asLiveData()
    val isEmpty: LiveData<Boolean> = items.map { it.isEmpty() }

    fun updateStatementDate(timestamp: Long, statement: Statement) {
        viewModelScope.launch {
            statementDao.update(statement.copy(date = timestamp))
            _statementDateUpdatedEvent.value = Event(Unit)
        }
    }

    fun deleteStatement(statement: Statement) {
        viewModelScope.launch {
            statementDao.delete(statement)
            _statementDeletedEvent.value = Event(statement)
        }
    }

    fun undoStatementDeletion(statement: Statement) {
        viewModelScope.launch {
            statementDao.insert(statement)
        }
    }
}