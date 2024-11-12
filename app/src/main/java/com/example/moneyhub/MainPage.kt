package com.example.moneyhub

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ensure the NavController is properly set up
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setupWithNavController(navController)

        // OnDestinationChangedListener 추가
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.HomeFragment -> {
                    // HomeFragment에 도착했을 때 아이콘을 설정
                    navView.menu.findItem(R.id.HomeFragment).setIcon(R.drawable.icon_home_on)
                    navView.menu.findItem(R.id.VoteFragment).setIcon(R.drawable.icon_vote_off)
                    navView.menu.findItem(R.id.AnalysisFragment).setIcon(R.drawable.icon_analysis_off)
                    navView.menu.findItem(R.id.MembersFragment).setIcon(R.drawable.icon_members_off)
                }
                R.id.VoteFragment -> {
                    // VoteFragment에 도착했을 때 아이콘을 설정
                    navView.menu.findItem(R.id.HomeFragment).setIcon(R.drawable.icon_home_off)
                    navView.menu.findItem(R.id.VoteFragment).setIcon(R.drawable.icon_vote_on)
                    navView.menu.findItem(R.id.AnalysisFragment).setIcon(R.drawable.icon_analysis_off)
                    navView.menu.findItem(R.id.MembersFragment).setIcon(R.drawable.icon_members_off)
                }
                R.id.AnalysisFragment -> {
                    // AnalysisFragment에 도착했을 때 아이콘을 설정
                    navView.menu.findItem(R.id.HomeFragment).setIcon(R.drawable.icon_home_off)
                    navView.menu.findItem(R.id.VoteFragment).setIcon(R.drawable.icon_vote_off)
                    navView.menu.findItem(R.id.AnalysisFragment).setIcon(R.drawable.icon_analysis_on)
                    navView.menu.findItem(R.id.MembersFragment).setIcon(R.drawable.icon_members_off)
                }
                R.id.MembersFragment -> {
                    // MembersFragment에 도착했을 때 아이콘을 설정
                    navView.menu.findItem(R.id.HomeFragment).setIcon(R.drawable.icon_home_off)
                    navView.menu.findItem(R.id.VoteFragment).setIcon(R.drawable.icon_vote_off)
                    navView.menu.findItem(R.id.AnalysisFragment).setIcon(R.drawable.icon_analysis_off)
                    navView.menu.findItem(R.id.MembersFragment).setIcon(R.drawable.icon_members_on)
                }
            }
        }
    }
}
