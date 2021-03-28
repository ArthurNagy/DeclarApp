package com.arthurnagy.staysafe.feature.home

import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.shared.formatToDate
import com.arthurnagy.staysafe.feature.shared.isToday
import com.arthurnagy.staysafe.feature.shared.labelRes

data class StatementUiModel(val statement: Statement) {
    val motiveTextResList: List<Int> get() = statement.motives.map { it.labelRes }
    val date: String get() = "${formatToDate(statement.date)}, ${statement.restrictionStartHour}:00 - 05:00"
    val isUpdateVisible: Boolean get() = !isToday(statement.date)
}