package com.arthurnagy.staysafe.feature.newdocument.statement.type

import androidx.lifecycle.ViewModel
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel

class StatementTypeViewModel(private val newDocumentViewModel: NewDocumentViewModel) : ViewModel() {

    fun updateStatementStartHour(startHour: Int) {
        newDocumentViewModel.updateStatement { copy(restrictionStartHour = startHour) }
    }

}