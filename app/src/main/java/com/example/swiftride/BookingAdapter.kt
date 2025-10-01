package com.example.swiftride

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftride.com.example.swiftride.data.BookingEntity

class BookingAdapter(
    private val onStatusUpdateClicked: (booking: BookingEntity, newStatus: String) -> Unit
) : ListAdapter<BookingEntity, BookingAdapter.BookingViewHolder>(BookingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = getItem(position)
        holder.bind(booking, onStatusUpdateClicked)
    }

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewBookingId: TextView = itemView.findViewById(R.id.textViewBookingId)
        private val textViewBookingUserId: TextView = itemView.findViewById(R.id.textViewBookingUserId)
        private val textViewBookingCarId: TextView = itemView.findViewById(R.id.textViewBookingCarId)
        private val textViewBookingDates: TextView = itemView.findViewById(R.id.textViewBookingDates)
        private val textViewBookingPrice: TextView = itemView.findViewById(R.id.textViewBookingPrice)
        private val textViewBookingStatus: TextView = itemView.findViewById(R.id.textViewBookingStatus)
        private val buttonSetConfirmed: Button = itemView.findViewById(R.id.buttonBookingSetConfirmed)
        private val buttonSetCancelled: Button = itemView.findViewById(R.id.buttonBookingSetCancelled)

        fun bind(booking: BookingEntity, onStatusUpdateClicked: (booking: BookingEntity, newStatus: String) -> Unit) {
            textViewBookingId.text = "Booking ID: ${booking.bookingId}"
            textViewBookingUserId.text = "User ID: ${booking.bookingUserId}" // Consider fetching user email/name
            textViewBookingCarId.text = "Car ID: ${booking.bookingCarId}" // Consider fetching car brand/model
            textViewBookingDates.text = "Dates: ${booking.pickupDate} to ${booking.returnDate}"
            textViewBookingPrice.text = "Total Price: $${String.format("%.2f", booking.totalPrice)}"
            textViewBookingStatus.text = "Status: ${booking.bookingStatus}"

            buttonSetConfirmed.setOnClickListener {
                onStatusUpdateClicked(booking, "Confirmed")
            }
            buttonSetCancelled.setOnClickListener {
                onStatusUpdateClicked(booking, "Cancelled")
            }
            
            // Optionally, disable buttons based on current status
            buttonSetConfirmed.isEnabled = booking.bookingStatus != "Confirmed"
            buttonSetCancelled.isEnabled = booking.bookingStatus != "Cancelled"
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