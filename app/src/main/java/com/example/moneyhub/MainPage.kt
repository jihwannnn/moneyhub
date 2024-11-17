package com.example.moneyhub

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        //MP - 09. Android Permission
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        Log.d("ITM", "Connected: ${networkInfo?.isConnected}")

        val perm= ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION")
        if(perm== PERMISSION_GRANTED) {
            val locationManager= getSystemService(LOCATION_SERVICE) as LocationManager
                    Log.d("ITM","${locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)}")
        }
        else{
            val permRationale= shouldShowRequestPermissionRationale("android.permission.ACCESS_FINE_LOCATION")
            Log.d("ITM","$permRationale")
        }




        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
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
