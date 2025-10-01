package com.example.swiftride.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftride.data.model.CarEntity
import com.example.swiftride.databinding.ItemCarBinding

class CarAdapter(
    private val onCarClickListener: (CarEntity) -> Unit
) : ListAdapter<CarEntity, CarAdapter.CarViewHolder>(CarDiffCallback()) {

    inner class CarViewHolder(val binding: ItemCarBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(car: CarEntity) {
            binding.car = car
            binding.btnSelect.setOnClickListener {
                onCarClickListener(car)
            }
            binding.executePendingBindings() // Ensures the binding happens immediately
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCarBinding.inflate(inflater, parent, false)
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val carItem = getItem(position)
        holder.bind(carItem)
    }
}

class CarDiffCallback : DiffUtil.ItemCallback<CarEntity>() {
    override fun areItemsTheSame(oldItem: CarEntity, newItem: CarEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CarEntity, newItem: CarEntity): Boolean {
        return oldItem == newItem
    }
}
