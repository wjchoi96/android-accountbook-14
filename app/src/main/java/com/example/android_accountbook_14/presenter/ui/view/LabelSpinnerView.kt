package com.example.android_accountbook_14.presenter.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.databinding.ViewItemInputTextSpinnerDropdownBinding
import com.example.android_accountbook_14.databinding.ViewLabelSpinnerBinding
import timber.log.Timber

class LabelSpinnerView @JvmOverloads constructor(
    context: Context,
    attrs : AttributeSet? = null,
    defStyle : Int = 0,
    defStyleRes : Int = 0
) : ConstraintLayout(context, attrs, defStyle, defStyleRes)   {
    private val binding: ViewLabelSpinnerBinding
    init {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_label_spinner, this, true)
    }

    var title: String
        get() = binding.tvTitle.text.toString()
        set(value) {
            binding.tvTitle.text = value
        }
    var value: String
        get() = selectItem ?: ""
        set(value) {
            for((idx, i) in spinnerList.withIndex()){
                if(i == value) {
                    binding.spinner.setSelection(idx)
                }
            }
        }

    private var selectItem : String? = null
    private var spinnerList = listOf<String>()
    private var textChangedListener: ((String)->(Unit))? = null
    private var editEvent: (()->(Unit))? = null

    fun setInputChangedListener(textChangedListener: (String) -> (Unit)){
        this.textChangedListener = textChangedListener
    }

    fun bindSpinner(title: String, hint: String, value: List<String>, editEvent: (()->(Unit))? = null){
        this.title = title
        this.editEvent = editEvent
        setSpinner(value, hint)
    }

    private fun setSpinner(list: List<String>, hint: String? = null){
        Timber.d("set Spinner editEvent => $editEvent")
        val hintList = mutableListOf<String>().apply {
            add(hint ?: resources.getString(R.string.input_text_select_hint))
            addAll(list)
            if(editEvent != null)
                add(resources.getString(R.string.label_spinner_edit))
        }
        spinnerList = hintList
        val arrayAdapter : ArrayAdapter<String> = object: ArrayAdapter<String>(context, R.layout.view_item_input_text_spinner, spinnerList){

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val tv = view as? TextView
                if(position != 0){
                    tv?.setTextColor(resources.getColor(R.color.purple_524D90, null))
                }else{
                    tv?.setTextColor(resources.getColor(R.color.purple_A79FCB, null))
                }
                return tv ?: super.getView(position, convertView, parent)
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val binding = ViewItemInputTextSpinnerDropdownBinding.inflate(LayoutInflater.from(parent.context))
                binding.let {
                    when(position) {
                        0 -> it.tvText.setTextColor(resources.getColor(R.color.purple_A79FCB, null))
                        else -> it.tvText.setTextColor(resources.getColor(R.color.purple_524D90, null))
                    }
                    it.tvText.text = spinnerList[position]
                    it.ivPlus.isVisible = editEvent != null && position == spinnerList.size-1
                }
                return binding.root ?: super.getView(position, convertView, parent)
            }

            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }.apply{
            setDropDownViewResource(R.layout.view_item_input_text_spinner_dropdown)
        }

        binding.spinner.apply {
            setPopupBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.bg_ledger_tag_spinner_popup, null))
            adapter = arrayAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when{
                        editEvent != null && position == spinnerList.size - 1 -> editEvent!!()
                        position == 0 -> {
                            selectItem = null
                            textChangedListener?.invoke(selectItem ?: "")
                        }
                        else -> {
                            selectItem = spinnerList[position]
                            textChangedListener?.invoke(selectItem ?: "")
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }
}