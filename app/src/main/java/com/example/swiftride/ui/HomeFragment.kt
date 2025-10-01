package com.example.swiftride.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swiftride.R
import com.example.swiftride.adapter.CarAdapter
import com.example.swiftride.databinding.FragmentHomeBinding
import com.example.swiftride.viewmodel.SharedViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var carAdapter: CarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter { carEntity ->
            sharedViewModel.selectCar(carEntity)
            Toast.makeText(context, "Selected: ${carEntity.brand} ${carEntity.model}", Toast.LENGTH_SHORT).show()

            if (sharedViewModel.currentUser.value != null) {
                findNavController().navigate(R.id.nav_bookings)
            } else {
                Toast.makeText(context, "Please login to book a car", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.loginFragment)
            }
        }
        binding.recyclerViewHomeCars.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewHomeCars.adapter = carAdapter
        binding.recyclerViewHomeCars.isNestedScrollingEnabled = false
    }

    private fun observeViewModel() {
        sharedViewModel.allCars.observe(viewLifecycleOwner) { cars ->
            carAdapter.submitList(cars)
        }

        sharedViewModel.currentUser.observe(viewLifecycleOwner) { user ->
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
