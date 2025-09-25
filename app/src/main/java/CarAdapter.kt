package com.example.swiftride

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CarAdapter(
    private val cars: List<Car>,
    private val onSelect: (Car) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCarImage: ImageView = itemView.findViewById(R.id.ivCarImage)
        val tvCarName: TextView = itemView.findViewById(R.id.tvCarName)
        val tvCarPrice: TextView = itemView.findViewById(R.id.tvCarPrice)
        val tvCarSeats: TextView = itemView.findViewById(R.id.tvCarSeats)
        val tvCarType: TextView = itemView.findViewById(R.id.tvCarType)
        val btnSelect: Button = itemView.findViewById(R.id.btnSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = cars[position]

        // Set text
        holder.tvCarName.text = car.name
        holder.tvCarPrice.text = "$${car.price}/day"
        holder.tvCarSeats.text = "${car.seats} seats"
        holder.tvCarType.text = car.type

        // Set image from drawable
        holder.ivCarImage.setImageResource(car.imageResId)  // <-- local drawable

        // Button click
        holder.btnSelect.setOnClickListener { onSelect(car) }
    }

    override fun getItemCount() = cars.size
}
