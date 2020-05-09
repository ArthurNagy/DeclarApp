package com.arthurnagy.staysafe.feature.documentdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.arthurnagy.staysafe.core.Result
import com.arthurnagy.staysafe.core.StatementLocalSource
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.shared.Event
import kotlinx.coroutines.launch
import timber.log.Timber

class DocumentDetailViewModel(
    private val documentId: Long,
    private val statementLocalSource: StatementLocalSource
) : ViewModel() {
    private val _navigateBackEvent = MutableLiveData<Event<Unit>>()
    val navigateBackEvent: LiveData<Event<Unit>> get() = _navigateBackEvent
    val statement: LiveData<Statement> = liveData {
        when (val result = statementLocalSource.getById(documentId)) {
            is Result.Success -> emit(result.value)
            is Result.Error -> Timber.e(result.exception)
        }
    }

    fun deleteDocument() {
        viewModelScope.launch {
            statement.value?.let { document ->
                when (val result = statementLocalSource.delete(document)) {
                    is Result.Success -> _navigateBackEvent.value = Event(Unit)
                    is Result.Error -> Timber.e(result.exception)
                }
            }
        }
    }
}