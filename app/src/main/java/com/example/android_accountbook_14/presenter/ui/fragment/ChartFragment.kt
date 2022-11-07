package com.example.android_accountbook_14.presenter.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.databinding.FragmentChartBinding
import com.example.android_accountbook_14.presenter.ui.compose.LedgerMonthTableChartScreen
import com.example.android_accountbook_14.presenter.viewmodel.ChartViewModel
import com.example.android_accountbook_14.presenter.viewmodel.MainViewModel
import com.example.android_accountbook_14.util.showSnackBar
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ChartFragment: BaseFragment<FragmentChartBinding>() {
    override val layoutResId: Int
        get() = R.layout.fragment_chart

    private val viewModel: ChartViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.composeView.setContent {
            MdcTheme {
                LedgerMonthTableChartScreen(viewModel.tableChartData)
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

        viewModel.pieChartData.observe(viewLifecycleOwner){
            setChart(it)
        }

        viewModel.monthSpend.observe(viewLifecycleOwner){
            binding.tvSpend.text = it
        }

        viewModel.emptyData.observe(viewLifecycleOwner){
            binding.composeView.isVisible = it.isNullOrBlank()
            binding.tvEmptyNotify.text = it
        }
    }

    private fun setChart(data: List<Pair<Float, String>>){
        val entries = data.map { PieEntry(it.first) }
        val colors = data.map { Color.parseColor(it.second) }
        val pieDataSet = PieDataSet(entries, "LedgerChart").apply {
            setDrawValues(false) // for remove chart text
        }
        val pieData = PieData(pieDataSet)
        pieDataSet.colors = colors
        binding.pieChart.apply {
            animateX(300)
            description.isEnabled = false // for remove description
            legend.isEnabled = false // for remove legend
            setData(pieData)
        }
    }
}