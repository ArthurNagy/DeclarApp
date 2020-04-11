package com.arthurnagy.staysafe.feature.newdocument.statement.routedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.formatToDate
import com.arthurnagy.staysafe.feature.shared.labelRes
import com.arthurnagy.staysafe.feature.shared.provider.StringProvider

class StatementRouteDataViewModel(private val newDocumentViewModel: NewDocumentViewModel, stringProvider: StringProvider) : ViewModel() {
    private val pendingStatement: LiveData<NewDocumentViewModel.PendingStatement> get() = newDocumentViewModel.pendingStatement
    val route = MutableLiveData<String>()
    val selectedMotive: LiveData<String> =
        pendingStatement.map { pendingStatement -> pendingStatement.motive?.let { stringProvider.getString(it.labelRes) } ?: "" }
    val date: LiveData<Long?> = pendingStatement.map { it.date }
    val dateFormatted: LiveData<String> = date.map { it?.let { formatToDate(it) } ?: "" }
    val isNextEnabled: LiveData<Boolean> = pendingStatement.map(::areStatementRouteDataValid)

    init {
        route.observeForever {
            if (pendingStatement.value?.route != it) {
                newDocumentViewModel.updateStatement { copy(route = it) }
            }
        }
    }

    private fun areStatementRouteDataValid(pendingStatement: NewDocumentViewModel.PendingStatement) =
        !pendingStatement.route.isNullOrEmpty() && pendingStatement.motive != null &&
            pendingStatement.date != null

    fun onDateSelected(date: Long) {
        if (pendingStatement.value?.date != date) {
            newDocumentViewModel.updateStatement { copy(date = date) }
        }
    }
}