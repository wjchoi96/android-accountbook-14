package com.example.android_accountbook_14.presenter.ui.view

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.databinding.ViewEditTextBinding


class EditTextWithTitleView  @JvmOverloads constructor(
    context: Context,
    attrs : AttributeSet? = null,
    defStyle : Int = 0,
    defStyleRes : Int = 0
) : ConstraintLayout(context, attrs, defStyle, defStyleRes)   {
    private val binding: ViewEditTextBinding
    init {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_edit_text, this, true)
    }

    var title: String
        get() = binding.tvTitle.text.toString()
        set(value) {
            binding.tvTitle.text = value
        }
    var value: String
        get() = binding.etText.text.toString()
        set(value) {
            binding.etText.setText(value)
        }

    fun setTextChangedListener(textChangedListener: (String) -> (Unit)){
        binding.etText.addTextChangedListener{
            textChangedListener(it.toString())
        }
    }

    fun bindEditText(title: String, hint: String){
        binding.tvTitle.text = title
        binding.etText.hint = hint
    }

    fun setInputMode(inputTypeNum: Int) {
        binding.etText.inputType = inputTypeNum
    }
    fun setMaxLength(maxLength: Int) {
        binding.etText.filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }
    fun setSelection(index: Int){
        binding.etText.setSelection(index)
    }

}