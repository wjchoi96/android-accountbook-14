package com.example.android_accountbook_14.presenter.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.databinding.FragmentCalenderBinding
import com.example.android_accountbook_14.presenter.ui.compose.CalenderScreen
import com.example.android_accountbook_14.presenter.viewmodel.CalenderViewModel
import com.example.android_accountbook_14.presenter.viewmodel.MainViewModel
import com.example.android_accountbook_14.util.showSnackBar
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class CalenderFragment: BaseFragment<FragmentCalenderBinding>() {
    override val layoutResId: Int
        get() = R.layout.fragment_calender

    private val viewModel: CalenderViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.composeView.setContent {
            MdcTheme {
                CalenderScreen(
                    viewModel.ledgerListItems,
                    Calendar.getInstance().get(Calendar.MONTH) + 1,
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                    viewModel.showMonth
                )
            }
        }

        setToolbar()
        setListener()
        observeData()
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchList()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden)
            viewModel.fetchList()
    }

    private fun setToolbar() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolBar)
            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    private fun setListener(){
        binding.btnLeftArrow.setOnClickListener {
            mainViewModel.minusMonth()
        }

        binding.btnRightArrow.setOnClickListener {
            mainViewModel.plusMonth()
        }

        binding.tvToolBarTitle.setOnClickListener {
            viewModel.clickTitle()
        }
    }

    private fun observeData() {
        mainViewModel.yearMonthData.observe(viewLifecycleOwner) {
            Timber.d("observe yearMonthData [${it}]")
            viewModel.setDate(it.first, it.second)
        }

        viewModel.dataLoading.observe(viewLifecycleOwner) {
            binding.progress.isVisible = it
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            showSnackBar(binding.layoutBackground, it)
        }

        viewModel.title.observe(viewLifecycleOwner) {
            binding.tvToolBarTitle.text = it
        }

        viewModel.showDatePickDialog.observe(viewLifecycleOwner){
            it.first.setDismissListener { y, m ->
                mainViewModel.setData(y, m) // viewModel 에서 main view model 을 아는걸 원치 않아서 이렇게 작성
            }
            it.first.show(childFragmentManager, it.second)

        }
    }
}