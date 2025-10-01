package com.example.swiftride

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftride.com.example.swiftride.data.CarEntity

class CarAdapter(
    private var cars: List<Car>,
    private val listener: (ERROR) -> ERROR
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    interface OnCarActionListener {
        fun onSelect(car: CarEntity)
    }

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
        holder.tvCarName.text = "${car.brand} ${car.model}"
        holder.tvCarPrice.text = "$${car.pricePerDay}/day"
        holder.tvCarSeats.text = "${car.seats} seats"
        holder.tvCarType.text = car.transmission

        // Set image from drawable
        holder.ivCarImage.setImageResource(car.imageResId)  // <-- local drawable

        // Button click
        holder.btnSelect.setOnClickListener { listener.onSelect(car) }
    }

    override fun getItemCount() = cars.size

    fun updateCars(newCars: List<CarEntity>) {
        val diffCallback = CarDiffCallback(cars, newCars)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        cars = newCars
        diffResult.dispatchUpdatesTo(this)
    }

}

class CarDiffCallback(
    private val oldList: List<CarEntity>,
    private val newList: List<CarEntity>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare unique IDs
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare all fields
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

