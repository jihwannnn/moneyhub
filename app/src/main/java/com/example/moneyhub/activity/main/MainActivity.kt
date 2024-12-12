package com.example.moneyhub.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

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
    private val shakeThreshold = 5000

    // 모달이 표시중인지 체크하는 플래그 추가
    private var isDialogShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSensor()
        setupNavigation()
        observeViewModel()
    }

    private fun setupSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun setupNavigation() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.custom_header_include)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            mainViewModel.updateCurrentDestination(destination.id)
        }

        binding.customHeaderInclude.imageViewMyPage.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            mainViewModel.uiState.collect { state ->
                when {
                    state.isSuccess -> {
                        Toast.makeText(this@MainActivity, "그룹이 성공적으로 삭제되었습니다", Toast.LENGTH_SHORT)
                            .show()
                        // 그룹이 삭제되었으므로 MyPage로 이동
                        startActivity(Intent(this@MainActivity, MyPageActivity::class.java))
                        finish()
                    }

                    state.error != null -> {
                        Toast.makeText(this@MainActivity, state.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        mainViewModel.currentDestination.observe(this) { destinationId ->
            updateNavigationIcons(destinationId)
        }
    }

    private fun updateNavigationIcons(destinationId: Int) {
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
            if ((currentTime - lastUpdate) > 100) {
                val diffTime = currentTime - lastUpdate
                lastUpdate = currentTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val speed = abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                // 모달이 표시중이 아닐 때만 새로운 모달을 표시
                if (speed > shakeThreshold && !isDialogShowing) {
                    showDeleteConfirmationDialog()
                }

                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        isDialogShowing = true // 모달 표시 시작
        AlertDialog.Builder(this)
            .setTitle("그룹 삭제")
            .setMessage("정말로 현재 그룹을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { dialog, _ ->
                mainViewModel.deleteCurrentGroup()
                dialog.dismiss()
                isDialogShowing = false // 모달 종료
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
                isDialogShowing = false // 모달 종료
            }
            .setOnCancelListener {
                isDialogShowing = false // 모달이 다른 방식으로 종료될 때도 플래그 초기화
            }
            .show()
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 센서 정확도 변경 시 처리
    }
}