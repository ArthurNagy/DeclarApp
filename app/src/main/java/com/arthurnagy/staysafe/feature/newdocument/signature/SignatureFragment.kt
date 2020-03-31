package com.arthurnagy.staysafe.feature.newdocument.signature

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.SignatureBinding
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.color
import com.arthurnagy.staysafe.feature.shared.parentGraphViewModel
import com.arthurnagy.staysafe.feature.shared.tint
import com.github.gcacace.signaturepad.views.SignaturePad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class SignatureFragment : Fragment(R.layout.fragment_signature) {

    private val sharedViewModel by parentGraphViewModel<NewDocumentViewModel>(navGraphId = R.id.newDocument)
    private val viewModel: SignatureViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = SignatureBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@SignatureFragment.viewModel
        }
        with(binding) {
            clear.setOnClickListener {
                signaturePad.clear()
            }
            signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
                override fun onStartSigning() {
                }

                override fun onClear() {
                    viewModel?.onClearSignature()
                }

                override fun onSigned() {
                    lifecycleScope.launch {
                        viewModel?.let { signatureViewModel ->
                            val signatureBitmap = signaturePad.transparentSignatureBitmap.tint(requireContext().color(R.color.signature_pen_color))
                            val signaturePng = withContext(Dispatchers.IO) {
                                requireContext().openFileOutput(signatureViewModel.fileName, Context.MODE_PRIVATE).use {
                                    signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                                }
                                File(requireContext().filesDir, signatureViewModel.fileName)
                            }
                            signatureViewModel.onSignatureCreated(signaturePng.path)
                        }
                    }
                }
            })
        }
    }
}