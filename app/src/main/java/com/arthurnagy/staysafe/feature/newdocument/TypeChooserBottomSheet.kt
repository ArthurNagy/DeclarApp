package com.arthurnagy.staysafe.feature.newdocument

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arthurnagy.staysafe.TypeChooserBinding
import com.arthurnagy.staysafe.feature.DocumentType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.halcyonmobile.android.common.extensions.navigation.findSafeNavController
import dev.chrisbanes.insetter.InsetterBindingAdapters

class TypeChooserBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = super.onCreateDialog(savedInstanceState).apply {
        window?.decorView?.let { InsetterBindingAdapters.setEdgeToEdgeFlags(it, true) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        TypeChooserBinding.inflate(inflater, container, false).also {
            it.certificate.setOnClickListener {
                findSafeNavController().navigate(TypeChooserBottomSheetDirections.actionTypeChooserBottomSheetToNewDocument(DocumentType.CERTIFICATE))
            }
            it.statement.setOnClickListener {
                findSafeNavController().navigate(TypeChooserBottomSheetDirections.actionTypeChooserBottomSheetToNewDocument(DocumentType.STATEMENT))
            }
        }.root
}