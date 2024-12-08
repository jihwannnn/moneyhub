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
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var lastUpdate: Long = 0
    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f
    private var lastZ: Float = 0.0f
    private val shakeThreshold = 1500 // 흔들기 감지 임계값



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initializing ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //센서 매니저 초기화
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


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
    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            // 100ms마다 체크 (너무 자주 체크하지 않도록)
            if ((currentTime - lastUpdate) > 100) {
                val diffTime = currentTime - lastUpdate
                lastUpdate = currentTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val speed = abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                if (speed > shakeThreshold) {
                    // 흔들기 감지됨 - MyPage로 이동
                    startActivity(Intent(this, MyPageActivity::class.java))
                }

                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 센서의 정확도가 변경될 때 호출되는 메서드
    }
}

