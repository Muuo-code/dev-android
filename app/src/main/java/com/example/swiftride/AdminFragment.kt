package com.example.swiftride

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftride.com.example.swiftride.data.CarEntity
import com.example.swiftride.com.example.swiftride.data.BookingEntity

// Assuming CarAdapter is in this package. If CarAdapter.java is in root (src/main/java), use: import CarAdapter
// For now, let's assume it's been converted to Kotlin or is accessible via this path.
// If CarAdapter.java is in the default package, this import might need adjustment or the CarAdapter moved.
// import CarAdapter // Use this if CarAdapter.java is in src/main/java

class AdminFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var carAdapter: CarAdapter // For Car list
    private lateinit var bookingAdapter: BookingAdapter // For Booking list

    private lateinit var editTextBrand: EditText
    private lateinit var editTextModel: EditText
    private lateinit var editTextPricePerDay: EditText
    private lateinit var editTextSeats: EditText
    private lateinit var editTextTransmission: EditText
    private lateinit var editTextImageResId: EditText
    private lateinit var buttonAddCar: Button
    private lateinit var recyclerViewCars: RecyclerView
    private lateinit var recyclerViewBookings: RecyclerView // Added for bookings

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Initialize Car UI elements
        editTextBrand = view.findViewById(R.id.editTextAdminBrand)
        editTextModel = view.findViewById(R.id.editTextAdminModel)
        editTextPricePerDay = view.findViewById(R.id.editTextAdminPricePerDay)
        editTextSeats = view.findViewById(R.id.editTextAdminSeats)
        editTextTransmission = view.findViewById(R.id.editTextAdminTransmission)
        editTextImageResId = view.findViewById(R.id.editTextAdminImageResId)
        buttonAddCar = view.findViewById(R.id.buttonAdminAddCar)
        recyclerViewCars = view.findViewById(R.id.recyclerViewAdminCars)

        // Initialize Booking UI elements
        recyclerViewBookings = view.findViewById(R.id.recyclerViewAdminBookings)

        setupCarRecyclerView()
        setupBookingRecyclerView()

        observeViewModel()

        buttonAddCar.setOnClickListener {
            addCar()
        }
    }

    private fun setupCarRecyclerView() {
        // Assuming CarAdapter is a Kotlin class or accessible. 
        // If CarAdapter.java, its constructor and methods need to be callable from Kotlin.
        // The click listener for car items can be defined here if needed for admin actions (e.g., delete, update).
        carAdapter = CarAdapter { car -> 
            // Handle car item click: e.g., select car to populate fields for update, or show delete option
            Toast.makeText(context, "Selected car: ${car.brand} ${car.model}", Toast.LENGTH_SHORT).show()
            // For now, just a Toast. Implement update/delete logic later.
            // Example: populateCarFieldsForUpdate(car)
        }
        recyclerViewCars.layoutManager = LinearLayoutManager(context)
        recyclerViewCars.adapter = carAdapter
    }

    private fun setupBookingRecyclerView() {
        bookingAdapter = BookingAdapter { booking, newStatus ->
            sharedViewModel.updateBookingStatus(booking, newStatus)
            Toast.makeText(context, "Booking ${booking.bookingId} status updated to $newStatus", Toast.LENGTH_SHORT).show()
        }
        recyclerViewBookings.layoutManager = LinearLayoutManager(context)
        recyclerViewBookings.adapter = bookingAdapter
    }

    private fun observeViewModel() {
        sharedViewModel.allCars.observe(viewLifecycleOwner) {
            cars -> carAdapter.submitList(cars)
        }

        sharedViewModel.allBookings.observe(viewLifecycleOwner) {
            bookings -> bookingAdapter.submitList(bookings)
        }
    }

    private fun addCar() {
        val brand = editTextBrand.text.toString().trim()
        val model = editTextModel.text.toString().trim()
        val priceStr = editTextPricePerDay.text.toString().trim()
        val seatsStr = editTextSeats.text.toString().trim()
        val transmission = editTextTransmission.text.toString().trim()
        val imageResIdStr = editTextImageResId.text.toString().trim()

        if (brand.isEmpty() || model.isEmpty() || priceStr.isEmpty() || seatsStr.isEmpty() || transmission.isEmpty() || imageResIdStr.isEmpty()) {
            Toast.makeText(context, "Please fill all car fields", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull()
        val seats = seatsStr.toIntOrNull()
        // For imageResId, it's better to use R.drawable.name. For simplicity, we assume an integer for now.
        // In a real app, this should be a name resolved to an ID or a direct selection.
        val imageResId = imageResIdStr.toIntOrNull() 

        if (price == null || price <= 0) {
            Toast.makeText(context, "Valid price required", Toast.LENGTH_SHORT).show(); return; }
        if (seats == null || seats <= 0) {
            Toast.makeText(context, "Valid seats required", Toast.LENGTH_SHORT).show(); return; }
        if (imageResId == null) { 
            Toast.makeText(context, "Valid Image Resource ID required", Toast.LENGTH_SHORT).show(); return; }

        val newCar = CarEntity(
            brand = brand, model = model, pricePerDay = price,
            seats = seats, transmission = transmission, imageResId = imageResId
        )
        sharedViewModel.insertCar(newCar)
        Toast.makeText(context, "Car added: $brand $model", Toast.LENGTH_SHORT).show()
        clearInputFields()
    }

    private fun clearInputFields() {
        editTextBrand.text.clear()
        editTextModel.text.clear()
        editTextPricePerDay.text.clear()
        editTextSeats.text.clear()
        editTextTransmission.text.clear()
        editTextImageResId.text.clear()
    }
}
