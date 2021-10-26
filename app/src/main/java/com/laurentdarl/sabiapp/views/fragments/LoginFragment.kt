package com.laurentdarl.sabiapp.views.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.laurentdarl.sabiapp.R
import com.laurentdarl.sabiapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    companion object {
        const val User_Id = "user_id"
        const val RC_SIGN_IN = 15
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (auth.currentUser != null && !auth.currentUser!!.isAnonymous) {
            homeActions()
        }

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging into your account!")
        progressDialog.setCanceledOnTouchOutside(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater)

        binding.tvSingInHint.setOnClickListener {
            registerActions()
        }

        binding.btnLogin.setOnClickListener {
            emailLogin()
        }

        binding.btnSkip.setOnClickListener {
            anonymousLogin()
        }

        binding.loginGoogle.setOnClickListener {
            googleLogin()
        }

        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            progressDialog.show()

            if (resultCode == Activity.RESULT_OK) {
                progressDialog.dismiss()
               homeActions()
            } else {
                progressDialog.dismiss()
                Log.e("LoginFragment", "Sign-in failed", response!!.error)
                Toast.makeText(requireContext(), response.error?.message.toString(),
                    Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun homeActions() {
        val actions = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        findNavController().navigate(actions)
    }

    private fun registerActions() {
        val actions = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        findNavController().navigate(actions)
    }

    private fun emailLogin() {
        when {
            TextUtils.isEmpty(binding.tifEmail.text.toString().trim {it <= ' '}) -> {
                binding.tfEmail.error = "Please enter a valid email"
            }

            TextUtils.isEmpty(binding.tifPassword.text.toString().trim {it <= ' '}) -> {
                binding.tfEmail.error = "Please enter a password"
            }

            else -> {
                val email = binding.tifEmail.text.toString().trim {it <= ' '}
                val password = binding.tifPassword.text.toString().trim {it <= ' '}

                progressDialog.show()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful) {
                            progressDialog.dismiss()
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            Toast.makeText(requireContext(), "Login successful!",
                                Toast.LENGTH_SHORT).show()
                            homeActions()
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(requireContext(), task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun anonymousLogin() {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    homeActions()
                    Toast.makeText(requireContext(), "You've logged in anonymously!",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun email2Login() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder()
                .build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                .build(), RC_SIGN_IN
        )
    }

    private fun googleLogin() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder()
                .build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                .build(), RC_SIGN_IN
        )
    }

    private fun phoneLogin() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.PhoneBuilder()
                .build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                .build(), RC_SIGN_IN
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}