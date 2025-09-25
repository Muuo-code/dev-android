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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_car_list, container, false)

        // Initialize SharedViewModel
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.recyclerCars)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns

        // Car list with local drawables
        val carList = listOf(
            Car("Toyota Corolla", 50, 5, "Automatic", R.drawable.axela),
            Car("Nissan X-Trail", 80, 7, "Manual", R.drawable.bmw_x5),
            Car("Honda Civic", 60, 5, "Automatic", R.drawable.civic),
            Car("BMW X5", 150, 7, "Automatic", R.drawable.trailblazer),
            Car("Tesla Model S", 200, 5, "Automatic", R.drawable.gle)
        )

        // Set Adapter
        recyclerView.adapter = CarAdapter(carList) { car ->
            sharedViewModel.selectCar(car)
            Toast.makeText(requireContext(), "${car.name} selected", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack() // return to previous fragment
        }

        return view
    }
}
