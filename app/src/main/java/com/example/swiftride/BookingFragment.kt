package com.example.swiftride

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*

class BookingFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var tvPickupDate: TextView
    private lateinit var tvReturnDate: TextView
    private lateinit var tvSelectedVehicle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val spinnerPickup: Spinner = view.findViewById(R.id.spinnerPickup)
        val spinnerReturn: Spinner = view.findViewById(R.id.spinnerReturn)
        val btnSelectVehicle: Button = view.findViewById(R.id.btnSelectVehicle)
        val btnConfirm: Button = view.findViewById(R.id.btnConfirm)

        tvPickupDate = view.findViewById(R.id.tvPickupDate)
        tvReturnDate = view.findViewById(R.id.tvReturnDate)
        tvSelectedVehicle = view.findViewById(R.id.tvSelectedVehicle)

        // Restore dates from ViewModel if already picked
        sharedViewModel.pickupDate.observe(viewLifecycleOwner) { date ->
            date?.let { tvPickupDate.text = it }
        }
        sharedViewModel.returnDate.observe(viewLifecycleOwner) { date ->
            date?.let { tvReturnDate.text = it }
        }

        // Observe selected car
        sharedViewModel.selectedCar.observe(viewLifecycleOwner) { car ->
            car?.let { tvSelectedVehicle.text = it.name }
        }

        // Spinners setup
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.cities_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPickup.adapter = adapter
            spinnerReturn.adapter = adapter
        }

        // Date Pickers
        tvPickupDate.setOnClickListener {
            showDatePicker { date ->
                tvPickupDate.text = date
                sharedViewModel.setPickupDate(date) // save in ViewModel
            }
        }
        tvReturnDate.setOnClickListener {
            showDatePicker { date ->
                tvReturnDate.text = date
                sharedViewModel.setReturnDate(date) // save in ViewModel
            }
        }

        // Select Vehicle → CarListFragment
        btnSelectVehicle.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CarListFragment())
                .addToBackStack(null)
                .commit()
        }


        // Confirm Booking
        btnConfirm.setOnClickListener {
            val sharedPref = requireContext().getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

            if (!isLoggedIn) {
                Toast.makeText(requireContext(), "Please login or register to continue", Toast.LENGTH_SHORT).show()

                // 🚀 Redirect to LoginActivity
                val intent = android.content.Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                return@setOnClickListener
            }

            // ✅ If logged in, validate inputs
            if (validateInputs(spinnerPickup, spinnerReturn)) {
                Toast.makeText(requireContext(), "Booking successful!", Toast.LENGTH_SHORT).show()

                // 🚀 Redirect to DashboardFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DashboardFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }



        return view
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val date = "$dayOfMonth/${month + 1}/$year"
                onDateSelected(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // 🚀 Block past dates
        dialog.datePicker.minDate = calendar.timeInMillis

        dialog.show()
    }

    private fun validateInputs(spinnerPickup: Spinner, spinnerReturn: Spinner): Boolean {
        val pickup = tvPickupDate.text.toString()
        val returnD = tvReturnDate.text.toString()

        return when {
            pickup.isEmpty() -> {
                Toast.makeText(requireContext(), "Select pickup date", Toast.LENGTH_SHORT).show()
                false
            }
            returnD.isEmpty() -> {
                Toast.makeText(requireContext(), "Select return date", Toast.LENGTH_SHORT).show()
                false
            }
            !isReturnAfterPickup(pickup, returnD) -> {
                Toast.makeText(requireContext(), "Return date must be after pickup date", Toast.LENGTH_SHORT).show()
                false
            }
            tvSelectedVehicle.text.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Select a vehicle", Toast.LENGTH_SHORT).show()
                false
            }
            spinnerPickup.selectedItem == null -> {
                Toast.makeText(requireContext(), "Select a pickup city", Toast.LENGTH_SHORT).show()
                false
            }
            spinnerReturn.selectedItem == null -> {
                Toast.makeText(requireContext(), "Select a return city", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun isReturnAfterPickup(pickup: String, returnD: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
            val pickupDate = sdf.parse(pickup)
            val returnDate = sdf.parse(returnD)
            returnDate != null && pickupDate != null && returnDate.after(pickupDate)
        } catch (e: Exception) {
            false
        }
    }
}
