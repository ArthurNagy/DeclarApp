package com.arthurnagy.staysafe.feature.newdocument.statement.routedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.arthurnagy.staysafe.core.model.Motive
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.util.StringProvider
import com.arthurnagy.staysafe.feature.util.formatToDate
import com.arthurnagy.staysafe.feature.util.labelRes

class StatementRouteDataViewModel(private val newDocumentViewModel: NewDocumentViewModel, stringProvider: StringProvider) : ViewModel() {
    private val pendingStatement: LiveData<NewDocumentViewModel.PendingStatement> =
        newDocumentViewModel.pendingDocument.map { it as NewDocumentViewModel.PendingStatement }
    val route = MutableLiveData<String>()
    val selectedMotive: LiveData<String> =
        pendingStatement.map { pendingStatement -> pendingStatement.motive?.let { stringProvider.getString(it.labelRes) } ?: "" }
    val date: LiveData<Long?> = pendingStatement.map { it.date }
    val dateFormatted: LiveData<String> = date.map { it?.let { formatToDate(it) } ?: "" }

    init {
        route.observeForever {
            if (pendingStatement.value?.route != it) {
                newDocumentViewModel.updateStatement { copy(route = it) }
            }
        }
    }

    fun onMotiveSelected(motive: Motive) {
        if (pendingStatement.value?.motive != motive) {
            newDocumentViewModel.updateStatement { copy(motive = motive) }
        }
    }

    fun onDateSelected(date: Long) {
        if (pendingStatement.value?.date != date) {
            newDocumentViewModel.updateStatement { copy(date = date) }
        }
    }
}