package com.example.swiftride.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.swiftride.R
import com.example.swiftride.databinding.FragmentLoginBinding
import com.example.swiftride.viewmodel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonLogin.setOnClickListener {
            handleLoginAttempt()
        }
    }

    private fun handleLoginAttempt() {
        val email = binding.editTextLoginEmail.text.toString().trim()
        val password = binding.editTextLoginPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.editTextLoginEmail.error = "Email is required"
            binding.editTextLoginEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.editTextLoginPassword.error = "Password is required"
            binding.editTextLoginPassword.requestFocus()
            return
        }

        binding.progressBarLogin.visibility = View.VISIBLE
        binding.buttonLogin.isEnabled = false

        lifecycleScope.launch {
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                processSuccessfulAuth(authResult.user!!)
            } catch (e: Exception) {
                when (e) {
                    is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> {
                        Log.d("LoginFragment", "Sign-in failed, attempting to create account: ${e.message}")
                        try {
                            val createUserResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                            processSuccessfulAuth(createUserResult.user!!, isNewUser = true)
                        } catch (regException: Exception) {
                            showLoginError("Registration failed: ${regException.message}")
                        }
                    }
                    is FirebaseAuthUserCollisionException -> {
                        showLoginError("Email already in use. Please login or use a different email.")
                    }
                    else -> {
                        showLoginError("Authentication error: ${e.message}")
                    }
                }
            }
        }
    }

    private suspend fun processSuccessfulAuth(firebaseUser: FirebaseUser, isNewUser: Boolean = false) {
        val userId = firebaseUser.uid
        val email = firebaseUser.email ?: ""
        val displayName = firebaseUser.displayName

        sharedViewModel.loginOrRegisterUser(userId, email, displayName, isAdminFlag = false)

        try {
            val isAdmin = sharedViewModel.isAdminUser(userId)
            binding.progressBarLogin.visibility = View.GONE
            binding.buttonLogin.isEnabled = true

            if (isAdmin) {
                Toast.makeText(context, "Admin login successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_adminFragment)
            } else {
                Toast.makeText(context, if(isNewUser) "Registration successful" else "Login successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        } catch (navException: Exception) {
            showLoginError("Navigation error: ${navException.message}. Check NavGraph and action IDs.")
        }
    }

    private fun showLoginError(message: String) {
        Log.e("LoginFragment", message)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        binding.progressBarLogin.visibility = View.GONE
        binding.buttonLogin.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
