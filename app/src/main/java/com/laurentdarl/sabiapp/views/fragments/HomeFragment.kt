package com.laurentdarl.sabiapp.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.laurentdarl.sabiapp.R
import com.laurentdarl.sabiapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val authUI = AuthUI.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        binding.btnLogout.setOnClickListener {
            authUI.signOut(requireContext())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val actions = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                        findNavController().navigate(actions)
                        Toast.makeText(requireContext(), "logged out successfully",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), task.exception?.message,
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.btnDelete.setOnClickListener {
            deleteAccount()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun deleteAccount() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete account?")
            .setMessage("This action is permanent, are you sure?")
            .setPositiveButton("Yes") {_,_ ->
                authUI.delete(requireContext())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()
                            val actions = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                            findNavController().navigate(actions)
                        } else {
                            Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}