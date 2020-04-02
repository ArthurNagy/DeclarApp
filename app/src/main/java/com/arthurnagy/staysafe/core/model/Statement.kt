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
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "route") val route: String,
    @ColumnInfo(name = "motive") val motive: Motive,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "signature") val signaturePath: String
)