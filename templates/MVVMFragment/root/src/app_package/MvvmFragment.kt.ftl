package ${escapeKotlinIdentifiers(packageName)}

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
<#if applicationPackage??>
import ${applicationPackage}.${bindingName}
import ${applicationPackage}.R
</#if>
import org.koin.androidx.viewmodel.ext.android.viewModel

class ${className}Fragment : Fragment(R.layout.${layoutName}) {

    private val viewModel: ${viewModelName} by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ${bindingName}.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@${className}Fragment.viewModel
        }
    }
}