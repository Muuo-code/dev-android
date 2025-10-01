package com.example.swiftride.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.swiftride.adapter.CarAdapter
import com.example.swiftride.data.model.CarEntity
import com.example.swiftride.databinding.FragmentCarListBinding
import com.example.swiftride.viewmodel.SharedViewModel

class CarListFragment : Fragment() {

    private var _binding: FragmentCarListBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var carAdapter: CarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter { car: CarEntity ->
            sharedViewModel.selectCar(car)
            Toast.makeText(requireContext(), "${car.brand} ${car.model} selected", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        binding.recyclerCars.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerCars.adapter = carAdapter
    }

    private fun observeViewModel() {
        sharedViewModel.allCars.observe(viewLifecycleOwner) { cars ->
            carAdapter.submitList(cars)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
