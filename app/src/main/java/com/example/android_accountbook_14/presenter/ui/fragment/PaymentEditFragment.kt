package com.example.android_accountbook_14.presenter.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.transition.Slide
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.databinding.FragmentPaymentEditBinding
import com.example.android_accountbook_14.domain.model.TextLabel
import com.example.android_accountbook_14.presenter.viewmodel.PaymentEditViewModel
import com.example.android_accountbook_14.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentEditFragment: BaseFragment<FragmentPaymentEditBinding>() {
    companion object {
        private const val EXTRA_DATA_LABEL_KEY = "payment"
        fun getInstance(colorLabel: TextLabel?): PaymentEditFragment = PaymentEditFragment().apply {
            arguments = bundleOf(
                EXTRA_DATA_LABEL_KEY to colorLabel
            )
        }
    }

    override val layoutResId: Int
        get() = R.layout.fragment_payment_edit

    private val viewModel: PaymentEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = Slide().apply {
            slideEdge = Gravity.END
            duration = 200
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initData(arguments?.get(EXTRA_DATA_LABEL_KEY) as? TextLabel)

        setToolbar()
        setUpTextInputView()
        setListener()
        observeData()
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

    private fun setUpTextInputView(){
        binding.viewTextInputName.bindEditText(
            getString(R.string.edit_label_title_title),
            getString(R.string.input_text_edit_hint)
        )
    }

    private fun setListener() = with(binding){
        layoutBackground.setOnClickListener {
            setKeyboardShown(false, it)
        }

        viewTextInputName.setTextChangedListener {
            viewModel.textChanged(it)
        }

        btnRegister.setOnClickListener {
            viewModel.btnClick()
        }
    }

    private fun observeData(){
        viewModel.dataLoading.observe(viewLifecycleOwner){
            binding.progress.isVisible = it
        }

        viewModel.modeChanged.observe(viewLifecycleOwner){
            when(it){
                PaymentEditViewModel.Mode.Edit -> {
                    binding.btnRegister.text = getString(R.string.btn_modify)
                    binding.tvToolBarTitle.text = getString(R.string.payment_edit_view_edit_mode_title)
                }
                PaymentEditViewModel.Mode.NewItem -> {
                    binding.btnRegister.text = getString(R.string.btn_register)
                    binding.tvToolBarTitle.text = getString(R.string.payment_edit_view_title)
                }
                else -> {}
            }
        }

        viewModel.title.observe(viewLifecycleOwner){
            binding.viewTextInputName.value = it
        }

        viewModel.btnEnable.observe(viewLifecycleOwner){
            binding.btnRegister.isEnabled = it
        }

        viewModel.viewState.observe(viewLifecycleOwner){
            when(it){
                is PaymentEditViewModel.PaymentEditViewState.FinishEdit -> {
                    requireActivity().onBackPressed()
                }

                is PaymentEditViewModel.PaymentEditViewState.FailEdit -> {
                    showSnackBar(binding.layoutBackground, it.error?.message)
                }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> requireActivity().onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}