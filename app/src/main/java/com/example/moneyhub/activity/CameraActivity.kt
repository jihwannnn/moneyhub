package com.example.moneyhub.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.moneyhub.R
import com.example.moneyhub.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    // 거래 내역 데이터를 저장할 변수들
    private var date: String? = null
    private var title: String? = null
    private var category: String? = null
    private var amount: Long? = null


    // 갤러리 실행 결과 처리
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.ivReceipt.setImageURI(it)
        }
    }

    // API 레벨에 따라 다른 권한 사용
    private val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로부터 데이터 받기
        getTransactionData()

        setupButtons()
        setupImageContainer()
    }

    // view model에 옮겨야 할 것
    private fun getTransactionData() {
        // 데이터 받기
        date = intent.getStringExtra("transaction_date")
        title = intent.getStringExtra("transaction_title")
        category = intent.getStringExtra("transaction_category")
        amount = intent.getLongExtra("transaction_amount", 0L)

        // 텍스트뷰 표시
        binding.tvTransactionInfo.text = """
       거래 ID: ${intent.getLongExtra("transaction_id", -1)}
       날짜: $date 
       제목: $title
       카테고리: $category
       금액: $amount
   """.trimIndent()
    }
    private fun setupButtons() {
        // Open Camera 버튼 설정
        binding.btnOpenCamera.apply {
            root.setBackgroundResource(R.drawable.emerald)
            linkBtnText.text = "Open a camera"
            root.setOnClickListener {
                checkAndRequestPermission()
            }
        }

        // Register Details 버튼 설정
        binding.btnRegisterDetails.apply {
            root.setBackgroundResource(R.drawable.emerald)
            linkBtnText.text = "Register details"
            root.setOnClickListener {
                // RegisterDetailsActivity로 데이터 전달
                val intent = Intent(this@CameraActivity, RegisterDetailsActivity::class.java).apply {
                    putExtra("date", date)
                    putExtra("title", title)
                    putExtra("category", category)
                    putExtra("amount", amount)
                }
                startActivity(intent)
            }
        }
    }

    private fun setupImageContainer() {
        binding.imageContainer.setOnClickListener {
            checkAndRequestPermission()
        }
    }

    private fun checkAndRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                showPermissionRationale()
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("갤러리 접근 권한 필요")
            .setMessage("영수증 이미지를 선택하기 위해 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("권한 요청") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    PERMISSION_REQUEST_CODE
                )
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openGallery() {
        getContent.launch("image/*")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(this,
                        "갤러리 접근 권한이 필요합니다.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}