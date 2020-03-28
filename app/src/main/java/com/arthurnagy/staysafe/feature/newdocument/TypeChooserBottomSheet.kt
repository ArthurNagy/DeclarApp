package com.arthurnagy.staysafe.feature.newdocument

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.arthurnagy.staysafe.TypeChooserBinding
import com.arthurnagy.staysafe.feature.DocumentType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.chrisbanes.insetter.InsetterBindingAdapters

class TypeChooserBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.decorView?.let { InsetterBindingAdapters.setEdgeToEdgeFlags(it, true) }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        TypeChooserBinding.inflate(inflater, container, false).also {
            it.certificate.setOnClickListener {
                findNavController().navigate(TypeChooserBottomSheetDirections.actionTypeChooserBottomSheetToNewDocumentFragment(DocumentType.CERTIFICATE))
            }
            it.statement.setOnClickListener {
                findNavController().navigate(TypeChooserBottomSheetDirections.actionTypeChooserBottomSheetToNewDocumentFragment(DocumentType.STATEMENT))
            }
        }.root
}