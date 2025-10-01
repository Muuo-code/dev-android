package com.example.swiftride.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.swiftride.R
import com.example.swiftride.databinding.FragmentBookingBinding
import com.example.swiftride.viewmodel.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class BookingFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        observeViewModel()
        setupDatePickers()
        setupTextWatchers()

        binding.buttonConfirmBooking.setOnClickListener {
            if (sharedViewModel.currentUser.value == null) {
                Toast.makeText(context, "Please log in to make a booking.", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.loginFragment)
                return@setOnClickListener
            }
            if (sharedViewModel.selectedCar.value == null) {
                Toast.makeText(context, "Please select a car first.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (binding.editTextPickupDate.text.isNullOrEmpty() || binding.editTextReturnDate.text.isNullOrEmpty()){
                Toast.makeText(context, "Please select pickup and return dates.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            sharedViewModel.createBooking()
            Toast.makeText(context, "Booking Confirmed!", Toast.LENGTH_LONG).show()
            findNavController().popBackStack(R.id.nav_home, false)
        }
    }

    private fun observeViewModel() {
        sharedViewModel.selectedCar.observe(viewLifecycleOwner) { car ->
            if (car != null) {
                binding.textViewSelectedCarBrandModel.text = "${car.brand} - ${car.model}"
                binding.textViewSelectedCarPrice.text = "Price per day: $${String.format("%.2f", car.pricePerDay)}"
                updateTotalPrice()
            } else {
                binding.textViewSelectedCarBrandModel.text = "No car selected"
                binding.textViewSelectedCarPrice.text = ""
                binding.textViewTotalPrice.text = "$0.00"
                binding.buttonConfirmBooking.isEnabled = false
            }
        }

        sharedViewModel.pickupDate.observe(viewLifecycleOwner) { date ->
            if (binding.editTextPickupDate.text.toString() != date) {
                binding.editTextPickupDate.setText(date)
            }
            updateTotalPrice()
        }

        sharedViewModel.returnDate.observe(viewLifecycleOwner) { date ->
             if (binding.editTextReturnDate.text.toString() != date) {
                binding.editTextReturnDate.setText(date)
            }
            updateTotalPrice()
        }
    }

    private fun setupDatePickers() {
        binding.buttonShowDatePickerPickup.setOnClickListener {
            showDatePickerDialog(true)
        }
        binding.buttonShowDatePickerReturn.setOnClickListener {
            showDatePickerDialog(false)
        }
    }

    private fun setupTextWatchers() {
        binding.editTextPickupDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != sharedViewModel.pickupDate.value) {
                    sharedViewModel.setPickupDate(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.editTextReturnDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != sharedViewModel.returnDate.value) {
                    sharedViewModel.setReturnDate(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showDatePickerDialog(isPickupDate: Boolean) {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val selectedDate = dateFormat.format(calendar.time)
            if (isPickupDate) {
                sharedViewModel.setPickupDate(selectedDate)
            } else {
                sharedViewModel.setReturnDate(selectedDate)
            }
        }

        val currentDateStr = if (isPickupDate) sharedViewModel.pickupDate.value else sharedViewModel.returnDate.value
        val initialCalendar = Calendar.getInstance()
        try {
            if (!currentDateStr.isNullOrEmpty()) {
                dateFormat.parse(currentDateStr)?.let { initialCalendar.time = it }
            }
        } catch (e: Exception) { }

        val dialog = DatePickerDialog(
            requireContext(),
            dateSetListener,
            initialCalendar.get(Calendar.YEAR),
            initialCalendar.get(Calendar.MONTH),
            initialCalendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun updateTotalPrice() {
        val car = sharedViewModel.selectedCar.value
        val pickupDateStr = sharedViewModel.pickupDate.value
        val returnDateStr = sharedViewModel.returnDate.value

        if (car != null && !pickupDateStr.isNullOrEmpty() && !returnDateStr.isNullOrEmpty()) {
            try {
                val pickupDateParsed = dateFormat.parse(pickupDateStr)
                val returnDateParsed = dateFormat.parse(returnDateStr)

                if (pickupDateParsed != null && returnDateParsed != null && returnDateParsed.after(pickupDateParsed)) {
                    val diffInMillis = returnDateParsed.time - pickupDateParsed.time
                    val days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
                    if (days >= 0) {
                        val displayDays = if (days == 0L) 1 else days
                        val totalPrice = car.pricePerDay * displayDays
                        binding.textViewTotalPrice.text = "$${String.format("%.2f", totalPrice)}"
                        binding.buttonConfirmBooking.isEnabled = true
                        return
                    }
                }
            } catch (e: Exception) {
                 binding.textViewTotalPrice.text = "Invalid dates"
                 binding.buttonConfirmBooking.isEnabled = false
                return
            }
        }
        binding.textViewTotalPrice.text = "$0.00" 
        binding.buttonConfirmBooking.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
