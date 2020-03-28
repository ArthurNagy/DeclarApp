package com.arthurnagy.staysafe.core.model

import androidx.room.TypeConverter

enum class Motive {
    PROFESSIONAL_INTERESTS,
    NECESSITY_PROVISIONING,
    MEDICAL_ASSISTANCE,
    JUSTIFIED_HELP,
    PHYSICAL_ACTIVITY,
    AGRICULTURAL_ACTIVITIES,
    BLOOD_DONATION,
    VOLUNTEERING,
    COMMERCIALIZE_AGRICULTURAL_PRODUCES,
    PROFESSIONAL_ACTIVITY_JUSTIFICATION_NECESSITIES
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