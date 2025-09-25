package com.example.swiftride

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnBookWithUs = view.findViewById<MaterialButton>(R.id.btnBookWithUs)

        btnBookWithUs.setOnClickListener {
            // ✅ Use the SAME shared preferences name as RegisterActivity
            val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                // ✅ User is logged in → go to BookingFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, BookingFragment())
                    .addToBackStack(null)
                    .commit()
            } else {
                // ❌ User not logged in → redirect to LoginActivity
                Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
        }

        return view
    }
}
