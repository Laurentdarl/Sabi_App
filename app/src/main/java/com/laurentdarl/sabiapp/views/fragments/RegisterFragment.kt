package com.laurentdarl.sabiapp.views.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isNotEmpty
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.laurentdarl.sabiapp.R
import com.laurentdarl.sabiapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressDialog: ProgressDialog
    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("please wait")
        progressDialog.setMessage("Registration in progress!")
        progressDialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(layoutInflater)

        val genderArray = resources.getStringArray(R.array.gender)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_btn, genderArray)
        binding.tiGender.setAdapter(arrayAdapter)

        binding.tvBackToLogin.setOnClickListener {
            val actions = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(actions)
        }

        binding.btnSignUp.setOnClickListener {

            when {
                TextUtils.isEmpty(binding.tiEmail.text.toString().trim {it <= ' '}) -> {
                    binding.tfEmail.error = "Please enter a valid email"
                }

                TextUtils.isEmpty(binding.tiPassword.text.toString().trim {it <= ' '}) -> {
                    binding.tfEmail.error = "Please enter a password"
                }

                else -> {
                    val name = binding.tiName.text.toString()
                    val email = binding.tiEmail.text.toString().trim {it <= ' '}
                    val password = binding.tiPassword.text.toString().trim {it <= ' '}
                    val repeatPassword = binding.tiRepeatPassword.text.toString().trim {it <= ' '}



                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        && password == repeatPassword
                        && password.length <= 12
                        && !TextUtils.isEmpty(name)) {

                        binding.tfRepeatPassword.error = null
                        progressDialog.show()

                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    progressDialog.dismiss()
                                    val firebaseUser: FirebaseUser = task.result!!.user!!
                                    Toast.makeText(
                                        requireContext(),
                                        "Registration successful!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    homeActions()
                                } else {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else if (password != repeatPassword) {
                        binding.tfRepeatPassword.error = "Incorrect password"
                        Toast.makeText(
                            requireContext(),
                            "Please enter the same passwords",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (password.length > 12) {
                        binding.tfRepeatPassword.error = "Password is too long"
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Fill out all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }

        binding.btnSkip.setOnClickListener {

            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        loadListActivity()
                        Toast.makeText(requireContext(), "You've logged in anonymously!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }

        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun homeActions() {
        val actions = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
        findNavController().navigate(actions)
    }

    private fun loadListActivity() {
        val user = auth.currentUser
        homeActions()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}