package com.arthurnagy.staysafe.feature

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Document(val id: Int, val type: Type) : Parcelable