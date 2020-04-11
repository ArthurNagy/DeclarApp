package com.arthurnagy.staysafe.feature.newdocument.statement.routedata

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.core.model.Motive
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.formatToDate
import com.arthurnagy.staysafe.feature.shared.labelRes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

class StatementRouteDataViewModel(private val newDocumentViewModel: NewDocumentViewModel) : ViewModel() {
    private val pendingStatement: LiveData<NewDocumentViewModel.PendingStatement> get() = newDocumentViewModel.pendingStatement
    val route = MutableLiveData<String>()
    val date: LiveData<Long?> = pendingStatement.map { it.date }
    val dateFormatted: LiveData<String> = date.map { it?.let { formatToDate(it) } ?: "" }
    val isNextEnabled: LiveData<Boolean> = pendingStatement.map(::areStatementRouteDataValid)
    private val motives: Flow<List<Motive>> = flowOf(Motive.values().toList())
    private val selectedMotives: LiveData<List<Motive>?> = pendingStatement.map { it.motives }

    @OptIn(ExperimentalCoroutinesApi::class)
    val motiveItems: LiveData<List<MotiveUiModel>> = motives.combine(selectedMotives.asFlow()) { motives, selectedMotives ->
        motives.map {
            MotiveUiModel(motive = it, selected = selectedMotives?.contains(it) == true)
        }
    }.asLiveData()

    init {
        route.observeForever {
            if (pendingStatement.value?.route != it) {
                newDocumentViewModel.updateStatement { copy(route = it) }
            }
        }
    }

    private fun areStatementRouteDataValid(pendingStatement: NewDocumentViewModel.PendingStatement) =
        !pendingStatement.route.isNullOrEmpty() && !pendingStatement.motives.isNullOrEmpty() &&
            pendingStatement.date != null

    fun onDateSelected(date: Long) {
        if (pendingStatement.value?.date != date) {
            newDocumentViewModel.updateStatement { copy(date = date) }
        }
    }

    fun onMotiveSelected(motiveMotiveUiModel: MotiveUiModel) {
        // If it is currently selected we need to remove it from the selected list, if not we need to add it
        newDocumentViewModel.updateStatement {
            val newSelectedMotives = this.motives.orEmpty().toMutableList().apply {
                if (motiveMotiveUiModel.selected) {
                    remove(motiveMotiveUiModel.motive)
                } else {
                    add(motiveMotiveUiModel.motive)
                }
            }
            copy(motives = newSelectedMotives)
        }
    }

    data class MotiveUiModel(val motive: Motive, val selected: Boolean) {
        @get:StringRes
        val motiveLabel: Int
            get() = motive.labelRes
        val checkTint get() = if (selected) R.color.color_secondary_variant else R.color.transparent
    }
}