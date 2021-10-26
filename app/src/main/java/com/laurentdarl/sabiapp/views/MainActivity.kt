package com.laurentdarl.sabiapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.laurentdarl.sabiapp.R
import com.laurentdarl.sabiapp.databinding.ActivityMainBinding
import com.laurentdarl.sabiapp.views.fragments.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val auth = FirebaseAuth.getInstance()

    companion object {
        const val USER_Id = "user_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment,
            R.id.loginFragment,
            R.id.registerFragment))

        val navHost = supportFragmentManager.findFragmentById(R.id.contained) as NavHostFragment
        val navController = navHost.navController
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navHost = supportFragmentManager.findFragmentById(R.id.contained) as NavHostFragment
        val navController = navHost.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}