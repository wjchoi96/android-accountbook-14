package com.example.android_accountbook_14.presenter.ui.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.transition.Slide
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.databinding.FragmentLedgerEditBinding
import com.example.android_accountbook_14.domain.model.LedgerModel
import com.example.android_accountbook_14.presenter.viewmodel.LedgerEditViewModel
import com.example.android_accountbook_14.util.showSnackBar
import com.example.android_accountbook_14.util.showToast
import com.example.android_accountbook_14.util.toCashString
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.abs


@AndroidEntryPoint
class LedgerEditFragment: BaseFragment<FragmentLedgerEditBinding>() {
    companion object {
        private const val EXTRA_REGISTER_MODE = "register_mode"
        private const val EXTRA_EDIT_MODE = "edit_mode"
        fun getInstance(mode: Int): LedgerEditFragment = LedgerEditFragment().apply {
            arguments = bundleOf(
                EXTRA_REGISTER_MODE to mode
            )
        }
        fun getInstanceEditMode(editItem: LedgerModel): LedgerEditFragment = LedgerEditFragment().apply {
            val mode = if(editItem.price >= 0) 1 else -1
            arguments = bundleOf(
                EXTRA_EDIT_MODE to editItem,
                EXTRA_REGISTER_MODE to mode
            )
        }
    }

    override val layoutResId: Int
        get() = R.layout.fragment_ledger_edit

