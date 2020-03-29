package com.arthurnagy.staysafe.feature.util

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

@Suppress("SpreadOperator")
class StringProvider(private val context: Context) {
    fun getString(@StringRes stringRes: Int): String = context.getString(stringRes)

    fun getString(@StringRes stringRes: Int, vararg arguments: Any): String = context.getString(stringRes, *arguments)

    fun getPluralString(@PluralsRes pluralRes: Int, quantity: Int, vararg args: Any): String = context.resources.getQuantityString(pluralRes, quantity, *args)
}