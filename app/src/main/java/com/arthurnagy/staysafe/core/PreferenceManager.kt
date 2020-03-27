package com.arthurnagy.staysafe.core

import android.content.Context
import com.arthurnagy.staysafe.feature.util.ThemeHelper
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceManager internal constructor(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
    var theme by PreferenceFieldDelegate.String("theme", ThemeHelper.DEFAULT_MODE)

    private sealed class PreferenceFieldDelegate<T>(protected val key: kotlin.String, protected val defaultValue: T) :
        ReadWriteProperty<PreferenceManager, T> {

        class Boolean(key: kotlin.String, defaultValue: kotlin.Boolean = false) : PreferenceFieldDelegate<kotlin.Boolean>(key, defaultValue) {

            override fun getValue(thisRef: PreferenceManager, property: KProperty<*>) = thisRef.preferences.getBoolean(key, defaultValue)

            override fun setValue(thisRef: PreferenceManager, property: KProperty<*>, value: kotlin.Boolean) =
                thisRef.preferences.edit().putBoolean(key, value).apply()
        }

        class String(key: kotlin.String, defaultValue: kotlin.String? = null) : PreferenceFieldDelegate<kotlin.String?>(key, defaultValue) {

            override fun getValue(thisRef: PreferenceManager, property: KProperty<*>) = thisRef.preferences.getString(key, defaultValue)

            override fun setValue(thisRef: PreferenceManager, property: KProperty<*>, value: kotlin.String?) =
                thisRef.preferences.edit().putString(key, value).apply()
        }

        class Int(key: kotlin.String, defaultValue: kotlin.Int = 0) : PreferenceFieldDelegate<kotlin.Int>(key, defaultValue) {

            override fun getValue(thisRef: PreferenceManager, property: KProperty<*>) = thisRef.preferences.getInt(key, defaultValue)

            override fun setValue(thisRef: PreferenceManager, property: KProperty<*>, value: kotlin.Int) =
                thisRef.preferences.edit().putInt(key, value).apply()
        }

        class Long(key: kotlin.String, defaultValue: kotlin.Long = 0) : PreferenceFieldDelegate<kotlin.Long>(key, defaultValue) {
            override fun getValue(thisRef: PreferenceManager, property: KProperty<*>): kotlin.Long = thisRef.preferences.getLong(key, defaultValue)

            override fun setValue(thisRef: PreferenceManager, property: KProperty<*>, value: kotlin.Long) {
                thisRef.preferences.edit().putLong(key, value).apply()
            }
        }

        class MutableSet(key: kotlin.String, defaultValue: Set<kotlin.String> = emptySet()) : PreferenceFieldDelegate<Set<kotlin.String>>(key, defaultValue) {
            override fun getValue(thisRef: PreferenceManager, property: KProperty<*>): Set<kotlin.String> =
                thisRef.preferences.getStringSet(key, defaultValue) ?: defaultValue

            override fun setValue(thisRef: PreferenceManager, property: KProperty<*>, value: Set<kotlin.String>) {
                thisRef.preferences.edit().putStringSet(key, value).apply()
            }
        }
    }
}