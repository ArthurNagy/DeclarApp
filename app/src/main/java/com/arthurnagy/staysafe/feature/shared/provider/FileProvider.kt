package com.arthurnagy.staysafe.feature.shared.provider

import android.content.Context
import java.io.File
import java.io.FileOutputStream

class FileProvider(private val context: Context) {

    fun openAppFile(fileName: String, func: (fileOutputStream: FileOutputStream) -> Unit) =
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { func(it) }

    fun getAppFile(fileName: String) = File(context.filesDir, fileName)
}