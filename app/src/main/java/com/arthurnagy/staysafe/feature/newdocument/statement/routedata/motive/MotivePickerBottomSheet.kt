package com.arthurnagy.staysafe.feature.newdocument.statement.routedata.motive

import android.app.Dialog
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.arthurnagy.staysafe.MotivePickerBinding
import com.arthurnagy.staysafe.R
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.sharedGraphViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.chrisbanes.insetter.InsetterBindingAdapters
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MotivePickerBottomSheet : BottomSheetDialogFragment() {

    private val sharedViewModel by sharedGraphViewModel<NewDocumentViewModel>(navGraphId = R.id.newDocument)
    private val viewModel: MotivePickerViewModel by viewModel { parametersOf(sharedViewModel) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = super.onCreateDialog(savedInstanceState).apply {
        window?.decorView?.let { InsetterBindingAdapters.setEdgeToEdgeFlags(it, true) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.bottom_sheet_motive_picker, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = MotivePickerBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@MotivePickerBottomSheet.viewModel
        }
        val motiveAdapter = MotiveAdapter {
            viewModel.onMotiveSelected(it)
            // It means that this will be the newly selected one, so we can dismiss the dialog
            if (!it.selected) {
                findNavController().navigateUp()
            }
        }
        binding.recycler.apply {
            adapter = motiveAdapter
            val itemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            val firstKeyline = resources.getDimensionPixelSize(R.dimen.first_keyline)
            itemDecoration.setDrawable(InsetDrawable(itemDecoration.drawable?.mutate(), firstKeyline, 0, firstKeyline, 0))
            addItemDecoration(itemDecoration)
        }
        viewModel.motiveItems.observe(viewLifecycleOwner, motiveAdapter::submitList)
    }
}