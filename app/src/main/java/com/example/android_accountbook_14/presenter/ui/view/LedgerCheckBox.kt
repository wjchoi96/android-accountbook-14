package com.example.android_accountbook_14.presenter.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.databinding.ViewLedgerCheckBoxBinding

class LedgerCheckBox(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {
    constructor(context: Context): this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    private val binding: ViewLedgerCheckBoxBinding

    private var checkChangedListener: ((Boolean) -> (Unit))? = null

    var title: String? = null
        set(value) {
            field = value
            value?.let { binding.textView.text = it }
        }
    var checked: Boolean = false
        get() = binding.checkBox.isChecked
        set(value){
            field = value
            binding.layoutBackground.isSelected = value
            binding.checkBox.isChecked = value
        }

    init {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_ledger_check_box, this, true)

        attrs.let {
            val typedArray = context.obtainStyledAttributes(it,
                R.styleable.LedgerCheckBox, 0 ,0)
            try {
                typedArray.getBoolean(R.styleable.LedgerCheckBox_ledger_check_box_start_radius, true).let { isStartRadius ->
                    val backgroundRes = if(isStartRadius)
                        R.drawable.bg_ledger_checkbox_left
                    else
                        R.drawable.bg_ledger_checkbox_right
                    binding.layoutBackground.setBackgroundResource(backgroundRes)
                }
                typedArray.getString(R.styleable.LedgerCheckBox_ledger_check_box_title)?.let { title ->
                    binding.textView.text = title
                }
            }catch (e : Exception){
                e.printStackTrace()
            }
            typedArray.recycle()
        }

        setOnClickListener {
            this.isSelected = !this.isSelected
            binding.checkBox.isChecked = isSelected
        }

        binding.checkBox.setOnCheckedChangeListener { compoundButton, b ->
            this.isSelected = b
            checkChangedListener?.invoke(b)
        }
    }

    fun setCheckChangedListener(checkChangedListener: (Boolean)->(Unit)) {
        this.checkChangedListener = checkChangedListener
    }

}