package com.arthurnagy.staysafe.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.arthurnagy.staysafe.StatementBinding
import com.arthurnagy.staysafe.core.model.Statement
import com.arthurnagy.staysafe.feature.shared.DataBoundListAdapter
import com.arthurnagy.staysafe.feature.shared.DataBoundViewHolder

typealias OnStatementSelected = (statement: Statement) -> Unit
typealias OnStatementDateUpdate = (statemnent: Statement) -> Unit

class DocumentsAdapter(
    private val onStatementSelected: OnStatementSelected,
    private val onStatementDateUpdate: OnStatementDateUpdate
) : DataBoundListAdapter<StatementUiModel, StatementBinding>(diffUtilItemCallback) {
    override fun createBinding(parent: ViewGroup): StatementBinding = StatementBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onBindingViewHolderCreated(holder: DataBoundViewHolder<StatementBinding>) {
        with(holder.binding) {
            root.setOnClickListener {
                onStatementSelected(getItem(holder.adapterPosition).statement)
            }
            updateDate.setOnClickListener {
                onStatementDateUpdate(getItem(holder.adapterPosition).statement)
            }
        }
    }

    override fun bind(binding: StatementBinding, item: StatementUiModel) {
        binding.uiModel = item
    }

    companion object {
        private val diffUtilItemCallback = object : DiffUtil.ItemCallback<StatementUiModel>() {
            override fun areItemsTheSame(oldItem: StatementUiModel, newItem: StatementUiModel): Boolean = oldItem.statement.id == newItem.statement.id

            override fun areContentsTheSame(oldItem: StatementUiModel, newItem: StatementUiModel): Boolean = oldItem == newItem
        }
    }
}