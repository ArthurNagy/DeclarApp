package com.arthurnagy.staysafe.feature.documentdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.arthurnagy.staysafe.core.db.StatementDao
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.shared.Event
import kotlinx.coroutines.launch

class DocumentDetailViewModel(
    private val documentId: Long,
    private val statementDao: StatementDao
) : ViewModel() {
    private val _navigateBackEvent = MutableLiveData<Event<Unit>>()
    val navigateBackEvent: LiveData<Event<Unit>> get() = _navigateBackEvent
    val statement: LiveData<Statement> = liveData {
        emit(statementDao.getById(documentId))
    }

    fun deleteDocument() {
        viewModelScope.launch {
            statement.value?.let { document ->
                statementDao.delete(document)
                _navigateBackEvent.value = Event(Unit)
            }
        }
    }
}