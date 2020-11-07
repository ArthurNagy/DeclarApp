package com.arthurnagy.staysafe.core.model

import androidx.room.TypeConverter

enum class Motive {
    PROFESSIONAL_INTERESTS,
    MEDICAL_ASSISTANCE,
    PURCHASE_OF_MEDICATION,
    MOTIVE_HELP,
    MOTIVE_FAMILY_DECEASE
}

class MotivesConverter {

    @TypeConverter
    fun from(motives: String?): List<Motive>? = motives?.split(',')?.mapNotNull { motiveString ->
        Motive.values().find { it.ordinal == motiveString.trim().toInt() }
    }

    @TypeConverter
    fun to(motives: List<Motive>?): String? = motives?.joinToString { it.ordinal.toString() }
}

class MotiveConverter {
    @TypeConverter
    fun fromInt(value: Int?): Motive? {
        return Motive.values().find { it.ordinal == value }
    }

    @TypeConverter
    fun motiveToInt(motive: Motive?): Int? {
        return motive?.ordinal
    }
}