package com.example.moneyhub.activity.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.moneyhub.R
import com.example.moneyhub.activity.RegisterDetailsActivity
import com.example.moneyhub.databinding.ActivityCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private val viewModel: CameraViewModel by viewModels()

    // 거래 내역 데이터를 저장할 변수들
    private var date: String? = null
    private var title: String? = null
    private var category: String? = null
    private var amount: Long? = null

    // For OCR
    private val secretKey = "S1JoZ3dFa0RMY1RKTFRXRVl4cVlEV1Byb1BJaElyUXo="

    // 갤러리 실행 결과 처리
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.ivReceipt.setImageURI(it)
            // Once image is set, call OCR API
            val imagePath = getFilePathFromUri(it)
            if (imagePath != null) {
                viewModel.callClovaOcrApi(imagePath, secretKey)
            } else {
                Toast.makeText(this, "이미지 경로를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
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
        observeViewModel()
    }

    // view model에 옮겨야 할 것
    private fun getTransactionData() {
        // 데이터 받기
        date = intent.getStringExtra("transaction_date")
        title = intent.getStringExtra("transaction_title")
        category = intent.getStringExtra("transaction_category")
        amount = intent.getLongExtra("transaction_amount", 0L)

        val transactionId = intent.getStringExtra("transaction_id") ?: ""

        // 텍스트뷰 표시
        binding.tvTransactionInfo.text = """
            거래 ID: $transactionId
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

    private fun observeViewModel() {
        // Observe UI State
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                if (state.isLoading) {
                    // Show loading indicator
                    Toast.makeText(this@CameraActivity, "OCR 처리 중...", Toast.LENGTH_SHORT).show()
                }

                state.error?.let { errorMessage ->
                    // Handle error
                    Toast.makeText(this@CameraActivity, errorMessage, Toast.LENGTH_LONG).show()
                }

                if (state.isSuccess) {
                    // OCR 성공적으로 완료됨
                }
            }
        }

        // observe OCR result
        lifecycleScope.launchWhenStarted {
            viewModel.ocrResult.collect { texts ->
                if (texts.isNotEmpty()) {
                    // OCR 인식된 텍스트를 처리
                    // 여기서는 단순히 Toast로 보여주지만, UI에 표시하거나 다음 동작을 수행할 수 있음
                    Toast.makeText(this@CameraActivity, "OCR 결과: ${texts.joinToString(", ")}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * URI를 실제 파일 경로로 변환하는 예시 메서드.
     * 실제 구현에서는 ContentResolver를 통해 파일을 로컬 캐시에 복사한 후 그 로컬 경로를 반환하거나,
     * MediaStore를 통해 실제 경로를 얻는 등의 추가 작업이 필요할 수 있다.
     */
    private fun getFilePathFromUri(uri: Uri): String? {
        // 예시 구현 (단순화): Uri로부터 InputStream을 얻어 앱의 캐시 디렉토리에 복사
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            tempFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}