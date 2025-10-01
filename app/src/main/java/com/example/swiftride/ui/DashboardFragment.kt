package com.example.swiftride.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swiftride.R
import com.example.swiftride.adapter.BookingAdapter
import com.example.swiftride.databinding.FragmentDashboardBinding
import com.example.swiftride.ui.auth.RegisterActivity
import com.example.swiftride.viewmodel.SharedViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter { booking, newStatus ->
            if (newStatus == "Cancelled" && booking.bookingStatus == "Pending") {
                sharedViewModel.updateBookingStatus(booking.bookingId, newStatus)
                Toast.makeText(context, "Booking ${booking.bookingId} cancelled.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Action not available for this booking's status.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.recyclerViewMyBookings.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewMyBookings.adapter = bookingAdapter
    }

    private fun observeViewModel() {
        sharedViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                sharedViewModel.getBookingsForUser(user.userId).observe(viewLifecycleOwner, Observer { bookings ->
                    if (bookings.isNullOrEmpty()) {
                        binding.textViewNoBookings.text = "You have no bookings yet."
                        binding.textViewNoBookings.visibility = View.VISIBLE
                        binding.recyclerViewMyBookings.visibility = View.GONE
                    } else {
                        binding.textViewNoBookings.visibility = View.GONE
                        binding.recyclerViewMyBookings.visibility = View.VISIBLE
                        bookingAdapter.submitList(bookings)
                    }
                })
            } else {
                binding.textViewNoBookings.text = "Please log in to see your bookings."
                binding.textViewNoBookings.visibility = View.VISIBLE
                binding.recyclerViewMyBookings.visibility = View.GONE
                bookingAdapter.submitList(emptyList())
                findNavController().navigate(R.id.loginFragment)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)
        val loginText = findViewById<TextView>(R.id.loginText)
        val registerText = findViewById<TextView>(R.id.registerText)

        btnGetStarted.setOnClickListener {
            Toast.makeText(this, "Get Started clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        loginText.setOnClickListener {
            Toast.makeText(this, "Login clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        registerText.setOnClickListener {
            Toast.makeText(this, "Register clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}