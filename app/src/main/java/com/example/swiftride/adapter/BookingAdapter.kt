package com.example.swiftride.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftride.data.model.BookingEntity
import com.example.swiftride.databinding.ItemBookingBinding

class BookingAdapter(
    private val onStatusUpdateClicked: (booking: BookingEntity, newStatus: String) -> Unit
) : ListAdapter<BookingEntity, BookingAdapter.BookingViewHolder>(BookingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBookingBinding.inflate(inflater, parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = getItem(position)
        holder.bind(booking, onStatusUpdateClicked)
    }

    class BookingViewHolder(private val binding: ItemBookingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(booking: BookingEntity, onStatusUpdateClicked: (booking: BookingEntity, newStatus: String) -> Unit) {
            binding.booking = booking

            binding.buttonBookingSetConfirmed.setOnClickListener {
                onStatusUpdateClicked(booking, "Confirmed")
            }
            binding.buttonBookingSetCancelled.setOnClickListener {
                onStatusUpdateClicked(booking, "Cancelled")
            }

            binding.buttonBookingSetConfirmed.isEnabled = booking.bookingStatus != "Confirmed"
            binding.buttonBookingSetCancelled.isEnabled = booking.bookingStatus != "Cancelled"

            binding.executePendingBindings()
        }
    }
}

class BookingDiffCallback : DiffUtil.ItemCallback<BookingEntity>() {
    override fun areItemsTheSame(oldItem: BookingEntity, newItem: BookingEntity): Boolean {
        return oldItem.bookingId == newItem.bookingId
    }

    override fun areContentsTheSame(oldItem: BookingEntity, newItem: BookingEntity): Boolean {
        return oldItem == newItem
    }
}
