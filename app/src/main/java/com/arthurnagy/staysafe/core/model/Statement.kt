package com.arthurnagy.staysafe.core.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Statement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "birth_date") val birthDate: Long,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "current_location") val currentLocation: String,
    @ColumnInfo(name = "birthday_location") val birthdayLocation: String,
    @ColumnInfo(name = "work_location") val workLocation: String?,
    @ColumnInfo(name = "work_addresses") val workAddresses: String?,
    @ColumnInfo(name = "motives") val motives: List<Motive>,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "signature") val signaturePath: String,
    @ColumnInfo(name = "created_at") val createdAt: Long
)