package com.example.android_accountbook_14.presenter.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android_accountbook_14.databinding.FragmentDialogDatePickBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

class DatePickBottomSheetDialogFragment private constructor(
    private val maxYear: Int,
    private val currentYear: Int,
    private val currentMonth: Int
): BottomSheetDialogFragment() {
    companion object {
        fun get(maxYear: Int, currentYear: Int, currentMonth: Int): DatePickBottomSheetDialogFragment {
            return DatePickBottomSheetDialogFragment(
                maxYear,
                currentYear,
                currentMonth
            )
        }
    }

    private lateinit var binding: FragmentDialogDatePickBinding
    private var dateChangedListener: ((Int, Int) -> (Unit))? = null
    fun setDateChangedListener(dateChangedListener: (Int, Int) -> (Unit)){
        this.dateChangedListener = dateChangedListener
    }

    private var dismissListener: ((Int, Int) -> (Unit))? = null
    fun setDismissListener(dismissListener: (Int, Int) -> (Unit)){
        this.dismissListener = dismissListener
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogDatePickBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDateText(currentYear, currentMonth)
        binding.yearNumberPicker.apply {
            minValue = 1900
            maxValue = maxYear
            value = currentYear
            setOnValueChangedListener { _, prev, value ->
                Timber.d("year changed $prev, $value")
                setDateText(value, binding.monthNumberPicker.value)
                dateChangedListener?.invoke(value, binding.monthNumberPicker.value)
            }
        }
        binding.monthNumberPicker.apply {
            minValue = 1
            maxValue = 12
            value = currentMonth
            setOnValueChangedListener { _, prev, value ->
                Timber.d("month changed $prev, $value")
                setDateText(binding.yearNumberPicker.value, value)
                dateChangedListener?.invoke(binding.yearNumberPicker.value, value)
            }
        }
        this.isCancelable = false

        binding.btnClose.setOnClickListener {
            dismissListener?.invoke(binding.yearNumberPicker.value, binding.monthNumberPicker.value)
            dismiss()
        }
    }
    private fun setDateText(year: Int, month: Int){
        binding.tvDate.text = "${year}년 ${month}월"
    }
}