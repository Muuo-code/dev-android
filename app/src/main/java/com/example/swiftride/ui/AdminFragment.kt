package com.example.swiftride.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swiftride.adapter.BookingAdapter
import com.example.swiftride.adapter.CarAdapter
import com.example.swiftride.data.model.CarEntity
import com.example.swiftride.databinding.FragmentAdminBinding
import com.example.swiftride.viewmodel.SharedViewModel

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var carAdapter: CarAdapter
    private lateinit var bookingAdapter: BookingAdapter

    private var selectedCarForUpdate: CarEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        setupCarRecyclerView()
        setupBookingRecyclerView()
        observeViewModel()
        setupClickListeners()
        switchToAddMode()
    }

    private fun setupCarRecyclerView() {
        carAdapter = CarAdapter { carEntity ->
            selectedCarForUpdate = carEntity
            populateCarFieldsForUpdate(carEntity)
            switchToUpdateMode()
        }
        binding.recyclerViewAdminCars.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewAdminCars.adapter = carAdapter
    }

    private fun setupBookingRecyclerView() {
        bookingAdapter = BookingAdapter { booking, newStatus ->
            sharedViewModel.updateBookingStatus(booking.bookingId, newStatus)
            Toast.makeText(context, "Booking ${booking.bookingId} status updated to $newStatus", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewAdminBookings.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewAdminBookings.adapter = bookingAdapter
    }

    private fun observeViewModel() {
        sharedViewModel.allCars.observe(viewLifecycleOwner) { cars ->
            carAdapter.submitList(cars)
        }
        sharedViewModel.allBookings.observe(viewLifecycleOwner) { bookings ->
            bookingAdapter.submitList(bookings)
        }
    }

    private fun setupClickListeners() {
        binding.buttonAdminAddCar.setOnClickListener { processAddCar() }
        binding.buttonAdminUpdateCar.setOnClickListener { processUpdateCar() }
        binding.buttonAdminDeleteCar.setOnClickListener { confirmDeleteCar() }
        binding.buttonAdminClearForm.setOnClickListener {
            clearInputFields()
            selectedCarForUpdate = null
            switchToAddMode()
        }
    }

    private fun populateCarFieldsForUpdate(car: CarEntity) {
        binding.editTextAdminBrand.setText(car.brand)
        binding.editTextAdminModel.setText(car.model)
        binding.editTextAdminPricePerDay.setText(car.pricePerDay.toString())
        binding.editTextAdminSeats.setText(car.seats.toString())
        binding.editTextAdminTransmission.setText(car.transmission)
        binding.editTextAdminImageResId.setText(getDrawableNameById(car.imageResId))
    }

    private fun switchToAddMode() {
        binding.buttonAdminAddCar.visibility = View.VISIBLE
        binding.buttonAdminUpdateCar.visibility = View.GONE
        binding.buttonAdminDeleteCar.visibility = View.GONE
        binding.buttonAdminClearForm.visibility = View.GONE
        binding.editTextAdminBrand.requestFocus()
    }

    private fun switchToUpdateMode() {
        binding.buttonAdminAddCar.visibility = View.GONE
        binding.buttonAdminUpdateCar.visibility = View.VISIBLE
        binding.buttonAdminDeleteCar.visibility = View.VISIBLE
        binding.buttonAdminClearForm.visibility = View.VISIBLE
    }

    private fun getDrawableIdByName(imageName: String): Int {
        return requireContext().resources.getIdentifier(imageName.lowercase(), "drawable", requireContext().packageName)
    }

    private fun getDrawableNameById(resId: Int): String {
        return try {
            requireContext().resources.getResourceEntryName(resId)
        } catch (e: Exception) {
            ""
        }
    }

    private fun validateCarInputs(): CarEntity? {
        val brand = binding.editTextAdminBrand.text.toString().trim()
        val model = binding.editTextAdminModel.text.toString().trim()
        val priceStr = binding.editTextAdminPricePerDay.text.toString().trim()
        val seatsStr = binding.editTextAdminSeats.text.toString().trim()
        val transmission = binding.editTextAdminTransmission.text.toString().trim()
        val imageName = binding.editTextAdminImageResId.text.toString().trim()

        if (brand.isEmpty() || model.isEmpty() || priceStr.isEmpty() || seatsStr.isEmpty() || transmission.isEmpty() || imageName.isEmpty()) {
            Toast.makeText(context, "Please fill all car fields", Toast.LENGTH_SHORT).show()
            return null
        }

        val price = priceStr.toDoubleOrNull()
        val seats = seatsStr.toIntOrNull()
        val imageResId = getDrawableIdByName(imageName)

        if (price == null || price <= 0) {
            Toast.makeText(context, "Valid price required", Toast.LENGTH_SHORT).show(); return null
        }
        if (seats == null || seats <= 0) {
            Toast.makeText(context, "Valid seats required", Toast.LENGTH_SHORT).show(); return null
        }
        if (imageResId == 0) {
            Toast.makeText(context, "Image '$imageName' not found in drawables. Please use a valid image name (without extension).", Toast.LENGTH_LONG).show(); return null
        }

        val carId = selectedCarForUpdate?.id ?: 0

        return CarEntity(
            id = carId,
            brand = brand, model = model, pricePerDay = price,
            seats = seats, transmission = transmission, imageResId = imageResId
        )
    }

    private fun processAddCar() {
        validateCarInputs()?.let {
            sharedViewModel.insertCar(it.copy(id = 0))
            Toast.makeText(context, "Car added: ${it.brand} ${it.model}", Toast.LENGTH_SHORT).show()
            clearInputFields()
        }
    }

    private fun processUpdateCar() {
        if (selectedCarForUpdate == null) {
            Toast.makeText(context, "No car selected for update.", Toast.LENGTH_SHORT).show()
            return
        }
        validateCarInputs()?.let {
            sharedViewModel.updateCar(it)
            Toast.makeText(context, "Car updated: ${it.brand} ${it.model}", Toast.LENGTH_SHORT).show()
            clearInputFields()
            selectedCarForUpdate = null
            switchToAddMode()
        }
    }

    private fun confirmDeleteCar() {
        val carToDelete = selectedCarForUpdate ?: return
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Car")
            .setMessage("Are you sure you want to delete ${carToDelete.brand} ${carToDelete.model}?")
            .setPositiveButton("Delete") { _, _ ->
                sharedViewModel.deleteCar(carToDelete)
                Toast.makeText(context, "${carToDelete.brand} ${carToDelete.model} deleted", Toast.LENGTH_SHORT).show()
                clearInputFields()
                selectedCarForUpdate = null
                switchToAddMode()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun clearInputFields() {
        binding.editTextAdminBrand.text.clear()
        binding.editTextAdminModel.text.clear()
        binding.editTextAdminPricePerDay.text.clear()
        binding.editTextAdminSeats.text.clear()
        binding.editTextAdminTransmission.text.clear()
        binding.editTextAdminImageResId.text.clear()
        binding.editTextAdminBrand.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
