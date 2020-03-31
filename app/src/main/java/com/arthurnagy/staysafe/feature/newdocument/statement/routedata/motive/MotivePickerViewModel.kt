package com.arthurnagy.staysafe.feature.newdocument.statement.routedata.motive

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.core.model.Motive
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.labelRes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

class MotivePickerViewModel(private val newDocumentViewModel: NewDocumentViewModel) : ViewModel() {

    private val motives: Flow<List<Motive>> = flowOf(Motive.values().toList())
    private val selectedMotive: LiveData<Motive?> = newDocumentViewModel.pendingDocument.map { (it as NewDocumentViewModel.PendingStatement).motive }
    val motiveItems: LiveData<List<UiModel>> = motives.combine(selectedMotive.asFlow()) { motives, selectedMotive ->
        motives.map {
            UiModel(motive = it, selected = it == selectedMotive)
        }
    }.asLiveData()

    fun onMotiveSelected(motiveUiModel: UiModel) {
        val newlySelectedMotive = if (motiveUiModel.selected) null else motiveUiModel.motive
        newDocumentViewModel.updateStatement { copy(motive = newlySelectedMotive) }
    }

    data class UiModel(val motive: Motive, val selected: Boolean) {
        @get:StringRes
        val motiveLabel: Int
            get() = motive.labelRes
        val checkTint get() = if (selected) R.color.color_secondary_variant else R.color.transparent
    }
}