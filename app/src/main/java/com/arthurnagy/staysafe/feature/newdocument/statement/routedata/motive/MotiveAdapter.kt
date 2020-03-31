package com.arthurnagy.staysafe.feature.newdocument.statement.routedata.motive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.arthurnagy.staysafe.ItemMotiveBinding
import com.arthurnagy.staysafe.feature.shared.DataBoundListAdapter
import com.arthurnagy.staysafe.feature.shared.DataBoundViewHolder

typealias OnMotiveSelected = (motive: MotivePickerViewModel.UiModel) -> Unit

class MotiveAdapter(private val onMotiveSelected: OnMotiveSelected) :
    DataBoundListAdapter<MotivePickerViewModel.UiModel, ItemMotiveBinding>(diffUtilItemCallback) {

    override fun createBinding(parent: ViewGroup): ItemMotiveBinding = ItemMotiveBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onBindingViewHolderCreated(holder: DataBoundViewHolder<ItemMotiveBinding>) {
        holder.binding.root.setOnClickListener {
            getItem(holder.adapterPosition)?.let(onMotiveSelected)
        }
    }

    override fun bind(binding: ItemMotiveBinding, item: MotivePickerViewModel.UiModel) {
        binding.uiModel = item
    }

    companion object {
        private val diffUtilItemCallback = object : DiffUtil.ItemCallback<MotivePickerViewModel.UiModel>() {
            override fun areItemsTheSame(oldItem: MotivePickerViewModel.UiModel, newItem: MotivePickerViewModel.UiModel): Boolean =
                oldItem.motive == newItem.motive

            override fun areContentsTheSame(oldItem: MotivePickerViewModel.UiModel, newItem: MotivePickerViewModel.UiModel): Boolean = oldItem == newItem
        }
    }
}