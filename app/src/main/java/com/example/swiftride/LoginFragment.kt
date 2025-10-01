package com.example.swiftride

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText // Added for layout consistency
    private lateinit var buttonLogin: Button
    private lateinit var progressBarLogin: ProgressBar

    // Hardcoded admin email for local testing (replace with Firebase logic)
    private val ADMIN_EMAIL_LOCAL = "admin"
    private val ADMIN_USER_ID_LOCAL = "admin_user_id" // A distinct ID for local admin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        editTextEmail = view.findViewById(R.id.editTextLoginEmail)
        editTextPassword = view.findViewById(R.id.editTextLoginPassword)
        buttonLogin = view.findViewById(R.id.buttonLogin)
        progressBarLogin = view.findViewById(R.id.progressBarLogin)

        buttonLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim() // Password not used for local logic yet

        if (email.isEmpty()) {
            Toast.makeText(context, "Please enter an email or username", Toast.LENGTH_SHORT).show()
            return
        }

        progressBarLogin.visibility = View.VISIBLE
        buttonLogin.isEnabled = false

        // Simulate Firebase Auth - This will be replaced
        // For local testing, we use a predefined admin email/ID

        val isPotentiallyAdmin = email.equals(ADMIN_EMAIL_LOCAL, ignoreCase = true)
        val userIdToLogin = if (isPotentiallyAdmin) ADMIN_USER_ID_LOCAL else email // Use email as userID for non-admin local test
        val userDisplayName = if (isPotentiallyAdmin) "Admin User" else "Test User"

        // Register the user in our local DB and set admin flag if applicable
        sharedViewModel.loginOrRegisterUser(userIdToLogin, email, userDisplayName, isAdminFlag = isPotentiallyAdmin)

        // Check admin status from our local DB
        lifecycleScope.launch {
            try {
                val isAdmin = sharedViewModel.isAdminUser(userIdToLogin)
                progressBarLogin.visibility = View.GONE
                buttonLogin.isEnabled = true

                if (isAdmin) {
                    Toast.makeText(context, "Admin login successful", Toast.LENGTH_SHORT).show()
                    // TODO: Replace with actual navigation action ID if it exists
                    // findNavController().navigate(R.id.action_loginFragment_to_adminFragment)
                    // For now, assume AdminFragment is accessible or handle navigation error
                    try {
                        findNavController().navigate(R.id.adminFragment) // Or your specific action ID
                    } catch (e: Exception) {
                        Toast.makeText(context, "Navigation to Admin failed. Ensure nav graph is set up.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "User login successful", Toast.LENGTH_SHORT).show()
                    // TODO: Replace with actual navigation action ID to home/user area
                    // findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    try {
                        findNavController().navigate(R.id.homeFragment) // Or your specific action ID
                    } catch (e: Exception) {
                        Toast.makeText(context, "Navigation to Home failed. Ensure nav graph is set up.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                progressBarLogin.visibility = View.GONE
                buttonLogin.isEnabled = true
                Toast.makeText(context, "Login error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
