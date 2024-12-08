package com.example.moneyhub.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.moneyhub.viewmodel.MainViewModel
import com.example.moneyhub.R
import com.example.moneyhub.activity.mypage.MyPageActivity
import com.example.moneyhub.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initializing ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // custom_header_include에만 WindowInsets 적용
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.custom_header_include)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Ensure the NavController is properly set up
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            mainViewModel.updateCurrentDestination(destination.id)
        }

        mainViewModel.currentDestination.observe(this) {destinationId ->
            binding.bottomNavigation.menu.apply {
                findItem(R.id.HomeFragment).setIcon(
                    if (destinationId == R.id.HomeFragment) R.drawable.icon_home_on else R.drawable.icon_home_off
                )
                findItem(R.id.BoardFragment).setIcon(
                    if (destinationId == R.id.BoardFragment) R.drawable.icon_board_on else R.drawable.icon_board_off
                )
                findItem(R.id.AnalysisFragment).setIcon(
                    if (destinationId == R.id.AnalysisFragment) R.drawable.icon_analysis_on else R.drawable.icon_analysis_off
                )
                findItem(R.id.MembersFragment).setIcon(
                    if (destinationId == R.id.MembersFragment) R.drawable.icon_members_on else R.drawable.icon_members_off
                )
            }
        }

        // clickEvent on mypage button at the header
        binding.customHeaderInclude.imageViewMyPage.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }
}
