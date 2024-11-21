package com.example.moneyhub.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.example.moneyhub.R
import com.example.moneyhub.databinding.ViewCustomGreyFormBinding

class CustomGreyFormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ViewCustomGreyFormBinding =
        ViewCustomGreyFormBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomGreyFormView)
            setIcon(typedArray.getResourceId(R.styleable.CustomGreyFormView_customIcon, R.drawable.email))
            setHint(typedArray.getString(R.styleable.CustomGreyFormView_customHint) ?: "입력할 정보")
            typedArray.recycle()
        }
    }

    fun onTextChanged(listener: (String) -> Unit) {
        binding.editTextOfGreyForm.addTextChangedListener { text ->
            listener(text.toString())
        }
    }

    fun setText(text: String) {
        binding.editTextOfGreyForm.setText(text)
    }

    fun setHint(hint: String) {
        binding.editTextOfGreyForm.hint = hint
    }

    fun setIcon(resourceId: Int) {
        binding.iconOfGreyForm.setImageResource(resourceId)
    }

    fun getText(): String = binding.editTextOfGreyForm.text.toString()
}