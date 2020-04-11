package com.arthurnagy.staysafe.feature.newdocument.statement.routedata

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.arthurnagy.staysafe.ItemMotiveBinding
import com.arthurnagy.staysafe.feature.newdocument.statement.routedata.StatementRouteDataViewModel.MotiveUiModel
import com.arthurnagy.staysafe.feature.shared.DataBoundListAdapter
import com.arthurnagy.staysafe.feature.shared.DataBoundViewHolder

typealias OnMotiveSelected = (motive: MotiveUiModel) -> Unit

class MotiveAdapter(private val onMotiveSelected: OnMotiveSelected) :
    DataBoundListAdapter<MotiveUiModel, ItemMotiveBinding>(diffUtilItemCallback) {

    override fun createBinding(parent: ViewGroup): ItemMotiveBinding = ItemMotiveBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onBindingViewHolderCreated(holder: DataBoundViewHolder<ItemMotiveBinding>) {
        holder.binding.root.setOnClickListener {
            getItem(holder.adapterPosition)?.let(onMotiveSelected)
        }
    }

    override fun bind(binding: ItemMotiveBinding, item: MotiveUiModel) {
        binding.uiModel = item
    }

    companion object {
        private val diffUtilItemCallback = object : DiffUtil.ItemCallback<MotiveUiModel>() {
            override fun areItemsTheSame(oldItem: MotiveUiModel, newItem: MotiveUiModel): Boolean =
                oldItem.motive == newItem.motive

            override fun areContentsTheSame(oldItem: MotiveUiModel, newItem: MotiveUiModel): Boolean = oldItem == newItem
        }
    }
}