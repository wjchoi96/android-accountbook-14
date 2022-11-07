package com.example.android_accountbook_14.presenter.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.transition.Slide
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.databinding.FragmentIncomeEditLabelBinding
import com.example.android_accountbook_14.domain.model.ColorLabel
import com.example.android_accountbook_14.presenter.ui.compose.ColorPaletteScreen
import com.example.android_accountbook_14.presenter.viewmodel.IncomeLabelEditViewModel
import com.example.android_accountbook_14.util.ColorUtil
import com.example.android_accountbook_14.util.showSnackBar
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncomeLabelEditFragment : BaseFragment<FragmentIncomeEditLabelBinding>() {
    companion object {
        private const val EXTRA_DATA_LABEL_KEY = "label"
        fun getInstance(colorLabel: ColorLabel?): IncomeLabelEditFragment = IncomeLabelEditFragment().apply {
            arguments = bundleOf(
                EXTRA_DATA_LABEL_KEY to colorLabel
            )
        }
    }

    override val layoutResId: Int
        get() = R.layout.fragment_income_edit_label

    private val viewModel: IncomeLabelEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = Slide().apply {
            slideEdge = Gravity.END
            duration = 200
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initData(arguments?.get(EXTRA_DATA_LABEL_KEY) as? ColorLabel)
        binding.composeView.setContent {
            MdcTheme {
                ColorPaletteScreen(
                    viewModel.selectColorForState,
                    viewModel.colors,
                    viewModel.colorSelectEvent,
                    colorToHexFunc = { ColorUtil.convertToHexString(it) }
                )
            }
        }
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

        viewModel.keyboardShown.observe(viewLifecycleOwner){
            setKeyboardShown(false, binding.layoutBackground)
        }

        viewModel.title.observe(viewLifecycleOwner){
            binding.viewTextInputName.value = it
        }

        viewModel.modeChanged.observe(viewLifecycleOwner){
            when(it){
                IncomeLabelEditViewModel.Mode.Edit -> {
                    binding.btnRegister.text = getString(R.string.btn_modify)
                    binding.tvToolBarTitle.text = getString(R.string.income_edit_view_edit_mode_title)
                }
                IncomeLabelEditViewModel.Mode.NewItem -> {
                    binding.btnRegister.text = getString(R.string.btn_register)
                    binding.tvToolBarTitle.text = getString(R.string.income_edit_view_title)
                }
                else -> {}
            }
        }

        viewModel.btnEnable.observe(viewLifecycleOwner){
            binding.btnRegister.isEnabled = it
        }

        viewModel.viewState.observe(viewLifecycleOwner){
            when(it){
                is IncomeLabelEditViewModel.IncomeLabelEditViewState.FinishEdit -> {
                    requireActivity().onBackPressed()
                }

                is IncomeLabelEditViewModel.IncomeLabelEditViewState.FailEdit -> {
                    showSnackBar(binding.layoutBackground, it.error?.message)
                }
            }
        }
    }

    private fun setKeyboardShown(shown: Boolean, view: View){
        val imm = (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        if(shown) // flag 0은 추가동작이 없음을 알림
            imm?.showSoftInput(view, 0)
        else {
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

}