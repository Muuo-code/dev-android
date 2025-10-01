package com.example.swiftride

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CarListFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var carAdapter: CarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_car_list, container, false)

        // Initialize SharedViewModel
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.recyclerCars)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Setup Adapter with empty list initially
        carAdapter = CarAdapter(emptyList()) { car ->
            sharedViewModel.selectCar(car)
            Toast.makeText(requireContext(), "${car.brand} ${car.model} selected", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        recyclerView.adapter = carAdapter

        // Observe DB changes
        sharedViewModel.allCars.observe(viewLifecycleOwner) { cars ->
            carAdapter.updateCars(cars)
        }

        return view
    }
}

