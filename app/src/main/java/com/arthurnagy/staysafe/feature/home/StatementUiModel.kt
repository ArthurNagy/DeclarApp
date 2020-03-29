package com.arthurnagy.staysafe.feature.home

import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.shared.formatToDate
import com.arthurnagy.staysafe.feature.shared.labelRes

data class StatementUiModel(val statement: Statement) {
    val motiveTextRes: Int get() = statement.motive.labelRes
    val date: String get() = formatToDate(statement.date)
}