    private val viewModel: LedgerEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = Slide().apply {
            slideEdge = Gravity.END
            duration = 200
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initView(
            arguments?.getInt(EXTRA_REGISTER_MODE),
            arguments?.getSerializable(EXTRA_EDIT_MODE) as? LedgerModel
        )
        setToolbar()
        setUpTextInputView()
        setListener()
        observeData()
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchLabelsBundle()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            viewModel.fetchLabelsBundle()
        }
    }

    private fun setToolbar(){
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolBar)
            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(true)
            }
        }
        binding.toolBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setUpTextInputView() = with(binding){
        inputTextPrice.apply {
            bindEditText(
                getString(R.string.ledger_register_price),
                getString(R.string.input_text_edit_hint)
            )
            //https://caliou.tistory.com/73
            setTextChangedListener {
                viewModel.inputPrice = it
            }
            setInputMode(InputType.TYPE_CLASS_NUMBER)
            value = viewModel.inputPrice ?: ""
        }

        inputTextContent.apply {
            bindEditText(
                getString(R.string.ledger_register_content),
                getString(R.string.input_text_edit_hint)
            )
            setTextChangedListener { viewModel.inputContent = it }
            value = viewModel.inputContent ?: ""
        }

        // 단순 로딩중 뷰 보여주기용
        labelSpinnerPayment.apply {
            bindSpinner(
                getString(R.string.ledger_register_label),
                getString(R.string.input_text_select_hint),
                emptyList()
            )
        }
        labelSpinnerLabel.apply {
            bindSpinner(
                getString(R.string.ledger_register_payment),
                getString(R.string.input_text_select_hint),
                emptyList()
            )
        }

    }

    private fun setListener() = with(binding){
        layoutBackground.setOnClickListener {
            setKeyboardShown(false, it)
        }

        btnLeft.setOnClickListener {
            setKeyboardShown(false, it)
            if(!btnLeft.isSelected){
                btnLeft.isSelected = !btnLeft.isSelected
                btnRight.isSelected = false
                viewModel.setMode(LedgerEditViewModel.LedgerRegisterMode.Income)
            }
        }

        btnRight.setOnClickListener {
            setKeyboardShown(false, it)
            if(!btnRight.isSelected){
                btnRight.isSelected = !btnRight.isSelected
                btnLeft.isSelected = false
                viewModel.setMode(LedgerEditViewModel.LedgerRegisterMode.Spend)
            }
        }

        tvDate.addTextChangedListener {
            viewModel.inputDate = it.toString()
        }

        layoutDateInput.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            val mYear: Int = c.get(Calendar.YEAR)
            val mMonth: Int = c.get(Calendar.MONTH)
            val mDay: Int = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    viewModel.dateConverter(year, month+1, dayOfMonth, viewModel.getDayOfWeek(year, month+1, dayOfMonth)).let {
                        binding.tvDate.text = it
                    }
                },
                mYear,
                mMonth,
                mDay
            )
            datePickerDialog.show()
        }

        btnRegister.setOnClickListener {
            viewModel.clickRegisterBtn()
        }
    }

    private fun observeData(){
        viewModel.dataLoading.observe(viewLifecycleOwner){
            binding.progress.isVisible = it
        }

        viewModel.isEditMode.observe(viewLifecycleOwner){
            when(it){
                true -> {
                    binding.btnRegister.text = getString(R.string.btn_save)
                    binding.tvToolBarTitle.text = getString(R.string.ledger_edit_title)
                }
                else -> {
                    binding.btnRegister.text = getString(R.string.btn_register)
                    binding.tvToolBarTitle.text = getString(R.string.ledger_register_title)
                }
            }
        }

        viewModel.modeChanged.observe(viewLifecycleOwner){
            when(it){
                LedgerEditViewModel.LedgerRegisterMode.Income -> {
                    binding.btnLeft.isSelected = true
                    binding.labelSpinnerPayment.isVisible = true
                }
                else -> {
                    binding.btnRight.isSelected = true
                    binding.labelSpinnerPayment.isVisible = false
                }
            }
        }

        viewModel.priceText.observe(viewLifecycleOwner){
            binding.inputTextPrice.value = it
            binding.inputTextPrice.setSelection(it.length) // 커서를 제일 끝으로 보냄
        }

        viewModel.labels.observe(viewLifecycleOwner){
            binding.labelSpinnerLabel.apply {
                bindSpinner(
                    getString(R.string.ledger_register_label),
                    getString(R.string.input_text_select_hint),
                    it.map{ it.second }
                ){ viewModel.clickAddLabel() }
                setInputChangedListener { viewModel.inputLabel = it }
                value = viewModel.inputLabel ?: ""
            }
        }

        viewModel.payment.observe(viewLifecycleOwner){
            binding.labelSpinnerPayment.apply {
                bindSpinner(
                    getString(R.string.ledger_register_payment),
                    getString(R.string.input_text_select_hint),
                    it.map{ it.second }
                ){ viewModel.clickAddPayment() }
                setInputChangedListener { viewModel.inputPayment = it }
                value = viewModel.inputPayment ?: ""
            }
        }

        viewModel.editView.observe(viewLifecycleOwner){
            binding.tvDate.text = viewModel.dateConverter(it.year, it.month, it.day, it.dayOfWeek)
            binding.labelSpinnerLabel.value = it.tag
            it.payment?.let { tag ->
                binding.labelSpinnerPayment.value = tag
            }
            binding.inputTextPrice.value = abs(it.price).toCashString()
            it.content?.let { content ->
                binding.inputTextContent.value = content
            }
        }

        viewModel.btnEnable.observe(viewLifecycleOwner){
            binding.btnRegister.isEnabled = it
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){
            showSnackBar(binding.layoutBackground, it)
        }

        viewModel.finishView.observe(viewLifecycleOwner){
            if(!it.isNullOrBlank())
                showToast(it)
            requireActivity().onBackPressed()
        }

        viewModel.navigateToAddPayment.observe(viewLifecycleOwner){
            parentFragmentManager.commit {
                addToBackStack(null)
                hide(this@LedgerEditFragment)
                add(R.id.container_fragment, PaymentEditFragment.getInstance(null))
            }
        }

        viewModel.navigateToAddSpendLabel.observe(viewLifecycleOwner){
            parentFragmentManager.commit {
                addToBackStack(null)
                hide(this@LedgerEditFragment)
                add(R.id.container_fragment, SpendLabelEditFragment.getInstance(null)) // or spend
            }
        }

        viewModel.navigateToAddIncomeLabel.observe(viewLifecycleOwner){
            parentFragmentManager.commit {
                addToBackStack(null)
                hide(this@LedgerEditFragment)
                add(R.id.container_fragment, IncomeLabelEditFragment.getInstance(null)) // or spend
            }
        }

    }

    private fun setKeyboardShown(shown: Boolean, view: View){
        context
        val imm = (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        if(shown) // flag 0은 추가동작이 없음을 알림
            imm?.showSoftInput(view, 0)
        else {
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

}