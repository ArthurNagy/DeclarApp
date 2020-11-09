package com.arthurnagy.staysafe.feature.newdocument.statement.routedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.arthurnagy.staysafe.core.model.Motive
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.formatToDate
import com.arthurnagy.staysafe.feature.shared.mediatorLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

class StatementRouteDataViewModel(private val newDocumentViewModel: NewDocumentViewModel) : ViewModel() {
    private val pendingStatement: LiveData<NewDocumentViewModel.PendingStatement> = newDocumentViewModel.pendingStatement

    val date: LiveData<Long?> = pendingStatement.map { it.date }
    val dateFormatted: LiveData<String> = date.map { it?.let { formatToDate(it) } ?: "" }

    val isNextEnabled: LiveData<Boolean> = pendingStatement.map(::areStatementRouteDataValid)

    private val motives: Flow<List<Motive>> = flowOf(Motive.values().toList())
    private val selectedMotives: LiveData<List<Motive>?> = pendingStatement.map { it.motives }

    val motiveItems: LiveData<List<MotiveUiModel>> = motives.combine(selectedMotives.asFlow()) { motives, selectedMotives ->
        motives.map {
            MotiveUiModel(motive = it, selected = selectedMotives?.contains(it) == true)
        }
    }.asLiveData()

    val displayWorkData: LiveData<Boolean> = motiveItems.map { motives ->
        motives.find { it.motive === Motive.PROFESSIONAL_INTERESTS }?.selected == true
    }

    val workLocation = mediatorLiveData("", pendingStatement) { currentValue, pendingStatement ->
        if (pendingStatement.workLocation != currentValue) {
            pendingStatement.workLocation
        } else {
            currentValue
        }
    }
    val workAddresses = mediatorLiveData("", pendingStatement) { currentValue, pendingStatement ->
        if (pendingStatement.workAddresses != currentValue) {
            pendingStatement.workAddresses
        } else {
            currentValue
        }
    }

    init {
        workLocation.observeForever {
            if (pendingStatement.value?.workLocation != it) {
                newDocumentViewModel.updateStatement { copy(workLocation = it) }
            }
        }
        workAddresses.observeForever {
            if (pendingStatement.value?.workAddresses != it) {
                newDocumentViewModel.updateStatement { copy(workAddresses = it) }
            }
        }
    }

    private fun areStatementRouteDataValid(pendingStatement: NewDocumentViewModel.PendingStatement) =
        !pendingStatement.motives.isNullOrEmpty() && isProfessionalInterestValid(pendingStatement) && pendingStatement.date != null

    private fun isProfessionalInterestValid(pendingStatement: NewDocumentViewModel.PendingStatement): Boolean = pendingStatement.motives?.let { motives ->
        motives.find { it === Motive.PROFESSIONAL_INTERESTS }?.let {
            !pendingStatement.workLocation.isNullOrBlank() && !pendingStatement.workAddresses.isNullOrBlank()
        } ?: true
    } ?: false

    fun onDateSelected(date: Long) {
        if (pendingStatement.value?.date != date) {
            newDocumentViewModel.updateStatement { copy(date = date) }
        }
    }

    fun onMotiveSelected(isSelected: Boolean, motiveIndex: Int) {
        motiveItems.value?.get(motiveIndex)?.let { motiveUiModel ->
            if (isSelected != motiveUiModel.selected) {
                val currentlySelectedMotive = motiveUiModel.copy(selected = isSelected)
                // If it is currently selected we need to remove it from the selected list, if not we need to add it
                newDocumentViewModel.updateStatement {
                    val newSelectedMotives = this.motives.orEmpty().toMutableList().apply {
                        if (currentlySelectedMotive.selected) {
                            add(currentlySelectedMotive.motive)
                        } else {
                            remove(currentlySelectedMotive.motive)
                        }
                    }
                    copy(motives = newSelectedMotives)
                }
            }
        }
    }

    data class MotiveUiModel(val motive: Motive, val selected: Boolean)
}