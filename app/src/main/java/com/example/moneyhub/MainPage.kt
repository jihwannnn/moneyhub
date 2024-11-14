package com.example.moneyhub

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.moneyhub.databinding.ActivityMainPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainPage : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initializing ViewBinding
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Applying WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ensure the NavController is properly set up
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // OnDestinationChangedListener 추가
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.menu.apply {
                findItem(R.id.HomeFragment).setIcon(
                    if (destination.id == R.id.HomeFragment) R.drawable.icon_home_on else R.drawable.icon_home_off
                )
                findItem(R.id.VoteFragment).setIcon(
                    if (destination.id == R.id.VoteFragment) R.drawable.icon_vote_on else R.drawable.icon_vote_off
                )
                findItem(R.id.AnalysisFragment).setIcon(
                    if (destination.id == R.id.AnalysisFragment) R.drawable.icon_analysis_on else R.drawable.icon_analysis_off
                )
                findItem(R.id.MembersFragment).setIcon(
                    if (destination.id == R.id.MembersFragment) R.drawable.icon_members_on else R.drawable.icon_members_off
                )
            }
        }
    }
}
