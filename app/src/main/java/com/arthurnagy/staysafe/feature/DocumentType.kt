package com.arthurnagy.staysafe.feature

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
enum class DocumentType : Parcelable {
    STATEMENT,
    CERTIFICATE
}