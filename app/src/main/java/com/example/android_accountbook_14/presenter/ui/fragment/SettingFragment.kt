package com.example.android_accountbook_14.presenter.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.databinding.FragmentSettingBinding
import com.example.android_accountbook_14.presenter.ui.compose.SettingScreen
import com.example.android_accountbook_14.presenter.viewmodel.SettingViewModel
import com.example.android_accountbook_14.util.showSnackBar
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingFragment: BaseFragment<FragmentSettingBinding>() {
    override val layoutResId: Int
        get() = R.layout.fragment_setting

    private val viewModel: SettingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.composeView.setContent {
            MdcTheme {
                SettingScreen(viewModel)
            }
        }
        setToolbar()
        observeData()
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchLabels()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            viewModel.fetchLabels()
        }
    }

    private fun setToolbar(){
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolBar)
            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    private fun observeData(){
        viewModel.dataLoading.observe(viewLifecycleOwner){
            binding.progress.isVisible = it
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){
            showSnackBar(binding.layoutBackground, it)
        }

        viewModel.viewState.observe(viewLifecycleOwner){
            when(it){
                is SettingViewModel.SettingViewState.NavigateToPaymentEditView -> {
                    parentFragmentManager.commit {
                        addToBackStack(null)
                        hide(this@SettingFragment)
                        add(R.id.container_fragment, PaymentEditFragment.getInstance(it.textLabel))
                    }
                }
                is SettingViewModel.SettingViewState.NavigateToSpendLabelEditView -> {
                    parentFragmentManager.commit {
                        addToBackStack(null)
                        hide(this@SettingFragment)
                        add(R.id.container_fragment,
                            SpendLabelEditFragment.getInstance(it.colorLabel)
                        )
                    }
                }
                is SettingViewModel.SettingViewState.NavigateToIncomeLabelEditView -> {
                    parentFragmentManager.commit {
                        addToBackStack(null)
                        hide(this@SettingFragment)
                        add(R.id.container_fragment,
                            IncomeLabelEditFragment.getInstance(it.colorLabel)
                        )
                    }
                }
            }
        }
    }

}