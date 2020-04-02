package com.arthurnagy.staysafe.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.arthurnagy.staysafe.core.db.StatementDao
import kotlinx.coroutines.flow.map

class HomeViewModel(statementDao: StatementDao) : ViewModel() {
    val items: LiveData<List<StatementUiModel>> = statementDao.get().map { statements -> statements.map { StatementUiModel(it) } }.asLiveData()
    val isEmpty: LiveData<Boolean> = items.map { it.isEmpty() }
}