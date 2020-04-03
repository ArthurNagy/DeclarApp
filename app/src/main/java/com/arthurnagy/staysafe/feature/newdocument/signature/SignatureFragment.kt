package com.arthurnagy.staysafe.feature.newdocument.signature

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Slide
import android.view.Gravity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.SignatureBinding
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.color
import com.arthurnagy.staysafe.feature.shared.tint
import com.github.gcacace.signaturepad.views.SignaturePad
import com.halcyonmobile.android.common.extensions.navigation.findSafeNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class SignatureFragment : Fragment(R.layout.fragment_signature) {

    private val sharedViewModel by navGraphViewModels<NewDocumentViewModel>(navGraphId = R.id.nav_new_document)
    private val viewModel: SignatureViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = Slide(Gravity.END)
        sharedElementEnterTransition = ChangeBounds()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = SignatureBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@SignatureFragment.viewModel
        }
        with(binding) {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            clear.setOnClickListener {
                signaturePad.clear()
                viewModel?.onClearSignature()
            }
            signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
                override fun onStartSigning() {
                }

                override fun onClear() {
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
        viewModel.events.observe(viewLifecycleOwner) {
            when (val action = it.consume()) {
                is SignatureViewModel.Action.OpenDocument -> findSafeNavController().navigate(
                    SignatureFragmentDirections.actionGlobalDocumentDetailFragment(action.documentId)
                )
            }
        }
    }
}