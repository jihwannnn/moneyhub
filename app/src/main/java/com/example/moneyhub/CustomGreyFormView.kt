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

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomGreyFormView,
            0, 0
        ).apply {
            try {
                val iconResId = getResourceId(R.styleable.CustomGreyFormView_customIcon, -1)
                if (iconResId != -1) {
                    binding.iconOfGreyForm.setImageResource(iconResId)
                }

                val hintText = getString(R.styleable.CustomGreyFormView_customHint)
                if (hintText != null) {
                    binding.editTextOfGreyForm.hint = hintText
                }
            } finally {
                recycle()
            }
        }
    }

    fun setIcon(resourceId: Int) {
        binding.iconOfGreyForm.setImageResource(resourceId)
    }

    fun setHint(text: String) {
        binding.editTextOfGreyForm.hint = text
    }

    fun getText(): String {
        return binding.editTextOfGreyForm.text.toString()
    }

}