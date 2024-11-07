package com.example.moneyhub
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.moneyhub.databinding.ViewCustomGreyFormBinding

class CustomGreyFormView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewCustomGreyFormBinding =
        ViewCustomGreyFormBinding.inflate(LayoutInflater.from(context), this, true)

    fun setIcon(resourceId: Int) {
        binding.iconOfGreyForm.setImageResource(resourceId)
    }

    fun setHint(text: String) {
        binding.editTextOfGreyForm.hint = text
    }

}